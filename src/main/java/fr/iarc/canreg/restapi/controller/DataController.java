package fr.iarc.canreg.restapi.controller;


import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.model.PatientDTO;
import fr.iarc.canreg.restapi.model.SourceDTO;
import fr.iarc.canreg.restapi.model.TumourDTO;
import fr.iarc.canreg.restapi.service.DataService;
import java.security.Principal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private DataService dataService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DataController.class);

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
        Patient record;

        try {
            record = dataService.getPatient(recordID);
        } catch (RecordLockedException e) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // with recordId and map of variables
        return new ResponseEntity<PatientDTO>(new PatientDTO(record), HttpStatus.OK);
    }

    /**
     * @param recordID
     * @return record content, null if not found and locked if there's an exception
     */
    @GetMapping(path = "/sources/{recordID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SourceDTO> getSource(@PathVariable("recordID") Integer recordID) {
        Source record;

        try {
            record = dataService.getSource(recordID);

        } catch (RecordLockedException e) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<SourceDTO>(new SourceDTO(record), HttpStatus.OK);
    }

    /**
     * @param recordID
     * @return record content, null if not found and locked if there is an exception
     */
    @GetMapping(path = "/tumours/{recordID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TumourDTO> getTumour(@PathVariable("recordID") Integer recordID) {
        Tumour record;

        try {
            record = dataService.getTumour(recordID);
            LOGGER.info("record : {} ", record);

        } catch (RecordLockedException e) {
            LOGGER.error("error : ", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.LOCKED);

        }
        if (record == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<TumourDTO>(HttpStatus.OK);
    }


    /**
     * Set patient
     * @param patient patient
     * @param apiUser user
     * @return TODO
     */
    @PostMapping(path = "/setPatients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> setPatient(@RequestBody PatientDTO patient, @ApiIgnore Principal apiUser) {

        int result = dataService.setPatient(patient, apiUser);
        return new ResponseEntity(result, HttpStatus.CREATED);
    }
}
