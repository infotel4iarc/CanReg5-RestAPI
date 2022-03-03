package fr.iarc.canreg.restapi.service;

import canreg.common.Tools;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.DatabaseRecord;
import canreg.common.database.Patient;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.NotFoundException;
import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.exception.VariableErrorException;
import fr.iarc.canreg.restapi.model.BulkImportContext;
import fr.iarc.canreg.restapi.model.DataDTO;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for the bulk import.<br>
 * Stores in memory the current status of import operations.
 */
@Service
public class BulkImportService {
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkImportService.class);

    @Autowired
    private DataService dataService;

    /**
     * Import a csv file.
     * @param importContext BulkImportContext
     */
    public void importFile(BulkImportContext importContext) {

        importContext.setReportFilePath(importContext.getInputFilePath()
                .resolveSibling("report-" + importContext.getOriginalFileName()));
        // Read first line and count the lines.
        prepareRead(importContext);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setDelimiter(importContext.getDelimiter())
                .build();
        try (CSVParser parser =
                     CSVParser.parse(importContext.getInputFilePath(), importContext.getEncoding(), csvFormat);
             BufferedWriter writer = Files.newBufferedWriter(importContext.getReportFilePath(), StandardCharsets.UTF_8)
        ) {
            writer.write("Starting to import patients from " + importContext.getOriginalFileName());
            int lineNumber = 0;
            for (CSVRecord csvRecord : parser) {
                if(lineNumber == 0) {
                    // skip the header
                    lineNumber++;
                    continue;
                }
                StringBuilder trace = new StringBuilder(30);
                DatabaseRecord inputRecord = null;
                if(importContext.isDataPatient()) {
                    inputRecord = new Patient();
                } else if(importContext.isDataTumour()) {
                    inputRecord = new Tumour();
                } else if(importContext.isDataSource()) {
                    inputRecord = new Source();
                }
                if(inputRecord != null) {
                    int index = 0;
                    for (String variableName : importContext.getVariableNames()) {
                        inputRecord.setVariable(variableName, csvRecord.get(index));
                        index++;
                    }

                    saveRecord(importContext, lineNumber, trace, inputRecord);

                    writer.write(trace.toString());
                }

                lineNumber++;
            }
            writer.write("\n\nFinished: "
                    + (importContext.getNumberOfLines() - 1) + " items in input: "        
                    + importContext.getProcessedLinesOK() + " written, "
                    + importContext.getProcessedLinesKO() + " skipped.\n"
            );
        } catch (IOException e) {
            throw new ServerException("Exception while parsing the input file", e);
        }
    }

    private void saveRecord(BulkImportContext importContext, int lineNumber, StringBuilder trace, 
                            DatabaseRecord inputRecord) {
        DataDTO<? extends DatabaseRecord> resultDto = null;
        try {
            trace.append('\n').append(lineNumber).append(": ");
            if(inputRecord instanceof Patient) {
                resultDto = dataService.savePatient((Patient) inputRecord, importContext.getUserName(), 
                        importContext.isWrite());
            } else if(inputRecord instanceof Tumour) {
                resultDto = dataService.saveTumour((Tumour) inputRecord, importContext.getUserName(), 
                        importContext.isWrite());
            } else if(inputRecord instanceof Source) {
                resultDto = dataService.saveSource((Source) inputRecord, importContext.getUserName(), 
                        importContext.isWrite());
            }
            if(resultDto != null) {
                if (importContext.isWrite()) {
                    trace.append("OK");
                } else {
                    trace.append("TEST OK");
                }
                List<String> formatErrors = 
                        (List<String>) resultDto.getVariables().get(CheckRecordService.VARIABLE_FORMAT_ERRORS);
                if(formatErrors != null) {
                    trace.append(": ").append(formatErrors.toString());
                }
                
                importContext.incrementCounterOK();
            }
        } catch (VariableErrorException | DuplicateRecordException | ServerException | RecordLockedException |
                NotFoundException e) {
            trace.append("KO: ").append(e.getMessage());
            importContext.incrementCounterKO();
        } 
    }

    /**
     * Read first line and count the lines.
     * @param importFile import file
     */
    private void prepareRead(BulkImportContext importFile) {
        // read the first line and count the number of lines
        AtomicInteger nbLines = new AtomicInteger();
        List<String> variableNames = new ArrayList<>();
        try (Stream<String> lines = Files.lines(importFile.getInputFilePath())) {
            lines.forEach(line -> {
                if (nbLines.get() == 0) {
                    // first line
                    String[] varNames = line.split(importFile.getDelimiter());
                    if (varNames.length == 0) {
                        throw new IllegalArgumentException("The first lines must contain the variables names");
                    }
                    Arrays.stream(varNames).forEach(s -> {
                        String varName = StringUtils.trimToNull(s);
                        if (varName == null) {
                            throw new IllegalArgumentException("No name of the first line must be empty");
                        }
                        variableNames.add(Tools.toLowerCaseStandardized(varName));
                    });
                }
                nbLines.getAndIncrement();
            });
        } catch (IOException e) {
            throw new ServerException("Exception while reading the input file", e);
        }
        importFile.setNumberOfLines(nbLines.get());
        importFile.setVariableNames(variableNames);
        LOGGER.info("{}: {} lines", importFile.getOriginalFileName(), importFile.getNumberOfLines());
        LOGGER.info("{}: variables = {}", importFile.getOriginalFileName(), importFile.getVariableNames());
    }
}