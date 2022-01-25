package fr.iarc.canreg.restapi.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;

public class Dictionnaries  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private int id ;
    
    @PrimaryKeyJoinColumn
    private int SQL211215123533670; 

    @Column(name = "DICTIONARYID")
    private int dictionaryId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "FONT")
    private String font;
    @Column(name = "TYPE")
    private String type;
    @Column(name = "CODELENGTH")
    private int codeLength;
    @Column(name = "CATEGORYDESCLENGTH")
    private int categoryDescLength;
    @Column(name = "FULLDICTCODELENGTH")
    private int fullDictCodeLength;
    @Column(name = "FULLDICTDESCLENGTH")
    private int fullDictDescLength;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(int dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public int getCategoryDescLength() {
        return categoryDescLength;
    }

    public void setCategoryDescLength(int categoryDescLength) {
        this.categoryDescLength = categoryDescLength;
    }

    public int getFullDictCodeLength() {
        return fullDictCodeLength;
    }

    public void setFullDictCodeLength(int fullDictCodeLength) {
        this.fullDictCodeLength = fullDictCodeLength;
    }

    public int getFullDictDescLength() {
        return fullDictDescLength;
    }

    public void setFullDictDescLength(int fullDictDescLength) {
        this.fullDictDescLength = fullDictDescLength;
    }





}
