package fr.iarc.canreg.restapi.model;

import canreg.common.database.Patient;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Patient DTO.
 */
@Getter
public class PatientDTO  implements Serializable {


    private  Map<String, Object> variables;

    public PatientDTO() {

    }

    /**
     * Constructor
     * @param patient patient read in the database
     */
    public PatientDTO(Patient patient) {
        this.variables = new TreeMap<>();
        for(String variableName : patient.getVariableNames()) {
            variables.put(variableName, patient.getVariable(variableName));
        }
    }




}
