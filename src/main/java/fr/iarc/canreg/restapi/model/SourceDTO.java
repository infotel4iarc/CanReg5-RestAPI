package fr.iarc.canreg.restapi.model;


import canreg.common.database.Source;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

@Getter
public class SourceDTO implements Serializable {


    private  Map<String,Object> variables;

    public SourceDTO() {
    }

    /**
     * Constructor
     * @param source source read in the database
     */
    public SourceDTO(Source source) {
        this.variables = new TreeMap<>();
        for(String variableName : source.getVariableNames()) {
            variables.put(variableName,source.getVariable(variableName));
        }
    }
}

