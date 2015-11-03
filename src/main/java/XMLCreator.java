import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Created by dic on 27-10-2015.
 */
public class XMLCreator {

    DocumentBuilderFactory docFactory;
    DocumentBuilder docBuilder;
    public XMLCreator()
    {
        docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String createScriptRunningXML( String scriptName, String scriptVersion,
                                          ArrayList<File> filesArray, String resultExtension)
    {
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Command");
        doc.appendChild(rootElement);

        Attr attrTaskType = doc.createAttribute("TaskType");
        attrTaskType.setValue("RunScript");
        rootElement.setAttributeNode(attrTaskType);

        Attr attrScriptName = doc.createAttribute("ScriptName");
        attrScriptName.setValue(scriptName);
        rootElement.setAttributeNode(attrScriptName);

        Attr attrScriptVersion = doc.createAttribute("ScriptVersion");
        attrScriptVersion.setValue(scriptVersion);
        rootElement.setAttributeNode(attrScriptVersion);

        Attr attrResultExtension = doc.createAttribute("ResultExtension");
        attrResultExtension.setValue(resultExtension);
        rootElement.setAttributeNode(attrResultExtension);

        Element elementFilesArray = doc.createElement("FilesArray");
        for (File file : filesArray)
        {   Element fileElement = doc.createElement("File");
            Attr attrFile = doc.createAttribute("fileName");
            attrFile.setValue(file.getName());

            //System.out.println(file.getName());
            fileElement.setAttributeNode(attrFile);
            elementFilesArray.appendChild(fileElement);
        }
        rootElement.appendChild(elementFilesArray);


        return createStringFromXmlDoc(doc);
    }


    public String createSendFilesXml(ArrayList<File> filesArray)
    {
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("Command");
        doc.appendChild(rootElement);

        Attr attrTaskType = doc.createAttribute("TaskType");
        attrTaskType.setValue("SendFiles");
        rootElement.setAttributeNode(attrTaskType);

        Element elementFilesArray = doc.createElement("FilesArray");
        for (File file : filesArray)
        {   Element fileElement = doc.createElement("File");
            Attr attrFile = doc.createAttribute("FileName");
            attrFile.setValue(file.getName());

            //System.out.println(file.getName());
            fileElement.setAttributeNode(attrFile);


            Attr attrSize = doc.createAttribute("FileSize");
            attrSize.setValue(file.length() + "");
            fileElement.setAttributeNode(attrSize);


            elementFilesArray.appendChild(fileElement);
        }
        rootElement.appendChild(elementFilesArray);
        return createStringFromXmlDoc(doc);
    }


    public String createStringFromXmlDoc(Document doc)
    {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return writer.getBuffer().toString().replaceAll("\n|\r", "");

    }
}
