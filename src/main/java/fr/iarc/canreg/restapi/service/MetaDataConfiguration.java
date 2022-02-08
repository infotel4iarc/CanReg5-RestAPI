package fr.iarc.canreg.restapi.service;

import canreg.client.LocalSettings;
import canreg.common.Tools;
import canreg.server.CanRegServerImpl;
import canreg.server.database.CanRegDAO;
import canreg.server.management.SystemDescription;
import fr.iarc.canreg.restapi.AppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Configuration class for the system.
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class MetaDataConfiguration {

  /**
   * Returns the XML file for the registry code.<br>
   * The file dir is: the configured folder "registryFilesFolder" <br>
   * The file name is: "registryCode" + ".xml"<br>
   * This method does not check if the file exists.
   *
   * @return file object
   */
  @Bean
  public File xmlRegistryFile(AppProperties config) {
    return new File(config.getRegistryFilesFolder(), config.getRegistryCode() + ".xml");
  }

  /**
   * Bean SystemDescription.
   *
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
   * Bean CanRegDAO.
   *
   * @param config            config
   * @param systemDescription systemDescription
   * @return CanRegDAO
   * @throws IOException if the file configured in "dbConfigFilePath" cannot be found
   */
  @Bean
  public CanRegDAO canRegDAO(AppProperties config, SystemDescription systemDescription) throws IOException {
    Properties dbProperties = loadDBProperties(config.getDbConfigFilePath());
    return new CanRegDAO(config.getRegistryCode(), systemDescription.getSystemDescriptionDocument(), dbProperties);
  }

  @Bean
  public HoldingDbHandler holdingDbHandler(AppProperties config, SystemDescription systemDescription) throws IOException {
    Properties dbProperties = loadDBProperties(config.getDbConfigFilePath());
    return new HoldingDbHandler(null, dbProperties, systemDescription);
  }
  

  private Properties loadDBProperties(String filePath) throws IOException {
    Properties props = new Properties();
    try (InputStream dbPropInputStream = Files.newInputStream(new File(filePath).toPath())) {
      props.load(dbPropInputStream);
    }
    return props;
  }

}
