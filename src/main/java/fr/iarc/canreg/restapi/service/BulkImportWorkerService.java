package fr.iarc.canreg.restapi.service;

import fr.iarc.canreg.restapi.model.BulkImportWorker;
import fr.iarc.canreg.restapi.repository.BulkImportWorkerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BulkImportWorkerService {

    @Autowired
    private BulkImportWorkerRepository bulkImportWorkerRepository;

    public long createOrUpdate(BulkImportWorker worker){
        return bulkImportWorkerRepository.save(worker).getId();
    }

    public Optional<BulkImportWorker> getWorkerById(Long id){
        return bulkImportWorkerRepository.findById(id);
    }

    public Optional<BulkImportWorker> getOldestWorkerWaiting(){
        List<BulkImportWorker> waitingWorkerList = bulkImportWorkerRepository.findByStatusOrderByCreatedDateAsc(BulkImportWorker.WAITING);
        if(!waitingWorkerList.isEmpty()){
            return Optional.of(waitingWorkerList.get(0));
        }
        return Optional.empty();
    }

}
