package fr.iarc.canreg.restapi.controller;


import static fr.iarc.canreg.restapi.model.BulkImportContext.DATA_PATIENT;
import static fr.iarc.canreg.restapi.model.BulkImportContext.DATA_SOURCE;
import static fr.iarc.canreg.restapi.model.BulkImportContext.DATA_TUMOUR;
import static fr.iarc.canreg.restapi.model.BulkImportContext.DELIMITER_COMMA;
import static fr.iarc.canreg.restapi.model.BulkImportContext.DELIMITER_TAB;
import static fr.iarc.canreg.restapi.model.BulkImportContext.MODE_TEST;
import static fr.iarc.canreg.restapi.model.BulkImportContext.MODE_WRITE;

import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.model.BulkImportBehaviour;
import fr.iarc.canreg.restapi.model.BulkImportContext;
import fr.iarc.canreg.restapi.service.BulkImportService;
import fr.iarc.canreg.restapi.service.FileStorageService;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Controller to access to the data.
 */
@RestController
@RequestMapping("/bulk")
public class BulkImportController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkImportController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private BulkImportService bulkImportService;

    /**
     * Import a csv file with patient data.
     *
     * @param csvFile csv file
     * @param dataType data type: PATIENT or TUMOUR or SOURCE                
     * @param encodingName a valid charset name
     * @param separatorName TAB or COMMA                 
     * @param behaviour see {@link fr.iarc.canreg.restapi.model.BulkImportBehaviour}
     * @param writeOrTest WRITE to write the data, TEST to test only                  
     * @param apiUser user
     * @return PatientDTO or an error
     */
    @PostMapping(path = "/import/{dataType}/{encodingName}/{separatorName}/{behaviour}/{writeOrTest}", 
            produces = MediaType.APPLICATION_JSON_VALUE, 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importCsv(@RequestPart MultipartFile csvFile,
                                    @PathVariable String dataType,
                                    @PathVariable String encodingName,
                                    @PathVariable String separatorName,
                                    @PathVariable String behaviour,
                                    @PathVariable String writeOrTest,
                                    @ApiIgnore Principal apiUser) {

        // Check input parameters
        checkDatatType(dataType);
        BulkImportBehaviour importBehaviour = checkBehaviour(behaviour);
        boolean isWrite = checkWriteMode(writeOrTest);
        String separator = checkSeparator(separatorName);
        Charset encoding = checkEncoding(encodingName);

        // Write the file on disk
        BulkImportContext bulkImportContext = fileStorageService.storeFile(csvFile, apiUser.getName());
        
        // Set the properties
        bulkImportContext.setDataType(dataType);
        bulkImportContext.setEncoding(encoding);
        bulkImportContext.setDelimiter(separator);
        bulkImportContext.setImportBehaviour(importBehaviour);
        bulkImportContext.setWrite(isWrite);
        bulkImportContext.setUserName(apiUser.getName());

        // Import
        String report = null;
        try {
            LOGGER.info("Ready to import csv file: {}", bulkImportContext);
            bulkImportService.importFile(bulkImportContext);
            report = Files.readAllLines(bulkImportContext.getReportFilePath(), StandardCharsets.UTF_8)
                    .stream().collect(Collectors.joining("\n"));
            
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (ServerException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while importing the file", e);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while reading the file", e);
        }

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    private Charset checkEncoding(String encodingName) {
        try {
            return Charset.forName(encodingName);
        } catch (IllegalArgumentException e ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "encodingName is not a valid encoding: "
                    + encodingName);
        }
    }

    private String checkSeparator(String separatorName) {
        String separator = null;
        if(DELIMITER_COMMA.equalsIgnoreCase(separatorName)) {
            separator = ",";
        } else if (DELIMITER_TAB.equalsIgnoreCase(separatorName)) {
            separator = "\t";
        }
        if(separator == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "separatorName must be: "
                    + DELIMITER_COMMA + " or " + DELIMITER_TAB);
        }
        return separator;
    }

    private String checkDatatType(String dataType) {
        if(!DATA_PATIENT.equals(dataType)
            && !DATA_TUMOUR.equals(dataType)
            && !DATA_SOURCE.equals(dataType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "dataType must be: "
                    + DATA_PATIENT + " or " + DATA_TUMOUR + " or " + DATA_SOURCE);
        }
        return dataType;
    }
    
    private BulkImportBehaviour checkBehaviour(String behaviour) {
        BulkImportBehaviour importBehaviour;
        try {
            importBehaviour = BulkImportBehaviour.valueOf(behaviour);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "behaviour must be a valid value, like: " 
                    + Arrays.stream(BulkImportBehaviour.values())
                    .map(BulkImportBehaviour::name).collect(Collectors.joining(" or ")));
        }
        return importBehaviour;
    }

    private boolean checkWriteMode(String writeOrTest) {
        boolean isWrite = MODE_WRITE.equals(writeOrTest);
        if(!isWrite && !MODE_TEST.equals(writeOrTest)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "writeOrTest must be: "
                    + MODE_WRITE + " or " + MODE_TEST);
        }
        return isWrite;
    }
}
