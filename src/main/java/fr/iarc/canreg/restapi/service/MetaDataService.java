package fr.iarc.canreg.restapi.service;

import fr.iarc.canreg.restapi.exception.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

@Service
public class MetaDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataService.class);

    /** The folder in which the registry files are stored. */
    @Value("${registryFilesFolder:}")
    private String registryFilesFolder;

    /**
     * Returns the XML file for the registry code.<br>
     * The file "registryCode" + ".xml" is read from the configured folder "registryFilesFolder".<br>
     * Returns null if the file cannot be found.
     * @param registryCode registry code, like "TRN"
     * @return file content or null if not found
     */
    public String getXmlRegistryFile(String registryCode) {
        File file = new File(registryFilesFolder, registryCode + ".xml");

        if (!file.exists()) {
            LOGGER.error("Metadata file cannot be found: {}", file);
            return null;
        }
        List<String> lines = null;

        try {
            lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOGGER.error("Metadata file cannot be read: {}", file, e);
            throw new ServerException("Metadata file cannot be read: " + e.getMessage() + " : " + file.getName());
        }
        return String.join("\n", lines);
    }


    public String getMetaData(String path) {
        try {
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
            Document document = db.parse(file);
            document.getDocumentElement().normalize();
        } catch (IOException e) {
            LOGGER.error("Error reading xml file", e);
            // TODO: action?
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }


}
