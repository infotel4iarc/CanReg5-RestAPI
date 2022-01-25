package fr.iarc.canreg.restapi;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Application Properties. 
 */
@Getter
@Setter
@ConfigurationProperties()
public class AppProperties {
    /** The folder with the registry files (xml) */
    private String registryFilesFolder;
    
    /** The registry code. */
    private String registryCode;
    
    /** Path of the configuration file for the CanReg database. */
    private String dbConfigFilePath;
    
    /** Full path of the settings file. */
    private String settingsFilePath;
}
