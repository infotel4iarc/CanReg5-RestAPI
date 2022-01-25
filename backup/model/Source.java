package fr.iarc.canreg.restapi.model;

import javax.persistence.Column;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class Source {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SRID", nullable = false, updatable = false, unique = true)
    private Integer srid;

    @Column(name = "NEXT_RECORD_DB_ID")
    private Integer next_record_db_id;

    @Column(name = "LAST_RECORD_DB_ID")
    private Integer last_record_db_id;

    @Column(name = "HOSP")
    private String hosp ;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "LABNO")
    private String labno ;

    @Column(name = "CASNO")
    private String casno;

    @Column(name = "TUMOURIDSOURCETABLE")
    private String tumourIdSourceTable;

    @Column(name = "SOURCERECORDID")
    private String sourceRecordID ;

    public Integer getSrid() {
        return srid;
    }

    public void setSrid(Integer srid) {
        this.srid = srid;
    }

    public Integer getNext_record_db_id() {
        return next_record_db_id;
    }

    public void setNext_record_db_id(Integer next_record_db_id) {
        this.next_record_db_id = next_record_db_id;
    }

    public Integer getLast_record_db_id() {
        return last_record_db_id;
    }

    public void setLast_record_db_id(Integer last_record_db_id) {
        this.last_record_db_id = last_record_db_id;
    }

    public String getHosp() {
        return hosp;
    }

    public void setHosp(String hosp) {
        this.hosp = hosp;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLabno() {
        return labno;
    }

    public void setLabno(String labno) {
        this.labno = labno;
    }

    public String getCasno() {
        return casno;
    }

    public void setCasno(String casno) {
        this.casno = casno;
    }

    public String getTumourIdSourceTable() {
        return tumourIdSourceTable;
    }

    public void setTumourIdSourceTable(String tumourIdSourceTable) {
        this.tumourIdSourceTable = tumourIdSourceTable;
    }

    public String getSourceRecordID() {
        return sourceRecordID;
    }

    public void setSourceRecordID(String sourceRecordID) {
        this.sourceRecordID = sourceRecordID;
    }
}
