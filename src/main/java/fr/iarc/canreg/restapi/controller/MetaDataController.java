package fr.iarc.canreg.restapi.controller;

import canreg.common.database.Dictionary;
import fr.iarc.canreg.restapi.service.MetaDataService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class MetaDataController {
    @Autowired
    private MetaDataService metaDataService;

    /**
     * Returns the XML file for the registry code.<br>
     * @param registryCode registry code, like "TRN"
     * @return file content or null if not found
     */
    @GetMapping(path = "/meta/system/{registryCode}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getMetaData (@PathVariable("registryCode") String registryCode) {
        String fileContent = metaDataService.getXmlRegistryFileContent(registryCode);
        if(fileContent == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }

    @GetMapping(path = "/meta/dictionary/{dictionaryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dictionary> getDictionary(@PathVariable("dictionaryId") Integer dictionaryId) {
        Dictionary dictionary = metaDataService.getDictionary(dictionaryId);
        if(dictionary == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dictionary, HttpStatus.OK);
    }

    @GetMapping(path = "/meta/dictionary/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<Integer, Dictionary>> getDictionaries() {
        Map<Integer, Dictionary> dictionaries = metaDataService.getDictionaries();
        if(dictionaries == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(dictionaries, HttpStatus.OK);
    }


}
