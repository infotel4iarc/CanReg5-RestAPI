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

    /** Path of the configuration file for the CanReg holding database. */
    private String holdingDbConfigFilePath;

    /** Full path of the settings file. */
    private String settingsFilePath;

    /** Role allowed to connect to Canreg for the rest api */
    private String role;

    /** Upload dir for bulk import. */
    private String bulkUploadDir;

    /** Upload dir for bulk import reports. */
    private String bulkReportDir;

    /** Delete the content of the directory on startup. */
    private boolean bulkUploadDirDeleteOnStartup;

}
