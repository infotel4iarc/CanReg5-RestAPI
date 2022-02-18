package fr.iarc.canreg.restapi.model;

import canreg.common.Globals;
import canreg.common.checks.CheckMessage;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.DatabaseRecord;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

/**
 * Parent DTO for the data of Patient, Tumour and Source.
 */
@Getter
public class DataDTO<T extends DatabaseRecord> implements Serializable {

    /** 
     * Variables that are cleaned during the fill of a DTO: <br>
     * - Globals.SystemVariableNames: PRID, TRID, NEXT_RECORD_DB_ID, LAST_RECORD_DB_ID
     * - CheckRecordService.VARIABLE_RAW_DATA: raw_data
     * - CheckRecordService.VARIABLE_FORMAT_ERRORS: format_errors
     */
    private static final Set<String> TECHNICAL_VARIABLES = new HashSet<>();
    
    static {
        Arrays.stream(Globals.SystemVariableNames.values()).forEach(n -> TECHNICAL_VARIABLES.add(n.name()));
        TECHNICAL_VARIABLES.add(CheckRecordService.VARIABLE_RAW_DATA);
        TECHNICAL_VARIABLES.add(CheckRecordService.VARIABLE_FORMAT_ERRORS);
    }

    /**
     * Map of variables.
     */
    private Map<String, Object> variables;

    /**
     * Constructor
     * @param databaseRecord  data read in the database
     * @param warningMessages warning messages
     */
    public void fill(T databaseRecord, List<CheckMessage> warningMessages) {
        this.variables = new HashMap<>();
        for (String variableName : databaseRecord.getVariableNames()) {
            if(!isTechnicalVariable(variableName)) {
                Object value = databaseRecord.getVariable(variableName);
                if (value != null) {
                    variables.put(variableName, value);
                }
            }
            // else: just skip the variable
        }
        if (warningMessages != null && !warningMessages.isEmpty()) {
            variables.put(CheckRecordService.VARIABLE_FORMAT_ERRORS, warningMessages);
        }
    }

    /**
     * Remove technical variables.
     */
    public void removeTechnicalVariables() {
        this.variables.keySet().stream()
                .filter(this::isTechnicalVariable)
                .collect(Collectors.toSet())
                .forEach(variableName -> this.variables.remove(variableName));
    }
    
    private boolean isTechnicalVariable(String variableName) {
        return TECHNICAL_VARIABLES.contains(variableName.toLowerCase(Locale.ENGLISH))
                 || TECHNICAL_VARIABLES.contains(variableName.toUpperCase(Locale.ENGLISH));
    }
}
