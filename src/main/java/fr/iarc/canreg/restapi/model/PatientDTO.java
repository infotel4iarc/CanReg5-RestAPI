package fr.iarc.canreg.restapi.model;

import java.util.Map;

import canreg.common.database.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import canreg.server.database.CanRegDAO;

public class PatientDTO {

    @Autowired
    public CanRegDAO canRegDAO;

    @Autowired
    public Patient patient;
    private final Map<String, Patient> patientMap;

    public PatientDTO(Map<String, Patient> patientMap) {
        this.patientMap = patientMap;
    }

    public Map<String, Patient> getPatientMap() {
        return patientMap;
    }




}
