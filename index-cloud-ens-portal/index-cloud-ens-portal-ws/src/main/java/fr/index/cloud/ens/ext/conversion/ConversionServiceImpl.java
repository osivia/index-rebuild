package fr.index.cloud.ens.ext.conversion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.portlet.PortletException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.index.cloud.ens.ext.etb.EtablissementService;
import fr.index.cloud.ens.ws.DriveRestController;
import fr.index.cloud.ens.ws.beans.MetadataClassifier;
import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

@Service
public class ConversionServiceImpl implements IConversionService {

    @Autowired
    ConversionRepository conversionRepository;

    /** The patch executor. */
    ExecutorService patchExecutor = null;

    /**
     * Post-construct.
     *
     * 
     */
    @PostConstruct
    public void postConstruct() {
        patchExecutor = Executors.newSingleThreadExecutor();
    }


    /**
     * Pre-destroy.
     */
    @PreDestroy
    public void preDestroy() {
        patchExecutor.shutdown();
    }


    /** portal Logger. */
    private static final Log logger = LogFactory.getLog(ConversionServiceImpl.class);


    /** conversion Logger. */
    protected static final Log conversionlogger = LogFactory.getLog("PORTAL_CONVERSION");

    private static Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    /** Code niveau ramené au MEFSTAT4 */
    private static int LEVEL_MEFSTAT4_LG = 4;

    /** Code matière tronqué */
    private static int SUBJECT_LG = 6;

    /** Code famille */
    private static int SUBJECT_FAMILLE_LG = 2;


    /**
     * Gets the nuxeo controller.
     *
     * @return the nuxeo controller
     */
    private NuxeoController getNuxeoController() {
        NuxeoController nuxeoController = new NuxeoController(DriveRestController.portletContext);
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        return nuxeoController;
    }


    @Override
    public String convert(PortalControllerContext ctx, String docId, String clientId, String field, MetadataClassifier metadata) {

        String resultCode = null;
        
        MetadataClassifier normalizedMetadata = new MetadataClassifier();
        normalizedMetadata.setCodes(metadata.getCodes() != null ? metadata.getCodes() : new ArrayList<String>());
        normalizedMetadata.setName(metadata.getName() != null ? metadata.getName() : "");
      
        

        if (normalizedMetadata.getCodes().size() > 0 || StringUtils.isNotEmpty(normalizedMetadata.getName())) {
            try {
                List<ConversionRecord> records = conversionRepository.getRecords(ctx);

                if (StringUtils.startsWith(clientId, EtablissementService.PRONOTE_CLIENT_PREFIX)) {

                    String etablissement = clientId.substring(EtablissementService.PRONOTE_CLIENT_PREFIX.length());

                    resultCode = convertBatch(records, docId, etablissement, field, normalizedMetadata);
                }

            } catch (PortletException e) {
                logger.error("Technical error in conversion tool ", e);
            }
        }

        return resultCode;
    }


    /**
     * Convert batch (outside request)
     *
     * @param records the records
     * @param docId the doc id
     * @param etablissementId the etablissement id
     * @param field the field
     * @param key the key
     * @param label the label
     * @return the string
     */
    protected String convertBatch(List<ConversionRecord> records, String docId, String etablissementId, String field, MetadataClassifier metadata) {

        String resultCode = null;


        resultCode = convertInternal(records, etablissementId, field, metadata);

        String codes = StringUtils.trimToNull(StringUtils.join(metadata.getCodes(), ","));    
            
        conversionlogger.info(dateFormat.format(new Date()) + ";" + docId + ";" + etablissementId + ";" + field + ";" + codes + ";" + metadata.getName()+ ";"
                + (resultCode != null ? resultCode : ""));

        return resultCode;
    }


    /**
     * Convert internal.
     *
     * @param records the records
     * @param etablissement the etablissement
     * @param field the field
     * @param key the key
     * @param label the label
     * @return the string
     */
    private String convertInternal(List<ConversionRecord> records, String etablissement, String field, MetadataClassifier metadata) {
        String resultCode = null;

        if (records != null) {

            // ETB + CODE
            resultCode = checkCode(records, etablissement, field, metadata.getCodes(), true);
            // ETB + LABEL
            if (resultCode == null)
                resultCode = checkLabel(records, etablissement, field, metadata.getName(), true);

            // NO ETB + CODE
            if (resultCode == null)
                resultCode = checkCode(records, etablissement, field, metadata.getCodes(), false);
            // NO ETB + LABEL
            if (resultCode == null)
                resultCode = checkLabel(records, etablissement, field,  metadata.getName(), false);

        }
        return resultCode;

    }


