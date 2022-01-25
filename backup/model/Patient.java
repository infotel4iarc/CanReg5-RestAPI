package fr.iarc.canreg.restapi.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Table(name = "Patient")
public class Patient {

    @PrimaryKeyJoinColumn
    private int SQL211215123535300 ;

    @Column(name = "SQL211215123533611", unique = true)
    private int SQL211215123533611 ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PRID", nullable = false, updatable = false, unique = true)
    private Integer prid ;

    @Column(name = "NEXT_RECORD_DB_ID")
    private int next_record_db_id;
    @Column(name = "LAST_RECORD_DB_ID")
    private int last_record_db_id;
    @Column(name = "REGNO", nullable = false)
    private String regno;
    @Column(name = "PERS")
    private String pers;
    @Column(name = "FAMN")
    private String famn;
    @Column(name = "FIRSTN")
    private String firstn;
    @Column(name = "MAIDN")
    private String maidn;
    @Column(name = "SEX")
    private String sex;
    @Column(name = "BIRTHD")
    private String birthd;
    @Column(name = "TRIB")
    private String trib;
    @Column(name = "OCCU")
    private String occu;
    @Column(name = "DLC")
    private String dlc;
    @Column(name = "STAT")
    private String stat;
    @Column(name = "MIDN")
    private String midn;
    @Column(name = "OBSOLETEFLAGPATIENTTABLE")
    private String obsoleteFlagPatientTable;
    @Column(name = "PATIENTRECORDID", nullable = false)
    private String patientRecordid;
    @Column(name = "PATIENTUPDATEDBY")
    private String patientUpdatedby;
    @Column(name = "PATIENTUPDATEDATE")
    private String patientUpdateDate;
    @Column(name = "PATIENTRECORDSTATUS")
    private String  patientRecordStatus;
    @Column(name = "PATIENTCHECKSTATUS")
    private String patientCheckStatus;

    public Integer getPrid() {
        return prid;
    }

    public void setPrid(Integer prid) {
        this.prid = prid;
    }

    public Patient() {
    }

    public int getNext_record_db_id() {
        return next_record_db_id;
    }

    public void setNext_record_db_id(int next_record_db_id) {
        this.next_record_db_id = next_record_db_id;
    }

    public int getLast_record_db_id() {
        return last_record_db_id;
    }

    public void setLast_record_db_id(int last_record_db_id) {
        this.last_record_db_id = last_record_db_id;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getPers() {
        return pers;
    }

    public void setPers(String pers) {
        this.pers = pers;
    }

    public String getFamn() {
        return famn;
    }

    public void setFamn(String famn) {
        this.famn = famn;
    }

    public String getFirstn() {
        return firstn;
    }

    public void setFirstn(String firstn) {
        this.firstn = firstn;
    }

    public String getMaidn() {
        return maidn;
    }

    public void setMaidn(String maidn) {
        this.maidn = maidn;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthd() {
        return birthd;
    }

    public void setBirthd(String birthd) {
        this.birthd = birthd;
    }

    public String getTrib() {
        return trib;
    }

    public void setTrib(String trib) {
        this.trib = trib;
    }

    public String getOccu() {
        return occu;
    }

    public void setOccu(String occu) {
        this.occu = occu;
    }

    public String getDlc() {
        return dlc;
    }

    public void setDlc(String dlc) {
        this.dlc = dlc;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getMidn() {
        return midn;
    }

    public void setMidn(String midn) {
        this.midn = midn;
    }

    public String getObsoleteFlagPatientTable() {
        return obsoleteFlagPatientTable;
    }

    public void setObsoleteFlagPatientTable(String obsoleteFlagPatientTable) {
        this.obsoleteFlagPatientTable = obsoleteFlagPatientTable;
    }

    public String getPatientRecordid() {
        return patientRecordid;
    }

    public void setPatientRecordid(String patientRecordid) {
        this.patientRecordid = patientRecordid;
    }

    public String getPatientUpdatedby() {
        return patientUpdatedby;
    }

    public void setPatientUpdatedby(String patientUpdatedby) {
        this.patientUpdatedby = patientUpdatedby;
    }

    public String getPatientUpdateDate() {
        return patientUpdateDate;
    }

    public void setPatientUpdateDate(String patientUpdateDate) {
        this.patientUpdateDate = patientUpdateDate;
    }

    public String getPatientRecordStatus() {
        return patientRecordStatus;
    }

    public void setPatientRecordStatus(String patientRecordStatus) {
        this.patientRecordStatus = patientRecordStatus;
    }

    public String getPatientCheckStatus() {
        return patientCheckStatus;
    }

    public void setPatientCheckStatus(String patientCheckStatus) {
        this.patientCheckStatus = patientCheckStatus;
    }
}
