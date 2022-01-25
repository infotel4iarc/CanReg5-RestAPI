package fr.iarc.canreg.restapi.model;


public class Patient {

    private Integer prid ;

    private int next_record_db_id;
    private int last_record_db_id;
    private String regno;
    private String pers;
    private String famn;
    private String firstn;
    private String maidn;
    private String sex;
    private String birthd;
    private String trib;
    private String occu;
    private String dlc;
    private String stat;
    private String midn;
    private String obsoleteFlagPatientTable;
    private String patientRecordid;
    private String patientUpdatedby;
    private String patientUpdateDate;
    private String  patientRecordStatus;
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
