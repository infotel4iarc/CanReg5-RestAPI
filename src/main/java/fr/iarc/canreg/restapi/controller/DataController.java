package fr.iarc.canreg.restapi.controller;


import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.NotFoundException;
import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.exception.VariableErrorException;
import fr.iarc.canreg.restapi.model.PatientDTO;
import fr.iarc.canreg.restapi.model.SourceDTO;
import fr.iarc.canreg.restapi.model.TumourDTO;
import fr.iarc.canreg.restapi.service.DataService;
import fr.iarc.canreg.restapi.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.Map;

/**
 * Controller to access to the data.
 */
@RestController
@RequestMapping("/api")
public class DataController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);
    public static final String VALIDATION_FAILED = "Validation failed: ";
    public static final String RECORD_IS_LOCKED = "The record is locked";

    @Autowired
    private DataService dataService;

    /**
     * Get all the populations.
     * @return Map id, PopulationDataset
     */
    @GetMapping(path = "/populations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Integer, PopulationDataset>> getPopulations() {
        Map<Integer, PopulationDataset> populations = dataService.getPopulations();
        // populations is never null
        return new ResponseEntity<>(populations, HttpStatus.OK);

    }

    /**
     * Get a population dataset.
     * @param populationID population id.
     * @return record content, null if not found
     */

    @GetMapping(path = "/population/{populationID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationDataset> getPopulation(@PathVariable("populationID") Integer populationID) {
        PopulationDataset population = dataService.getPopulation(populationID);
        if (population == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(population, HttpStatus.OK);
    }


    /**
     * Get a patient
     * @param recordID record id
     * @return record content, null if not found and locked if there's an exception
     */
    @GetMapping(path = "/patients/{recordID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> getPatient(@PathVariable("recordID") Integer recordID) {
        Patient dbRecord;

        dbRecord = dataService.getPatient(recordID);
        if (dbRecord == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found: " + recordID);
        }

        // with recordId and map of variables
        return new ResponseEntity<>(PatientDTO.from(dbRecord, null), HttpStatus.OK);
    }

    /**
     * Get source.
     * @param recordID record id
     * @return record content, null if not found and locked if there's an exception
     */
    @GetMapping(path = "/sources/{recordID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SourceDTO> getSource(@PathVariable("recordID") Integer recordID) {
        Source dbRecord;

        dbRecord = dataService.getSource(recordID);
        if (dbRecord == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(SourceDTO.from(dbRecord, null), HttpStatus.OK);
    }

    /**
     * Get a tumour.
     * @param recordID record id
     * @return record content, null if not found and locked if there is an exception
     */
    @GetMapping(path = "/tumours/{recordID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TumourDTO> getTumour(@PathVariable("recordID") Integer recordID) {
        Tumour dbRecord;

        dbRecord = dataService.getTumour(recordID);
        LOGGER.info("dbRecord : {} ", dbRecord);

        if (dbRecord == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(TumourDTO.from(dbRecord, null), HttpStatus.OK);
    }


    /**
     * Create a patient.
     * @param patient patient
     * @param apiUser user
     * @return PatientDTO or an error
     */
    @PostMapping(path = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> createPatient(@RequestBody PatientDTO patient, @ApiIgnore Principal apiUser) {
        PatientDTO result;
        try {
            result = dataService.savePatient(patient, apiUser);

        } catch (VariableErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, VALIDATION_FAILED + e.getMessage());

        } catch (DuplicateRecordException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /***
     * update a patient.
     * @param patient patient
     * @return PatientDTO
     */
    @PutMapping(path = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> editPatient(@RequestBody PatientDTO patient, @ApiIgnore Principal apiUser) {
        PatientDTO result = null;
        try {
            result = dataService.editPatient(patient, apiUser);
        } catch (ServerException e) {
            if (e.getMessage().contains("multiple")) {
                throw new ResponseStatusException(HttpStatus.MULTIPLE_CHOICES, e.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (RecordLockedException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED, RECORD_IS_LOCKED);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /***
     * Create a tumour.
     * @param tumour tumour
     * @param apiUser api user
     * @return tumourDto
     */
    @PostMapping(path = "/tumours", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TumourDTO> createTumour(@RequestBody TumourDTO tumour, @ApiIgnore Principal apiUser) {
        TumourDTO result = null;
        try {
            result = dataService.saveTumour(tumour, apiUser);

        } catch (VariableErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, VALIDATION_FAILED + e.getMessage());

        } catch (DuplicateRecordException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The tumour already exists");

        } catch (RecordLockedException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED, RECORD_IS_LOCKED);

        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A record is not found");

        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /***
     * Update a tumour.
     * @param tumour tumour
     * @return TumourDTO
     */
    @PutMapping(path = "/tumours", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TumourDTO> editTumour(@RequestBody TumourDTO tumour, @ApiIgnore Principal apiUser) {
        TumourDTO result = null;
        try {
            result = dataService.editTumour(tumour, apiUser);
        } catch (ServerException e) {
            if (e.getMessage().equals(Constants.MULTIPLE_TUMOUR))
                throw new ResponseStatusException(HttpStatus.MULTIPLE_CHOICES, e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ERROR_UPDATE_TUMOUR);
        } catch (NotFoundException e) {
            if (e.getMessage().contains(Constants.PATIENT_NOT_FOUND)) {
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, Constants.PATIENT_NOT_FOUND);
            }
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.TUMOUR_NOT_FOUND);
        } catch (RecordLockedException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED, e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /***
     * Create a source.
     * @param source source
     * @param apiUser api user
     * @return sourceDto
     */
    @PostMapping(path = "/sources", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SourceDTO> createSource(@RequestBody SourceDTO source, @ApiIgnore Principal apiUser) {
        SourceDTO result = null;
        try {
            result = dataService.saveSource(source, apiUser);
            LOGGER.info("source : {} ", result);

        } catch (VariableErrorException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, VALIDATION_FAILED + e.getMessage());

        } catch (DuplicateRecordException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The source already exists");

        } catch (RecordLockedException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED, RECORD_IS_LOCKED);

        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "A record is not found");

        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /***
     * update a source.
     * @param source source
     * @return SourceDTO
     */
    @PutMapping(path = "/sources", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SourceDTO> editSource(@RequestBody SourceDTO source, @ApiIgnore Principal apiUser) {
        SourceDTO result = null;
        try {
            result = dataService.editSource(source, apiUser);
        } catch (ServerException e) {
            if (e.getMessage().equals(Constants.MULTIPLE_SOURCE))
                throw new ResponseStatusException(HttpStatus.MULTIPLE_CHOICES, Constants.MULTIPLE_SOURCE);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.ERROR_UPDATE_SOURCE);
        } catch (NotFoundException e) {
            if (e.getMessage().contains(Constants.TUMOUR_NOT_FOUND))
                throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, Constants.TUMOUR_NOT_FOUND);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, Constants.SOURCE_NOT_FOUND);
        } catch (RecordLockedException e) {
            throw new ResponseStatusException(HttpStatus.LOCKED, RECORD_IS_LOCKED);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    /***
     * Create a population dataset.
     * @param populationDataset populationDataset
     * @return PopulationDataset
     */
    @PostMapping(path = "/populations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationDataset> createPopulation(@RequestBody PopulationDataset populationDataset) {
        PopulationDataset result = null;
        try {
            result = dataService.createPopulation(populationDataset);
            LOGGER.info("PopulationDataset : {} ", result);
        } catch (DuplicateRecordException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The population dataSet already exists");
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /***
     * update a population dataset.
     * @param populationDataset populationDataset
     * @return PopulationDataset
     */
    @PutMapping(path = "/populations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PopulationDataset> editPopulation(@RequestBody PopulationDataset populationDataset) {
        PopulationDataset result = null;
        try {
            result = dataService.editPopulation(populationDataset);
            LOGGER.info("PopulationDataset : {} ", result);
        } catch (ServerException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while deleting a population ");
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The population dataSet does not Exist");
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
