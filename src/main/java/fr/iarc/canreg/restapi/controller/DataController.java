package fr.iarc.canreg.restapi.controller;


import canreg.common.database.DatabaseRecord;
import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.service.DataService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private DataService dataService;

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
     * @param recordID
     * @return record content, null if not found and locked if there's an exception
     */
    @SneakyThrows
    @GetMapping(path = "/patients/{recordID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Patient> getPatient(@PathVariable("recordID") Integer recordID) {

        try {
            dataService.getPatient(recordID);
        } catch (RecordLockedException e) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
        if ((dataService.getPatient(recordID)) == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping(path = "/tumours", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Tumour> getTumours(@PathVariable("recordID") Integer recordID){

        Tumour tumour = null;
        try {
            tumour = dataService.getTumours(recordID);
        } catch (RecordLockedException e) {
            e.printStackTrace();
        }
        if (tumour != null) {
            return new ResponseEntity(tumour, HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }


    @GetMapping(path = "/sources", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getSources(int recordID) throws RecordLockedException {
        Source source = dataService.getSources(recordID, false);
        if (source == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(source, HttpStatus.OK);
    }


}