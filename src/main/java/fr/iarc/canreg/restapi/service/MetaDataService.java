package fr.iarc.canreg.restapi.service;

import canreg.common.database.Dictionary;
import canreg.server.database.CanRegDAO;
import fr.iarc.canreg.restapi.exception.ServerException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MetaDataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataService.class);

    /** The folder in which the registry files are stored. */
    @Value("${registryFilesFolder:}")
    private String registryFilesFolder;

    @Autowired
    private CanRegDAO canRegDAO;

    /**
     * Returns the content of the XML file for the registry code.<br>
     * The file "registryCode" + ".xml" is read from the configured folder "registryFilesFolder".<br>
     * Returns null if the file cannot be found.
     * @param registryCode registry code, like "TRN"
     * @return file content or null if not found
     */
    public String getXmlRegistryFileContent(String registryCode) {
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
            throw new ServerException("Metadata file cannot be read: " + e.getMessage() + " : " + file.getName(), e);
        }
        return String.join("\n", lines);
    }

    public Map<Integer, Dictionary> getDictionaries() {
        return canRegDAO.getDictionary();
    }
    
    public Dictionary getDictionary(Integer id) {
        return getDictionaries().get(id);
    }

}
