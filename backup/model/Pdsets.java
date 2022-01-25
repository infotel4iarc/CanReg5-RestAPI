package fr.iarc.canreg.restapi.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;

public class Pdsets {
    @PrimaryKeyJoinColumn
    private int SQL211215123533680;

    @Column(name = "SQL211215123533681", unique = true)
    private int SQL211215123533681;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private int id;

    @Column(name = "PDS_ID")
    private int Pds_id;

    @Column(name = "PDS_NAME")
    private String pds_name;

    @Column(name = "FILTER")
    private String filter;

    @Column(name = "DATE")
    private String date;

    @Column(name = "SOURCE")
    private String source;

    @Column(name = "AGE_GROUP_STRUCTURE")
    private String age_group_structure;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "WORLD_POPULATION_ID ")
    private int world_population_id;

    @Column(name = "WORLD_POPULATION_BOOL ")
    private int world_population_bool;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPds_id() {
        return Pds_id;
    }

    public void setPds_id(int pds_id) {
        Pds_id = pds_id;
    }

    public String getPds_name() {
        return pds_name;
    }

    public void setPds_name(String pds_name) {
        this.pds_name = pds_name;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAge_group_structure() {
        return age_group_structure;
    }

    public void setAge_group_structure(String age_group_structure) {
        this.age_group_structure = age_group_structure;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWorld_population_id() {
        return world_population_id;
    }

    public void setWorld_population_id(int world_population_id) {
        this.world_population_id = world_population_id;
    }

    public int getWorld_population_bool() {
        return world_population_bool;
    }

    public void setWorld_population_bool(int world_population_bool) {
        this.world_population_bool = world_population_bool;
    }
}