    /**
     * Check.
     *
     * @param records the records
     * @param etablissement the etablissement
     * @param field the field
     * @param key the key
     * @param label the label
     * @param includeEtb the include etb
     * @param checkCode the check code
     * @return the string
     */
    private String checkCode(List<ConversionRecord> records, String etablissement, String field, List<String> codes, boolean includeEtb) {

        String resultCode = null;
        String famille = null;

        for (String code : codes) {

            if (resultCode == null && StringUtils.isNotEmpty(code)) {

                if ("L".equals(field)) {
                    // On retient les 4 premiers caractères du niveau
                    if (code.length() > LEVEL_MEFSTAT4_LG) {
                        code = code.substring(0, LEVEL_MEFSTAT4_LG);
                    }
                }

                if ("S".equals(field)) {
                    // On supprime les caractères d’espacement en début de code (TrimLeft)
                    code = code.replaceAll("^\\s+", "");
                    // On ne prend que les 6 premiers caractères qui restent
                    if (code.length() > SUBJECT_LG)
                        code = code.substring(SUBJECT_LG);
                    // Si le code comporte moins de 6 caractères, on remplit de ‘0’ le début.
                    if (code.length() < SUBJECT_LG)
                        code = StringUtils.leftPad(code, SUBJECT_LG, "0");
                    // Et enfin on remplace les 2 derniers caractères (sensés correspondre à la modalité d’enseignement) par des ‘0’
                    code = code.substring(0, SUBJECT_LG - 2);
                    code = StringUtils.rightPad(code, SUBJECT_LG, "0");
                    famille = code.substring(0, 2);
                }


                for (ConversionRecord record : records) {
                    if ((!includeEtb && StringUtils.isEmpty(record.getEtablissement()))
                            || (includeEtb && StringUtils.equals(record.getEtablissement(), etablissement))) {

                        if (StringUtils.equals(record.getPublishCode(), code)) {
                            resultCode = record.getResultCode();
                            break;
                        }
                    }
                }

                // Si non trouvé, recherche par famille
                if (resultCode == null && StringUtils.isNotEmpty(famille)) {
                    for (ConversionRecord record : records) {
                        if ((!includeEtb && StringUtils.isEmpty(record.getEtablissement()))
                                || (includeEtb && StringUtils.equals(record.getEtablissement(), etablissement))) {

                            if (StringUtils.equals(record.getPublishCode(), famille)) {
                                resultCode = record.getResultCode();
                                break;
                            }
                        }
                    }
                }
            }
        }

        return resultCode;
    }


    private String checkLabel(List<ConversionRecord> records, String etablissement, String field, String label, boolean includeEtb) {

        String resultCode = null;


        if (StringUtils.isNotBlank(label)) {

            // Remplacer tous les caractères accentués par leur équivalent sans accent
            label = Normalizer.normalize(label, Normalizer.Form.NFD);
            label = label.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

            if ("L".equals(field)) {

                // Suppression des espaces
                label = label.replaceAll("\\s+", "");

                // Passer toutes les lettres en majuscule
                label = label.toUpperCase();
            }

            if ("S".equals(field)) {

                // Passer toutes les lettres en majuscule
                label = label.toUpperCase();

                // Remplacer tous les caractères qui ne sont pas des lettres par des espaces
                label = label.replaceAll("[^A-Za-z]", " ");

                // Supprimer les caractères d’espacement en début et fin de libellé ainsi que les suites d’espaces par un seul.
                label = label.replaceAll("^\\s+", "");
                label = label.replaceAll("\\s+$", "");
                label = StringUtils.normalizeSpace(label);
            }


            for (ConversionRecord record : records) {
                if ((!includeEtb && StringUtils.isEmpty(record.getEtablissement()))
                        || (includeEtb && StringUtils.equals(record.getEtablissement(), etablissement))) {

                    if (StringUtils.equals(record.getPublishLabel(), label)) {
                        resultCode = record.getResultCode();
                        break;
                    }
                }
            }
        }

        return resultCode;
    }


    @Override
    public void applyPatch(PortalControllerContext ctx, File file) throws FileNotFoundException, IOException, MalformedLineException, PortletException {

        List<PatchRecord> patchRecords = checkPatch(ctx, file);
        List<ConversionRecord> conversionRecords = conversionRepository.getRecords(ctx);

        patchExecutor.submit(new PatchCallable(this, getNuxeoController(), patchRecords, conversionRecords));

    }


    @Override
    public List<PatchRecord> checkPatch(PortalControllerContext ctx, File file) throws FileNotFoundException, IOException, MalformedLineException {

        return conversionRepository.extractRecordsFromPatch(ctx, file);
    }


}
