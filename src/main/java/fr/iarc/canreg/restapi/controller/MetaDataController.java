package fr.iarc.canreg.restapi.controller;

import fr.iarc.canreg.restapi.service.MetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class MetaDataController {
    @Autowired
    private MetaDataService metaDataService;

    @GetMapping("/meta/system")
    public String getMetaData (String registryCode) {

        return "file" ;
    }



}
