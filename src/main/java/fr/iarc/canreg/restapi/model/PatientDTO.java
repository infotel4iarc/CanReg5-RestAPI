package fr.iarc.canreg.restapi.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import canreg.common.database.Patient;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

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
        this.variables = new HashMap<>();
        for(String variableName : patient.getVariableNames()) {
            variables.put(variableName, patient.getVariable(variableName));
        }
    }




}
