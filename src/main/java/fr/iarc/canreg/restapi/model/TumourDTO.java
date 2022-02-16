package fr.iarc.canreg.restapi.model;


import canreg.common.database.Tumour;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Tumour DTO.
 */
@Getter
public class TumourDTO implements Serializable {


    private  Map<String, Object> variables;

    public TumourDTO() {
    }

    /**
     * Constructor
     * @param tumour tumour read in the database
     */
    public TumourDTO(Tumour tumour) {
        this.variables = new HashMap<>();
        for (String variableName : tumour.getVariableNames()) {
            variables.put(variableName, tumour.getVariable(variableName));

        }
    }


}
