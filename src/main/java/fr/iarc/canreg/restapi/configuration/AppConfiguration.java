package fr.iarc.canreg.restapi.configuration;

import canreg.client.LocalSettings;
import canreg.common.Tools;
import canreg.common.checks.CheckRecordService;
import canreg.server.database.CanRegDAO;
import canreg.server.management.SystemDescription;
import fr.iarc.canreg.restapi.AppProperties;
import fr.iarc.canreg.restapi.service.HoldingDbHandler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the system.
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class AppConfiguration {

    /**
     * Returns the XML file for the registry code.<br>
     * The file dir is: the configured folder "registryFilesFolder" <br>
     * The file name is: "registryCode" + ".xml"<br>
     * This method does not check if the file exists.
     * @param config yaml application properties
     * @return file object
     */
    @Bean
    public File xmlRegistryFile(AppProperties config) {
        return new File(config.getRegistryFilesFolder(), config.getRegistryCode() + ".xml");
    }

    /**
     * The database properties read in the file configured in "dbConfigFilePath".
     * @param config yaml application properties
     * @return Properties
     * @throws IOException if the file configured in "dbConfigFilePath" cannot be found or read.
     */
    @Bean
    public Properties dbProperties(AppProperties config) throws IOException {
        return loadDBProperties(config.getDbConfigFilePath());
    }

    /**
     * Bean SystemDescription.
     * @param xmlRegistryFile xmlRegistryFile
     * @param config          config
     * @return SystemDescription
     */
    @Bean
    public SystemDescription systemDescription(File xmlRegistryFile, AppProperties config) {
        LocalSettings localSettings = new LocalSettings(new File(config.getSettingsFilePath()));
        Tools.setLocalSettings(localSettings);
        return new SystemDescription(xmlRegistryFile.getAbsolutePath());
    }

    /**
     * Bean CanRegDAO to access to the main database (not a holding DB).
     * @param dbProperties      the database properties
     * @param systemDescription systemDescription
     * @return CanRegDAO
     */
    @Bean
    public CanRegDAO canRegDAO(Properties dbProperties, SystemDescription systemDescription) {
        return new CanRegDAO(systemDescription.getRegistryCode(), systemDescription.getSystemDescriptionDocument(),
                dbProperties);
    }

    /**
     * The handler to access to the holding databases.
     * @param dbProperties      the database properties
     * @param systemDescription the main SystemDescription
     * @return HoldingDbHandler
     */
    @Bean
    public HoldingDbHandler holdingDbHandler(Properties dbProperties, SystemDescription systemDescription) {
        return new HoldingDbHandler(dbProperties, systemDescription);
    }

    /**
     * Service to check a record before saving it.
     * @param canRegDAO main dao
     * @return CheckRecordService
     */
    @Bean
    public CheckRecordService checkRecordService(CanRegDAO canRegDAO) {
        CheckRecordService checkRecordService = new CheckRecordService(canRegDAO);
        // Read the dictionaries
        checkRecordService.setDictionaries(canRegDAO.getDictionary());
        return checkRecordService;
    }

    private Properties loadDBProperties(String filePath) throws IOException {
        Properties props = new Properties();
        try (InputStream dbPropInputStream = Files.newInputStream(new File(filePath).toPath())) {
            props.load(dbPropInputStream);
        }
        return props;
    }

}
