package fr.iarc.canreg.restapi.model;


import canreg.common.checks.CheckMessage;
import canreg.common.database.Source;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;

/**
 * Source DTO.
 */
@Getter
public class SourceDTO extends DataDTO<Source> implements Serializable {

    /**
     * Builds a SourceDTO.
     * @param source source read in the database
     * @param checkMessages warning messages, can be null or empty
     */
    public static SourceDTO from(Source source, List<CheckMessage> checkMessages) {
        SourceDTO sourceDTO = new SourceDTO();
        sourceDTO.fill(source, checkMessages);
        return sourceDTO;
    }

}

