package fr.iarc.canreg.restapi.controller;


import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.service.DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataController {

    @Autowired
    private DataService dataService;

    @GetMapping(path = "/populations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Integer, PopulationDataset>> getPopulations() {
        Map<Integer, PopulationDataset> populations = dataService.getPopulations();

        if (populations == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(populations, HttpStatus.OK);

    }

    @GetMapping(path = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getPatient() throws RecordLockedException {
        Patient patient = dataService.getPatient(1, false);
        if (patient == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(patient, HttpStatus.OK);

    }

    @GetMapping(path = "/tumours", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTumours(int recordID) throws RecordLockedException {

        Tumour tumour = dataService.getTumours(recordID, false);
        if (tumour != null) {
            return new ResponseEntity(tumour, HttpStatus.OK);
        }
        return new ResponseEntity( HttpStatus.NOT_FOUND);
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
