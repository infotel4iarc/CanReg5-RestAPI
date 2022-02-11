package fr.iarc.canreg.restapi.controller;


import canreg.common.Globals;
import canreg.common.database.DatabaseRecord;
import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.RecordLockedException;
import com.google.gson.Gson;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.NotFoundException;
import fr.iarc.canreg.restapi.model.PatientDTO;
import fr.iarc.canreg.restapi.model.SourceDTO;
import fr.iarc.canreg.restapi.model.TumourDTO;
import fr.iarc.canreg.restapi.service.DataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private DataService dataService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);
    private static final Gson gson = new Gson();
    /**
     * @return Map
     */
    @GetMapping(path = "/populations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Integer, PopulationDataset>> getPopulations() {
        Map<Integer, PopulationDataset> populations = dataService.getPopulations();
        // populations is never null
        return new ResponseEntity<>(populations, HttpStatus.OK);

    }

    /**
     * @param populationID
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
     * @param recordID
     * @return record content, null if not found and locked if there's an exception
     */
    @GetMapping(path = "/patients/{recordID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> getPatient(@PathVariable("recordID") Integer recordID) {
        DatabaseRecord record;

        try {
            record = dataService.getRecord(recordID,  Globals.PATIENT_TABLE_NAME);
        } catch (RecordLockedException e) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // with recordId and map of variables
        return new ResponseEntity<>(new PatientDTO((Patient) record), HttpStatus.OK);
    }

    /**
     * @param recordID
     * @return record content, null if not found and locked if there's an exception
     */
    @GetMapping(path = "/sources/{recordID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SourceDTO> getSource(@PathVariable("recordID") Integer recordID) {
        DatabaseRecord record;

        try {
            record = dataService.getRecord(recordID,  Globals.SOURCE_TABLE_NAME);

        } catch (RecordLockedException e) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(new SourceDTO((Source) record), HttpStatus.OK);
    }

    /**
     * @param recordID
     * @return record content, null if not found and locked if there is an exception
     */
    @GetMapping(path = "/tumours/{recordID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TumourDTO> getTumour(@PathVariable("recordID") Integer recordID) {
        DatabaseRecord record;

        try {
            record =dataService.getRecord(recordID,  Globals.TUMOUR_TABLE_NAME);
            LOGGER.info("record : {} ", record);

        } catch (RecordLockedException e) {
            LOGGER.error("error : ", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.LOCKED);

        }
        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new TumourDTO((Tumour) record),HttpStatus.OK);
    }


    /**
     * Set patient
     *
     * @param patient patient
     * @param apiUser user
     * @return TODO
     */
    @PutMapping(path = "/setPatients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> setPatient(@RequestBody PatientDTO patient, @ApiIgnore Principal apiUser) throws RecordLockedException {
        PatientDTO result;
        try {
            result = dataService.savePatient(patient, apiUser);
            LOGGER.info("patient : {} ", result);
        } catch (RecordLockedException e) {
            LOGGER.error("error : ", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }catch(DuplicateRecordException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /***
     *
     * @param tumour tumour
     * @param apiUser
     * @return tumourDto
     * @throws RecordLockedException
     */
    @PutMapping(path = "/setTumour", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setTumour(@RequestBody TumourDTO tumour, @ApiIgnore Principal apiUser) throws RecordLockedException {
        TumourDTO result = null;
        try {
            result = dataService.saveTumour(tumour, apiUser);
            LOGGER.info("tumour : {} ", result);
        } catch (RecordLockedException e) {
            LOGGER.error("error : ", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }catch(DuplicateRecordException e){
           return   ResponseEntity.status(HttpStatus.CONFLICT).body(gson.toJson(e.getMessage()));
        }catch(NotFoundException e){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(gson.toJson(e.getMessage()));
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    /***
     *
     * @param source source
     * @param apiUser
     * @return sourceDto
     * @throws RecordLockedException
     */
    @PutMapping(path = "/setSource", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity setSource(@RequestBody SourceDTO source, @ApiIgnore Principal apiUser) throws RecordLockedException {
        SourceDTO result = null;
        try {
            result = dataService.saveSource(source, apiUser);
            LOGGER.info("source : {} ", result);
        } catch (RecordLockedException e) {
            LOGGER.error("error : ", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }catch(DuplicateRecordException e){
            return   ResponseEntity.status(HttpStatus.CONFLICT).body(gson.toJson(e.getMessage()));
        }catch(NotFoundException e){
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(gson.toJson(e.getMessage()));
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}
