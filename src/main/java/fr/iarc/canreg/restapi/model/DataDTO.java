package fr.iarc.canreg.restapi.model;

import canreg.common.checks.CheckMessage;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.DatabaseRecord;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Parent DTO for the data of Patient, Tumour and Source.
 */
@Getter
public class DataDTO<T extends DatabaseRecord> implements Serializable {
    /**
     * Map of variables.
     */
    private Map<String, Object> variables;

    /**
     * Constructor
     * @param databaseRecord data read in the database
     * @param warningMessages warning messages                       
     */
    public void fill(T databaseRecord, List<CheckMessage> warningMessages) {
        this.variables = new HashMap<>();
        for(String variableName : databaseRecord.getVariableNames()) {
            Object value = databaseRecord.getVariable(variableName);
            if(value != null) {
                variables.put(variableName, value);
            }
        }
        if(warningMessages != null && !warningMessages.isEmpty()) {
            variables.put(CheckRecordService.VARIABLE_FORMAT_ERRORS, warningMessages);
        }
    }

}
