package fr.iarc.canreg.restapi.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bulk_import_worker")
public class BulkImportWorker {

    // ID
    @Id
    @GeneratedValue
    public long id;
    // USER ID
    public long userId;
    // RESULT
    public String result;
    // STATUS
    public String status;
    // MESSAGE
    public String message;






}
