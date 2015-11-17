package Common.XMLClasses;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.Hashtable;

/**
 * Created by dic on 13-11-2015.
 */
public class XMLParser {

    public String[][] parseSendMultipleFiles(String xmlString)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("File");
        String[][] list = new String[nodeList.getLength()][3];
        for (int temp = 0; temp < nodeList.getLength(); temp++) {

            Node nNode = nodeList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE ) {

                Element eElement = (Element) nNode;
                String fileName= eElement.getAttribute("FileName") ;
                String fileSize = eElement.getAttribute("FileSize");
                String filePath = eElement.getAttribute("FilePath");
                list[temp][0] = fileName;
                list[temp][1] = fileSize;
                list[temp][2] = filePath;

                //list[]
            }
        }

        //Node node = document.getElementById("TaskType");
        for (int i=0; i< list.length; i++)
        {
            System.out.println(list[i][0] + ":" + list[i][1] + ":" + list[i][2]  );
        }

        return list;
    }

    public Hashtable parseSendFile(String xmlString)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.getDocumentElement().normalize();
        Element root =  document.getDocumentElement();
        String fileName= root.getAttribute("FileName") ;
        String fileSize = root.getAttribute("FileSize");
        String filePath = root.getAttribute("FilePath");

        System.out.println(root.getNodeName() +   "   :  " + fileName +   "   :  " + fileSize  +   "   :  " + filePath );

        Hashtable hashtable = new Hashtable();
        hashtable.put("FileName", fileName);
        hashtable.put("FileSize", fileSize);
        hashtable.put("FilePath", filePath);

        return hashtable;
    }

    public Hashtable parseScript(String xmlString)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.getDocumentElement().normalize();
        Element root =  document.getDocumentElement();
        String scriptName= root.getAttribute("ScriptName") ;
        String filePath = root.getAttribute("FilePath");
        String resultExtension = root.getAttribute("ResultExtension");
        String scriptVersion = root.getAttribute("ScriptVersion");
        Hashtable hashtable = new Hashtable();
        hashtable.put("ScriptName", scriptName);
        hashtable.put("FilePath", filePath);
        hashtable.put("ResultExtension", resultExtension);
        hashtable.put("ScriptVersion", scriptVersion);
        System.out.println(root.getNodeName() + "   :  " + scriptName + "   :  " + filePath + "   :  " + resultExtension + "   :  " + scriptVersion);


        return hashtable;
    }

    public Hashtable parseOsInfo(String xmlString)
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        Document document = null;
        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        document.getDocumentElement().normalize();
        Element root =  document.getDocumentElement();
        String PcName= root.getAttribute("PcName") ;
        String OsName = root.getAttribute("OsName");
        String resultExtension = root.getAttribute("ResultExtension");
        String scriptVersion = root.getAttribute("ScriptVersion");
        Hashtable hashtable = new Hashtable();
        hashtable.put("PcName", PcName);
        hashtable.put("OsName", OsName);

        NodeList nodeList = document.getElementsByTagName("ScriptLanguage");
        String[] list = new String[nodeList.getLength()];
        for (int temp = 0; temp < nodeList.getLength(); temp++) {

            Node nNode = nodeList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;
                String Location = eElement.getAttribute("Location");
                list[temp] = Location;
                //list[]
            }
        }

        System.out.println("Size before putting it in hashtable: " + list.length);
        hashtable.put("ScriptLanguages", list);

        return hashtable;
    }


}
