package fr.iarc.canreg.restapi.service;

import canreg.common.database.PopulationDataset;
import canreg.common.database.Tumour;
import canreg.server.database.CanRegDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(classes = DataService.class)
@ActiveProfiles("junit")
public class DataServiceTest {


    @MockBean
    private CanRegDAO canRegDAO;
    @Autowired
    private DataService dataService;

    @Test
    void testGetPopulations() {
        Map<Integer, PopulationDataset> populationsMap = new HashMap<>();
        PopulationDataset populationDataset = new PopulationDataset();
        populationDataset.setPopulationDatasetID(1);
        populationsMap.put(1,populationDataset);

        Assertions.assertNotNull(populationDataset);
    }

    @Test
    void testGetTumours() {
        Tumour tumour = new Tumour();
        tumour.getSources();

    }


}
