package fr.iarc.canreg.restapi.model;


import canreg.common.checks.CheckMessage;
import canreg.common.database.Tumour;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;

/**
 * Tumour DTO.
 */
@Getter
public class TumourDTO extends DataDTO<Tumour> implements Serializable {

    /**
     * Builds a TumourDTO.
     * @param tumour tumour read in the database
     * @param checkMessages warning messages, can be null or empty
     */
    public static TumourDTO from(Tumour tumour, List<CheckMessage> checkMessages) {
        TumourDTO tumourDTO = new TumourDTO();
        tumourDTO.fill(tumour, checkMessages);
        return tumourDTO;
    }

}
