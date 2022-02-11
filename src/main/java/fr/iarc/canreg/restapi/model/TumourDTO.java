package fr.iarc.canreg.restapi.model;


import canreg.common.database.Tumour;
import lombok.Getter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TumourDTO implements Serializable {


    private  Map<String, Object> variables;

    public TumourDTO() {

    }
    public TumourDTO(Tumour tumour) {
        this.variables = new HashMap<>();
        for (String variableName : tumour.getVariableNames()) {
            variables.put(variableName, tumour.getVariable(variableName));

        }
    }


}
