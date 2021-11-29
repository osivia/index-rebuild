package fr.index.cloud.ens.ws.check;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GlobalChecker {

    public static void main(String[] args) {

        try {
            String result;

            result = new GlobalChecker().run(GlobalChecker.class.getResourceAsStream("/fr/index/cloud/ens/ws/check/test.xml"));
            System.out.println("check -> " + result);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public String run(InputStream stream) throws Exception {
        String result = "";
        try {

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;

            builder = builderFactory.newDocumentBuilder();

            Document document = builder.parse(stream);
            Element rootElement = document.getDocumentElement();

            // <env>
            Map<String, String> envVar = new HashMap<>();
            NodeList envs = rootElement.getElementsByTagName("properties");
            for (int i = 0; i < envs.getLength(); i++) {
                Element propNode = (Element) envs.item(i);
                if (propNode instanceof Element) {
                    envVar.put(getTagValue(propNode, "name"), getTagValue(propNode, "value"));
                }
            }

            // <hosts>
            Element hosts = (Element) rootElement.getElementsByTagName("hosts").item(0);
            for (int i = 0; i < hosts.getElementsByTagName("host").getLength(); i++) {
                Node node = hosts.getElementsByTagName("host").item(i);
                if (node instanceof Element) {
                    // <host>
                    Element host = (Element) node;
                   

                    String hostName = getTagValue(host, "name");

                    // <tests>
                    Element testNodes = (Element) host.getElementsByTagName("tests").item(0);
                    for (int j = 0; j < testNodes.getElementsByTagName("test").getLength(); j++) {
                        if (testNodes.getElementsByTagName("test").item(j) instanceof Element) {
                            Element testElement = (Element) testNodes.getElementsByTagName("test").item(j);
                            String testResult = runTest(envVar, hostName, testElement);
                            if( !"ok".equals(testResult))   {
                                result += "\n"+hostName+"#"+j+" -> "+ testResult;
                            }
                         }
                    }
                }
            }
        } catch (Exception e) {
            result="ko:"+ e.getMessage();
        }

        if( StringUtils.isEmpty(result))
            result = "ok";
        else
            result = "ko:" + result;
        return result;
    }

    private String runTest(Map<String, String> envVar, String hostName, Element test) {
        
        String testName = getTagValue(test, "name");
        String result = "ko:test not implemented";

        if ("url".equals(testName)) {
            String url = getTagValue(test, "url");
            String regexp = getTagValue(test, "regexp");
            result = new URLChecker(envVar, hostName, url, regexp).check();
        }

        if ("size".equals(testName)) {
            result = new SizeChecker(envVar, hostName).check();
        }

        if ("port".equals(testName)) {
            String remotePort = getTagValue(test, "remotePort");
            String remoteHost = getTagValue(test, "remoteHost");
            result = new PortChecker(envVar, hostName, remoteHost,remotePort).check();
        }
       return result;


    }

    private static String getTagValue(Element element, String tagName) {
        return element.getElementsByTagName(tagName).item(0).getFirstChild().getNodeValue();
    }

}
