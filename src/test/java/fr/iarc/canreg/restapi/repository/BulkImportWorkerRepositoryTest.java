package fr.iarc.canreg.restapi.repository;


import fr.iarc.canreg.restapi.model.BulkImportWorker;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles({"junit"})
@RunWith(SpringRunner.class)
@DataJpaTest
//@SpringBootTest(classes={BulkImportWorkerRepository.class})
class BulkImportWorkerRepositoryTest {

    @Autowired
    BulkImportWorkerRepository bulkImportWorkerRepository;

    @Test
    void saveTest(){
        BulkImportWorker worker = new BulkImportWorker();
        long workerId = bulkImportWorkerRepository.save(worker).getId();
        assertTrue(bulkImportWorkerRepository.existsById(workerId));
    }

    @Test
    void updateTest(){
        BulkImportWorker worker = new BulkImportWorker();
        long workerId = bulkImportWorkerRepository.save(worker).getId();
        BulkImportWorker workerUpdated = bulkImportWorkerRepository.getById(workerId);
        workerUpdated.setResult("SUCCESS");
        bulkImportWorkerRepository.save(workerUpdated);
        assertTrue(workerUpdated.getResult().equals(bulkImportWorkerRepository.getById(workerId).getResult()));

    }

    @Test
    void findByIdTest(){
        BulkImportWorker worker = new BulkImportWorker();
        long workerId = bulkImportWorkerRepository.save(worker).getId();
        BulkImportWorker worker1 = bulkImportWorkerRepository.getById(workerId);
        assertEquals(worker1, worker);
    }
}
