package fr.iarc.canreg.restapi.service;

import fr.iarc.canreg.restapi.AppProperties;
import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.model.BulkImportContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service to handle the file storage.
 */
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    /**
     * Constructor.
     * @param config appProperties
     */
    public FileStorageService(AppProperties config) {
        this.fileStorageLocation = Paths.get(config.getBulkUploadDir())
                .toAbsolutePath().normalize();
        try {
            if(config.isBulkUploadDirDeleteOnStartup()) {
                FileUtils.deleteDirectory(this.fileStorageLocation.toFile());
            }
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException e) {
            throw new ServerException("Creation of upload dir failed.", e);
        }
    }

    /**
     * Store a file in the upload dir.
     * @param multipartFile multipartFile
     * @param userId user id
     * @return file path
     */
    public BulkImportContext storeFile(MultipartFile multipartFile, String userId) {
        String originalFileName = checkInputFileName(multipartFile);
        
        String fileName = "";
        try {

            fileName = userId 
                    + '_' + System.currentTimeMillis() 
                    + "__" + originalFileName;
            
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(userId).resolve(fileName);
            Files.createDirectories(targetLocation.getParent());
            Files.copy(multipartFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return new BulkImportContext(targetLocation, originalFileName);
            
        } catch (IOException ex) {
            throw new ServerException("Error while storing file " + fileName, ex);
        }
    }

    private String checkInputFileName(MultipartFile multipartFile) {
        if(multipartFile == null) {
            throw new IllegalArgumentException("the file is not set");
        }
        String rawName = multipartFile.getOriginalFilename();
        if(rawName == null) {
            throw new IllegalArgumentException("the file name is not set");
        }
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(rawName);
        // Check if the file's name contains invalid characters
        if(originalFileName.contains("..")) {
            throw new IllegalArgumentException("The filename contains invalid characters '..': "
                    + originalFileName);
        }
        return originalFileName;
    }

}
