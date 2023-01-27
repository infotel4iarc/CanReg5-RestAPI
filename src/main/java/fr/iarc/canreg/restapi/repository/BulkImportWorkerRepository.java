package fr.iarc.canreg.restapi.repository;

import fr.iarc.canreg.restapi.model.BulkImportWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BulkImportWorkerRepository extends JpaRepository<BulkImportWorker, Long> {

}
