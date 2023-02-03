package fr.iarc.canreg.restapi.controller;

import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.model.BulkImportContext;
import fr.iarc.canreg.restapi.model.BulkImportWorker;
import fr.iarc.canreg.restapi.service.BulkImportService;
import fr.iarc.canreg.restapi.service.BulkImportWorkerService;
import fr.iarc.canreg.restapi.service.FileStorageService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/worker")
public class BulkImportWorkerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BulkImportWorkerController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private BulkImportService bulkImportService;

    @Autowired
    private BulkImportWorkerService bulkImportWorkerService;



    // get status
    @GetMapping("/status/{id}")
    public ResponseEntity<String> getBulkImportWorkerStatus(@PathVariable long id) {
        Optional<BulkImportWorker> worker = bulkImportWorkerService.getWorkerById(id);
        return worker.map(bulkImportWorker -> new ResponseEntity<>(bulkImportWorker.getStatus(), HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // get report
    @GetMapping("/report/{id}")
    public ResponseEntity<String> getBulkImportWorkerReport(@PathVariable long id) {
        Optional<BulkImportWorker> worker = bulkImportWorkerService.getWorkerById(id);
        if (worker.isPresent()) {
            BulkImportContext bulkImportContext = worker.get().createBulkImportContextFromWorker();
            String report = null;
            try {
                report = Files.readAllLines(bulkImportContext.getReportFilePath(), StandardCharsets.UTF_8).stream().collect(Collectors.joining("\n"));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while reading the report file", e);
            }
            return new ResponseEntity<>(report, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // scheduled task to import csv file
    @Scheduled(cron = "* 0/5 * * * *")
    public void scheduledFileImport() {

        // load worker for import
        Optional<BulkImportWorker> worker = bulkImportWorkerService.getOldestWorkerWaiting();
        if (worker.isPresent()){
            BulkImportContext bulkImportContext = worker.get().createBulkImportContextFromWorker();
            LOGGER.info(String.format("Starting import %s uploaded by user %s", bulkImportContext.getOriginalFileName(), bulkImportContext.getUserName()));
            worker.get().setStatus(BulkImportWorker.IN_PROGRESS);
            bulkImportWorkerService.createOrUpdate(worker.get());

            // Import
            String report = null;
            try {
                LOGGER.info("Ready to import csv file: {}", bulkImportContext);
                bulkImportService.importFile(bulkImportContext);
                report = Files.readAllLines(bulkImportContext.getReportFilePath(), StandardCharsets.UTF_8)
                        .stream().collect(Collectors.joining("\n"));
                worker.get().setStatus(BulkImportWorker.FINISHED);
                bulkImportWorkerService.createOrUpdate(worker.get());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
            } catch (ServerException e) {
                worker.get().setStatus(BulkImportWorker.ERROR);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while importing the file", e);
            } catch (IOException e) {
                worker.get().setStatus(BulkImportWorker.ERROR);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while reading the file", e);
            }


        }
    }


}
