package fr.iarc.canreg.restapi.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;

public class System {
    @PrimaryKeyJoinColumn
    private Integer SQL211215123533730 ;

    @Column(name = "SQL211215123533731",unique = true)
    private Integer SQL211215123533731 ;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false, unique = true)
    private Integer id;

    @Column(name = "LOOKUP", nullable = false)
    private String lookup ;

    @Column(name = "value")
    private String value ;


}
