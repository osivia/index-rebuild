/**
 * 
 */
package fr.index.cloud.ens.directory.person.export.portlet.service;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;
import org.nuxeo.ecm.automation.client.model.PropertyMap;
import org.osivia.portal.api.Constants;
import org.osivia.portal.api.PortalException;
import org.osivia.portal.api.batch.IBatchService;
import org.osivia.portal.api.cache.services.CacheInfo;
import org.osivia.portal.api.context.PortalControllerContext;
import org.osivia.portal.api.directory.v2.model.Person;
import org.osivia.portal.core.cms.CMSException;
import org.osivia.portal.core.cms.CMSItem;
import org.osivia.portal.core.cms.CMSServiceCtx;
import org.osivia.portal.core.cms.ICMSServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import fr.index.cloud.ens.directory.person.export.portlet.commands.GetExportProceduresCommand;
import fr.index.cloud.ens.directory.person.export.portlet.commands.RemoveProcedureCommand;
import fr.index.cloud.ens.directory.person.export.portlet.controller.Export;
import fr.index.cloud.ens.directory.person.export.portlet.controller.PersonExportForm;
import fr.index.cloud.ens.directory.person.export.portlet.controller.Export.ExportStatus;

import fr.toutatice.portail.cms.nuxeo.api.NuxeoController;
import fr.toutatice.portail.cms.nuxeo.api.forms.FormFilterException;
import fr.toutatice.portail.cms.nuxeo.api.forms.IFormsService;
import fr.toutatice.portail.cms.nuxeo.api.services.NuxeoCommandContext;

/**
 * @author Loïc Billon
 *
 */
@Service
public class PersonExportServiceImpl implements PersonExportService {


    /**
     * Application context.
     */
    @Autowired
    private ApplicationContext applicationContext;

	@Autowired
	private IBatchService batchService;
	
	@Autowired
	private IFormsService formService;
	
	@Autowired
	private ICMSServiceLocator locator;

	/* (non-Javadoc)
	 * @see fr.index.cloud.ens.directory.person.export.portlet.controller.PersonExportService#prepareBatch(org.osivia.portal.api.context.PortalControllerContext, java.lang.String)
	 */
	@Override
	public void prepareBatch(PortalControllerContext portalControllerContext, String uid, String userWorkspacePath, PersonExportForm form)
			throws ParseException, PortalException {
		
		
		Map<String, String> variables = new HashMap<>();
		variables.put("userId", uid);

		String taskPath = null;
		
		try {
			variables = formService.start(portalControllerContext, MODEL_ID, variables);
			
			CMSServiceCtx cmsContext = new CMSServiceCtx();
			cmsContext.setPortalControllerContext(portalControllerContext);
						
			CMSItem task = locator.getCMSService().getTask(cmsContext, UUID.fromString(variables.get("uuid")));
			taskPath = task.getCmsPath();

		} catch (FormFilterException e) {
			throw new PortalException(e);
		} catch (CMSException e) {
			throw new PortalException(e);
		}
		
		PersonExportBatch batch = new PersonExportBatch(userWorkspacePath);
		batch.setExportsPath(getExportsPath());
		String batchId = "export"+uid;

		batch.setPortletContext(portalControllerContext.getPortletCtx());
		batch.setTaskPath(taskPath);
		batch.setBatchId(batchId);

		batchService.addBatch(batch);
		
		updateForm(portalControllerContext, form);
	}

	/* (non-Javadoc)
	 * @see fr.index.cloud.ens.directory.person.export.portlet.controller.PersonExportService#getForm(org.osivia.portal.api.context.PortalControllerContext)
	 */
	@Override
	public PersonExportForm getForm(PortalControllerContext pcc) {
		
        // Form
		PersonExportForm form = this.applicationContext.getBean(PersonExportForm.class);
		updateForm(pcc, form);
		return form;
	}
	
	public void updateForm(PortalControllerContext pcc, PersonExportForm form) {
        
        
        // Get logger person
        Person person = (Person) pcc.getRequest().getAttribute(Constants.ATTR_LOGGED_PERSON_2);
		
        NuxeoController nuxeoController = new NuxeoController(pcc.getRequest(), pcc.getResponse(), pcc.getPortletCtx());
        nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
        nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
        
        Documents procedures = (Documents) nuxeoController.executeNuxeoCommand(new GetExportProceduresCommand(person.getUid()));
        
        form.setIsExportRunning(Boolean.FALSE);
        form.setExports(new Hashtable<String, Export>());
        form.setLimitReached(Boolean.FALSE);
		
        int countExports = 0;
        for(Document procedure : procedures) {
        	Date date = procedure.getDate("dc:modified");
        	String currentStep = procedure.getString("pi:currentStep");
        	
        	Export export = new Export();
        	export.setPi(procedure);
        	export.setDate(date);
        	
        	if(currentStep.equals("1")) {
        		export.setStatus(ExportStatus.RUNNING);
        		form.setIsExportRunning(Boolean.TRUE);
        	}
        	else if(currentStep.equals("2") || currentStep.equals("endStep")) {
        		export.setStatus(ExportStatus.DONE);
        		
        		PropertyMap map = procedure.getProperties().getMap("pi:globalVariablesValues");
        		String zipFilename = (String) map.get("zipFilename");

        		export.setZipFilename(getExportsPath() + zipFilename);
        		countExports = countExports +1;
        	}
        	
        	form.getExports().put(procedure.getId(), export);
        	
        }
        if(countExports >= 2) {
        	form.setLimitReached(Boolean.TRUE);
        }
        

	}

	/* (non-Javadoc)
	 * @see fr.index.cloud.ens.directory.person.export.portlet.controller.PersonExportService#remove(org.osivia.portal.api.context.PortalControllerContext, java.lang.String)
	 */
	@Override
	public void remove(PortalControllerContext pcc, Export export, PersonExportForm form) {
		
		
		File file = new File(getExportsPath()+export.getZipFilename());
		file.delete();
		
        NuxeoController nuxeoController = new NuxeoController(pcc.getRequest(), pcc.getResponse(), pcc.getPortletCtx());
		nuxeoController.setAuthType(NuxeoCommandContext.AUTH_TYPE_SUPERUSER);
		nuxeoController.setCacheType(CacheInfo.CACHE_SCOPE_NONE);
		
        nuxeoController.executeNuxeoCommand(new RemoveProcedureCommand(export.getPi()));
        
        updateForm(pcc, form);
	}

	private String getExportsPath() {
		if(System.getProperties().get("exportaccount.path") != null && 
				StringUtils.isNotBlank(System.getProperties().get("exportaccount.path").toString())) {
			return System.getProperties().get("exportaccount.path").toString();
		}
		else return "/tmp/";
	}

}
