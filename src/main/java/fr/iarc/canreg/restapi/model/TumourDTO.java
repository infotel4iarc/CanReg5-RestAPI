package fr.iarc.canreg.restapi.model;


import canreg.common.database.Tumour;
import lombok.Getter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TumourDTO implements Serializable {

    @Getter
    private final Map<String, Object> variables;


    public TumourDTO(Tumour tumour) {
        this.variables = new HashMap<>();
        for (String variableName : tumour.getVariableNames()) {
            variables.put(variableName, tumour.getVariable(variableName));

        }
    }


}
