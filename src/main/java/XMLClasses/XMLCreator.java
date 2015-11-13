package XMLClasses;

import Common.FolderInfo;
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
    FolderInfo folderInfo;
    public XMLCreator(FolderInfo _folderInfo)
    {
        folderInfo = _folderInfo;
        docFactory = DocumentBuilderFactory.newInstance();
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String createScriptRunningXML( File script, String scriptLanguage,
                                           String resultExtension)
    {
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("RunScript");
        doc.appendChild(rootElement);



        Attr attrScriptName = doc.createAttribute("ScriptName");
        attrScriptName.setValue(script.getName());
        rootElement.setAttributeNode(attrScriptName);

        Attr scriptPath = doc.createAttribute("FilePath");
        scriptPath.setValue(script.getAbsolutePath().substring(folderInfo.folderPath.getAbsolutePath().length()));
        rootElement.setAttributeNode(scriptPath);


        Attr attrScriptVersion = doc.createAttribute("ScriptVersion");
        attrScriptVersion.setValue(scriptLanguage);
        rootElement.setAttributeNode(attrScriptVersion);

        Attr attrResultExtension = doc.createAttribute("ResultExtension");
        attrResultExtension.setValue(resultExtension);
        rootElement.setAttributeNode(attrResultExtension);

//        Attr attrResultFolder = doc.createAttribute("ResultFolder");
//        attrResultFolder.setValue(resultFolder);
//        rootElement.setAttributeNode(attrResultFolder);




        return createStringFromXmlDoc(doc);
    }


    public String createSendMultipleFilesXml(ArrayList<File> filesArray)
    {
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("SendMultipleFiles");
        doc.appendChild(rootElement);



        Element elementFilesArray = doc.createElement("FilesArray");
        for (File file : filesArray)
        {   Element fileElement = doc.createElement("File");

            Attr fileName = doc.createAttribute("FileName");
            fileName.setValue(file.getName());
            fileElement.setAttributeNode(fileName);

            Attr fileSize = doc.createAttribute("FileSize");
            fileSize.setValue(file.length() + "");
            fileElement.setAttributeNode(fileSize);

            Attr filePath = doc.createAttribute("FilePath");
            filePath.setValue(file.getAbsolutePath().substring( folderInfo.folderPath.getAbsolutePath().length()));
            fileElement.setAttributeNode(filePath);



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



    public String createSendFileXMLDoc(File file)
    {
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("SendFile");
        doc.appendChild(rootElement);

        Attr fileName = doc.createAttribute("FileName");
        fileName.setValue(file.getName());
        rootElement.setAttributeNode(fileName);

        Attr fileSize = doc.createAttribute("FileSize");
        fileSize.setValue(file.length()+"");
        rootElement.setAttributeNode(fileSize);

        Attr filePath = doc.createAttribute("FilePath");
        filePath.setValue(file.getAbsolutePath().substring( folderInfo.folderPath.getAbsolutePath().length()));
        rootElement.setAttributeNode(filePath);


        return createStringFromXmlDoc(doc);
    }


    public String sendOsInfo(ArrayList<String> listScriptLanguages, String pcName, String osName)
    {
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("SendOsInfo");
        doc.appendChild(rootElement);

        Attr attrPcName = doc.createAttribute("PcName");
        attrPcName.setValue(pcName);
        rootElement.setAttributeNode(attrPcName);

        Attr attrOsName = doc.createAttribute("OsName");
        attrOsName.setValue(osName);
        rootElement.setAttributeNode(attrOsName);

        Element elementFilesArray = doc.createElement("ScriptLanguages");
        for (String scriptLanguage : listScriptLanguages)
        {    Element fileElement = doc.createElement("ScriptLanguage");
            fileElement.setAttribute("Location", scriptLanguage);
            elementFilesArray.appendChild(fileElement);
        }

        rootElement.appendChild(elementFilesArray);


        return createStringFromXmlDoc(doc);
    }




}
