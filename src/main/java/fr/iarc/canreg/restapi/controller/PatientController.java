package fr.iarc.canreg.restapi.controller;

import fr.iarc.canreg.restapi.model.Patient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class PatientController {


   // CanRegDAO canRegDAO = new CanRegDAO() ;

    @GetMapping("/patient")
    public List<Patient> getAllPatient() {
        final List<Patient> patientList = new ArrayList<Patient>();
        return patientList;
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable(value = "id") Long id) {

        Optional<Patient> patient = Optional.empty();
        return patient.isPresent() ? new ResponseEntity<Patient>(patient.get(), HttpStatus.OK)
                : new ResponseEntity("No data found", HttpStatus.NOT_FOUND);
    }
}


