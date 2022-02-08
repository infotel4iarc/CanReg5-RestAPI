package fr.iarc.canreg.restapi.model;



import canreg.common.database.Source;
import lombok.Getter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SourceDTO implements Serializable {

    @Getter
    private final Map<String,Object> variables;

    /**
     * Constructor
     * @param source source read in the database
     */
    public SourceDTO(Source source) {
        this.variables = new HashMap<>();
        for(String variableName : source.getVariableNames()) {
            variables.put(variableName,source.getVariable(variableName));
        }
    }
}

