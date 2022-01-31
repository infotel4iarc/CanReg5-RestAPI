package fr.iarc.canreg.restapi.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import canreg.common.database.Patient;
import lombok.Getter;

/**
 * Patient DTO.
 */
public class PatientDTO  implements Serializable {

    @Getter
    private final Map<String, Object> variables;

    /**
     * Constructor
     * @param patient patient read in the database
     */
    public PatientDTO(Patient patient) {
        this.variables = new HashMap<>();
        for(String variableName : patient.getVariableNames()) {
            variables.put(variableName, patient.getVariable(variableName));
        }
    }




}
