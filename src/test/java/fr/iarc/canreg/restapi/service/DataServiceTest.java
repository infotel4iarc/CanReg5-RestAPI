package fr.iarc.canreg.restapi.service;

import canreg.common.checks.CheckRecordService;
import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.server.database.CanRegDAO;
import fr.iarc.canreg.restapi.model.PatientDTO;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of DataService.
 */
@ExtendWith(MockitoExtension.class)
class DataServiceTest {

    @InjectMocks
    private DataService dataService;
    @Mock
    private Principal apiUserPrincipal;
    @Mock
    private HoldingDbHandler holdingDbHandler;
    @Mock
    private Patient mockedPatient;
    @Mock
    private PatientDTO patientDTO;
    @Mock
    private CanRegDAO canRegDAO;
    @Mock
    private CheckRecordService checkRecordService;

    @Mock
    DerbySQLIntegrityConstraintViolationException dbException;

    @Test
    void testGetPopulations() {
        Map<Integer, PopulationDataset> populationsMap = new HashMap<>();
        PopulationDataset populationDataset = new PopulationDataset();
        populationDataset.setPopulationDatasetID(1);
        populationsMap.put(1, populationDataset);

        Assertions.assertNotNull(populationDataset);
    }



}

