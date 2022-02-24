package fr.iarc.canreg.restapi.model;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Context to import a file. 
 */
@Getter
@Setter
@ToString
public class BulkImportContext {

    public static final String DATA_PATIENT = "PATIENT";
    public static final String DATA_TUMOUR = "TUMOUR";
    public static final String DATA_SOURCE = "SOURCE";
    public static final String MODE_WRITE = "WRITE";
    public static final String MODE_TEST = "TEST";
    public static final String DELIMITER_COMMA = "COMMA";
    public static final String DELIMITER_TAB = "TAB";

    /** Path of the input file on the filesystem. */
    private Path inputFilePath;

    /** Path of the report file on the filesystem. */
    private Path reportFilePath;

    /** Original file name. */
    private String originalFileName;

    /** Data type. */
    private String dataType;
    
    /** CSV delimiter: comma or tab. */
    private String delimiter;
    
    /** File encoding. */
    private Charset encoding;
    
    /** Import status. */
    private BulkImportBehaviour importBehaviour;

    /** Write in the database if true, test only if false. */
    private boolean write;

    /** Import status. */
    private BulkImportStatus importStatus;

    /** User name. */
    private String userName;
    
    /** Variable names, built from the first line of the import file. */
    private List<String> variableNames = new ArrayList<>();
    
    /** Progress from 0 to 100. */
    private int progress;
    
    /** Number of data lines (excluding the first line). */
    private int numberOfLines;
    
    /** Number of processed lines OK. */
    private int processedLinesOK;

    /** Number of processed lines OK. */
    private int processedLinesKO;

    /**
     * Constructor.
     * @param inputFilePath inputFilePath
     * @param originalFileName originalFileName
     */
    public BulkImportContext(Path inputFilePath, String originalFileName) {
        this.inputFilePath = inputFilePath;
        this.originalFileName = originalFileName;
    }

    /**
     * Add 1 to processedLinesOK.
     * @return processedLinesOK
     */
    public synchronized int incrementCounterOK() {
        processedLinesOK++;
        return processedLinesOK;
    }
    
    /**
     * Add 1 to processedLinesKO.
     * @return processedLinesKO
     */
    public synchronized int incrementCounterKO() {
        processedLinesKO++;
        return processedLinesKO;
    }

    public boolean isDataPatient() {
        return DATA_PATIENT.equals(dataType);
    }
    public boolean isDataTumour() {
        return DATA_TUMOUR.equals(dataType);
    }
    public boolean isDataSource() {
        return DATA_SOURCE.equals(dataType);
    }
}
