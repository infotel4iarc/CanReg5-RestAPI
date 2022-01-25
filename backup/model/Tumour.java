package fr.iarc.canreg.restapi.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;

public class Tumour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRID", nullable = false, updatable = false, unique = true)
    private int trid;

    @PrimaryKeyJoinColumn
    private int SQL211215123535321 ;

    @Column(name = "SQL211215123533541", unique = true)
    private int SQL211215123533541;

    @ManyToOne
    @JoinColumn(name = "SQL211215123533830" , referencedColumnName = "SQL211215123533611")
    private int SQL211215123533830 ;

    @Column(name = "NEXT_RECORD_DB_ID")
    private int next_record_db_id;

    @Column(name = "LAST_RECORD_DB_ID")
    private int last_record_db_id;

    @Column(name = "RECS", nullable = false)
    private String recs;

    @Column(name = "CHEC")
    private String chec;

    @Column(name = "AGE")
    private int age;

    @Column(name = "ADDR")
    private String addr;

    @Column(name = "INCID")
    private String incid;

    @Column(name = "TOP")
    private String top;

    @Column(name = "MOR")
    private String mor;

    @Column(name = "BEH")
    private String beh;

    @Column(name = "BAS")
    private String bas;

    @Column(name = "I10")
    private String i10;

    @Column(name = "MPCODE")
    private String mpcode;

    @Column(name = "MPSEG")
    private String mpseq;

    @Column(name = "MPTOT")
    private String mptot;

    @Column(name = "UPDATE")
    private String update;

    @Column(name = "ICCC")
    private String icc;

    @Column(name = "OBSOLETEFLAGTUMOURTABLE")
    private String obsoleteFlagTumourTable;

    @Column(name = "TUMOURID", nullable = false)
    private String TUMOURID;

    @Column(name = "PATIENTIDTUMOURTABLE")
    private String patientIdTumourTable;

    @Column(name = "PATIENTRECORDIDTUMOURTABLE")
    private String patientRecordIdTumourTable;

    @Column(name = "TUMOURUPDATEDBY")
    private String tumourUpdateBy;

    @Column(name = "TUMOURUNDUPLICATIONSTATUS")
    private String tumourUnduplicationStatus;



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

    public String getRecs() {
        return recs;
    }

    public void setRecs(String recs) {
        this.recs = recs;
    }

    public String getChec() {
        return chec;
    }

    public void setChec(String chec) {
        this.chec = chec;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getIncid() {
        return incid;
    }

    public void setIncid(String incid) {
        this.incid = incid;
    }

    public String getTop() {
        return top;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public String getMor() {
        return mor;
    }

    public void setMor(String mor) {
        this.mor = mor;
    }

    public String getBeh() {
        return beh;
    }

    public void setBeh(String beh) {
        this.beh = beh;
    }

    public String getBas() {
        return bas;
    }

    public void setBas(String bas) {
        this.bas = bas;
    }

    public String getI10() {
        return i10;
    }

    public void setI10(String i10) {
        this.i10 = i10;
    }

    public String getMpcode() {
        return mpcode;
    }

    public void setMpcode(String mpcode) {
        this.mpcode = mpcode;
    }

    public String getMpseq() {
        return mpseq;
    }

    public void setMpseq(String mpseq) {
        this.mpseq = mpseq;
    }

    public String getMptot() {
        return mptot;
    }

    public void setMptot(String mptot) {
        this.mptot = mptot;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getIcc() {
        return icc;
    }

    public void setIcc(String icc) {
        this.icc = icc;
    }

    public String getObsoleteFlagTumourTable() {
        return obsoleteFlagTumourTable;
    }

    public void setObsoleteFlagTumourTable(String obsoleteFlagTumourTable) {
        this.obsoleteFlagTumourTable = obsoleteFlagTumourTable;
    }

    public String getTUMOURID() {
        return TUMOURID;
    }

    public void setTUMOURID(String TUMOURID) {
        this.TUMOURID = TUMOURID;
    }

    public String getPatientIdTumourTable() {
        return patientIdTumourTable;
    }

    public void setPatientIdTumourTable(String patientIdTumourTable) {
        this.patientIdTumourTable = patientIdTumourTable;
    }

    public String getPatientRecordIdTumourTable() {
        return patientRecordIdTumourTable;
    }

    public void setPatientRecordIdTumourTable(String patientRecordIdTumourTable) {
        this.patientRecordIdTumourTable = patientRecordIdTumourTable;
    }

    public String getTumourUpdateBy() {
        return tumourUpdateBy;
    }

    public void setTumourUpdateBy(String tumourUpdateBy) {
        this.tumourUpdateBy = tumourUpdateBy;
    }

    public String getTumourUnduplicationStatus() {
        return tumourUnduplicationStatus;
    }

    public void setTumourUnduplicationStatus(String tumourUnduplicationStatus) {
        this.tumourUnduplicationStatus = tumourUnduplicationStatus;
    }

}
