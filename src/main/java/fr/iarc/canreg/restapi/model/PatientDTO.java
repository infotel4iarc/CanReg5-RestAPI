package fr.iarc.canreg.restapi.model;

import canreg.common.checks.CheckMessage;
import canreg.common.database.Patient;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;

/**
 * Patient DTO.
 */
@Getter
public class PatientDTO extends DataDTO<Patient> implements Serializable {

    /**
     * Builds a PatientDTO.
     * @param patient patient read in the database
     * @param checkMessages warning messages, can be null or empty
     */
    public static PatientDTO from(Patient patient, List<CheckMessage> checkMessages) {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.fill(patient, checkMessages);
        return patientDTO;
    }

}
