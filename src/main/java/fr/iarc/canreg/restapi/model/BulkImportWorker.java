package fr.iarc.canreg.restapi.model;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@Entity
@EqualsAndHashCode
@Table(name = "bulk_import_worker")
public class BulkImportWorker {

    // ID
    @Id
    @GeneratedValue
    private long id;

    // USER ID
    private long userId;

    // RESULT
    private String result;

    // STATUS
    private String status;

    // MESSAGE
    private String message;


    // Bulk import context properties
    /** Path of the input file on the filesystem. */
    private String inputFilePath;

    /** Path of the report file on the filesystem. */
    private String reportFilePath;

    /** Original file name. */
    private String originalFileName;

    /** Data type. */
    private String dataType;

    /** CSV delimiter: comma or tab. */
    private String delimiter;

    /** File encoding. */
    private String encoding;

    /** Import status. */
    private BulkImportBehaviour importBehaviour;

    /** Write in the database if true, test only if false. */
    private boolean write;

    /** Import status. */
    private BulkImportStatus importStatus;

    /** User name. */
    private String userName;

    /** Variable names, built from the first line of the import file. */
    private String variableNames;

    /** Progress from 0 to 100. */
    private int progress;

    /** Number of data lines (excluding the first line). */
    private int numberOfLines;

    /** Number of processed lines OK. */
    private int processedLinesOK;

    /** Number of processed lines OK. */
    private int processedLinesKO;

    @CreatedDate
    private Date createdDate;


    /** WORKER STATUS */
    public static final String WAITING = "WAITING";
    public static final String IN_PROGRESS = "IN_PROGRESS";
    public static final String FINISHED = "FINISHED";
    public static final String ERROR = "ERROR";

    public void setBulkImportContextProperties(BulkImportContext bulkImportContext){
        this.inputFilePath = bulkImportContext.getInputFilePath().toString();
        this.reportFilePath = bulkImportContext.getReportFilePath().toString();
        this.originalFileName = bulkImportContext.getOriginalFileName();
        this.dataType = bulkImportContext.getDataType();
        this.delimiter = bulkImportContext.getDelimiter();
        this.encoding = bulkImportContext.getEncoding().toString();
        this.importBehaviour = bulkImportContext.getImportBehaviour();
        this.importStatus = bulkImportContext.getImportStatus();
        this.userName = bulkImportContext.getUserName();
        this.variableNames = bulkImportContext.getVariableNames().stream().collect(Collectors.joining());
        this.progress = bulkImportContext.getProgress();
        this.numberOfLines = bulkImportContext.getNumberOfLines();
        this.processedLinesOK = bulkImportContext.getProcessedLinesOK();
        this.processedLinesKO = bulkImportContext.getProcessedLinesKO();
    }

    public BulkImportContext createBulkImportContextFromWorker(){
        BulkImportContext importContext = new BulkImportContext(Paths.get(this.inputFilePath), this.originalFileName);
        importContext.setReportFilePath(Paths.get(this.reportFilePath));
        importContext.setDataType(this.dataType);
        importContext.setDelimiter(this.delimiter);
        importContext.setEncoding(Charset.forName(this.encoding));
        importContext.setImportBehaviour(this.importBehaviour);
        importContext.setWrite(this.write);
        importContext.setImportStatus(this.importStatus);
        importContext.setUserName(this.userName);
        importContext.setVariableNames(Arrays. asList(this.variableNames. split(",")));
        importContext.setProgress(this.progress);
        importContext.setNumberOfLines(this.numberOfLines);
        importContext.setProcessedLinesOK(this.processedLinesOK);
        importContext.setProcessedLinesKO(this.processedLinesKO);
        return importContext;
    }

}
