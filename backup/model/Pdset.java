package fr.iarc.canreg.restapi.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;

public class Pdset {

    @PrimaryKeyJoinColumn
    private Integer SQL211215123533690;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private Integer id;


    @Column(name = "PDS_ID")
    private Integer Pds_id;

    @Column(name = "AGE_GROUP")
    private Integer age_group;

    @Column(name = "SEX")
    private Integer sex;

    @Column(name = "COUNT")
    private Integer count;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPds_id() {
        return Pds_id;
    }

    public void setPds_id(Integer pds_id) {
        Pds_id = pds_id;
    }

    public Integer getAge_group() {
        return age_group;
    }

    public void setAge_group(Integer age_group) {
        this.age_group = age_group;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
