package fr.iarc.canreg.restapi.service;

import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.CanRegDAO;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.CanRegApiApplication;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.model.PatientDTO;
import fr.iarc.canreg.restapi.model.SourceDTO;
import fr.iarc.canreg.restapi.model.TumourDTO;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CanRegApiApplication.class})
@ContextConfiguration(classes = DataService.class)
@ActiveProfiles("junit")
public class DataServiceTest {

    @Mock
    private Principal apiUserPrincipal;
    @MockBean
    private HoldingDbHandler holdingDbHandler;
    @Mock
    private Patient mockedPatient;
    @Mock
    private PatientDTO patientDTO;
    @MockBean
    private CanRegDAO canRegDAO;
    @Autowired
    private DataService dataService;

    @Test
    void testGetPopulations() {
        Map<Integer, PopulationDataset> populationsMap = new HashMap<>();
        PopulationDataset populationDataset = new PopulationDataset();
        populationDataset.setPopulationDatasetID(1);
        populationsMap.put(1, populationDataset);

        Assertions.assertNotNull(populationDataset);
    }

    @Test
    void testSavePatient() throws RecordLockedException, SQLException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(patient);
        PatientDTO patientDTO = dataService.savePatient(new PatientDTO(patient), apiUserPrincipal);
        Assertions.assertTrue(("Smith").equals(patientDTO.getVariables().get("famn")));
    }

    @Test
    void testSavePatientException() throws RecordLockedException, SQLException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.savePatient(Mockito.any(Patient.class))).thenThrow(DerbySQLIntegrityConstraintViolationException.class);

        DuplicateRecordException exception = Assertions.assertThrows(DuplicateRecordException.class, () -> dataService.savePatient(new PatientDTO(patient), apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Patient already exist"));

    }

    @Test
    void testSavePatientExceptionSQL() throws RecordLockedException, SQLException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.savePatient(Mockito.any(Patient.class))).thenThrow(SQLException.class);

        ServerException exception = Assertions.assertThrows(ServerException.class, () -> dataService.savePatient(new PatientDTO(patient), apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error while saving a Patient"));
    }

    @Test
    void testSaveTumour() throws RecordLockedException, SQLException {

        Tumour tumour = new Tumour();
        tumour.setVariable("trid", 1);
        tumour.setVariable("extent", "RE");
        tumour.setVariable("patientrecordidtumourtable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(tumour);
        TumourDTO tumourDTO = dataService.saveTumour(new TumourDTO(tumour), apiUserPrincipal);
        Assertions.assertTrue(("RE").equals(tumourDTO.getVariables().get("extent")));
    }

    @Test
    void testSaveSource() throws RecordLockedException, SQLException {

        Source source = new Source();
        source.setVariable("sourcerecordid", "2006601301010");
        source.setVariable("source", "016");
        source.setVariable("tumouridsourcetable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(source);
        SourceDTO sourceDTO = dataService.saveSource(new SourceDTO(source), apiUserPrincipal);
        Assertions.assertTrue(("016").equals(sourceDTO.getVariables().get("source")));
    }
}

