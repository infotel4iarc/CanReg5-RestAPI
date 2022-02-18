package fr.iarc.canreg.restapi.service;

import canreg.common.checks.CheckMessage;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.CanRegDAO;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.NotFoundException;
import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.exception.VariableErrorException;
import fr.iarc.canreg.restapi.model.PatientDTO;
import fr.iarc.canreg.restapi.model.SourceDTO;
import fr.iarc.canreg.restapi.model.TumourDTO;
import java.util.ArrayList;
import java.util.List;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.Principal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    void testSavePatient() throws SQLException, RecordLockedException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");

        Patient patientDb = new Patient();
        patientDb.setVariable("prid", 234);
        patientDb.setVariable("famn", "Smith");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkPatient(Mockito.any(Patient.class))).thenReturn(new ArrayList<>());
        Mockito.when(canRegDAO.savePatient(Mockito.any(Patient.class))).thenReturn(1);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(patientDb);

        PatientDTO patientDTO = dataService.savePatient(PatientDTO.from(patient, null), apiUserPrincipal);

        Mockito.verify(canRegDAO, Mockito.times(1)).savePatient(Mockito.any(Patient.class));
        Assertions.assertEquals("Smith", patientDTO.getVariables().get("famn"));
        Assertions.assertEquals(234, patientDTO.getVariables().get("prid"));
        Assertions.assertNull(patientDTO.getVariables().get(CheckRecordService.VARIABLE_FORMAT_ERRORS));
        Assertions.assertNull(patientDTO.getVariables().get(CheckRecordService.VARIABLE_RAW_DATA));
    }

    @Test
    void testSavePatientWithWarning() throws SQLException, RecordLockedException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");

        Patient patientDb = new Patient();
        patientDb.setVariable("prid", 234);
        patientDb.setVariable("famn", "Smith");

        List<CheckMessage> checkMessages = new ArrayList<>();
        checkMessages.add(new CheckMessage("famn", "", "this variable is mandatory", false));

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkPatient(Mockito.any(Patient.class))).thenReturn(checkMessages);
        Mockito.when(canRegDAO.savePatient(Mockito.any(Patient.class))).thenReturn(1);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(patientDb);

        PatientDTO patientDTO = dataService.savePatient(PatientDTO.from(patient, null), apiUserPrincipal);

        Mockito.verify(canRegDAO, Mockito.times(1)).savePatient(Mockito.any(Patient.class));
        Assertions.assertEquals("Smith", patientDTO.getVariables().get("famn"));
        Assertions.assertEquals(234, patientDTO.getVariables().get("prid"));
        Assertions.assertEquals("[{level='warning', variable='famn', value='', message='this variable is mandatory'}]",
                patientDTO.getVariables().get(CheckRecordService.VARIABLE_FORMAT_ERRORS).toString());
        Assertions.assertNull(patientDTO.getVariables().get(CheckRecordService.VARIABLE_RAW_DATA));
    }

    @Test
    void testSavePatientWithError() throws SQLException, RecordLockedException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");
        patient.setVariable("birthd", "1905-01-20");

        List<CheckMessage> checkMessages = new ArrayList<>();
        checkMessages.add(new CheckMessage("birthd", "1905-01-20", "this date is not a valid date yyyyMMdd", true));

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkPatient(Mockito.any(Patient.class))).thenReturn(checkMessages);

        PatientDTO inputDto = PatientDTO.from(patient, null);
        VariableErrorException exception = Assertions.assertThrows(VariableErrorException.class,
                () -> dataService.savePatient(inputDto, apiUserPrincipal));

        Mockito.verify(canRegDAO, Mockito.times(0)).savePatient(Mockito.any(Patient.class));
        Mockito.verify(canRegDAO, Mockito.times(0)).getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean());
        Assertions.assertEquals("[{level='error', variable='birthd', value='1905-01-20', message='this date is not a valid date yyyyMMdd'}]",
                exception.getMessage());
    }

    @Test
    void testSavePatientException() throws SQLException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.savePatient(Mockito.any(Patient.class))).thenThrow(DerbySQLIntegrityConstraintViolationException.class);

        PatientDTO inputDto = PatientDTO.from(patient, null);
        DuplicateRecordException exception = Assertions.assertThrows(DuplicateRecordException.class,
                () -> dataService.savePatient(inputDto, apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Patient already exists"));

    }

    @Test
    void testSavePatientExceptionSQL() throws SQLException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.savePatient(Mockito.any(Patient.class))).thenThrow(SQLException.class);

        PatientDTO inputDto = PatientDTO.from(patient, null);
        ServerException exception = Assertions.assertThrows(ServerException.class,
                () -> dataService.savePatient(inputDto, apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error while saving a Patient"));
    }

    @Test
    void testSaveTumour() throws RecordLockedException {

        Tumour tumour = new Tumour();
        tumour.setVariable("trid", 1);
        tumour.setVariable("extent", "RE");
        tumour.setVariable("patientrecordidtumourtable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(tumour);
        TumourDTO tumourDTO = dataService.saveTumour(new TumourDTO(tumour), apiUserPrincipal);
        Assertions.assertEquals("RE", tumourDTO.getVariables().get("extent"));
    }

    @Test
    void testSaveTumourExceptionDB_NotFound() throws RecordLockedException, SQLException {

        Tumour tumour = new Tumour();
        tumour.setVariable("trid", 1);
        tumour.setVariable("extent", "RE");
        tumour.setVariable("patientrecordidtumourtable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(dbException.getSQLState()).thenReturn("23503");
        Mockito.when(canRegDAO.saveTumour(Mockito.any(Tumour.class))).thenThrow(dbException);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> dataService.saveTumour(new TumourDTO(tumour), apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Patient not exist :"));

    }

    @Test
    void testSaveTumourExceptionDB_DuplicateKey() throws RecordLockedException, SQLException {

        Tumour tumour = new Tumour();
        tumour.setVariable("trid", 1);
        tumour.setVariable("extent", "RE");
        tumour.setVariable("patientrecordidtumourtable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(dbException.getSQLState()).thenReturn("23500");
        Mockito.when(canRegDAO.saveTumour(Mockito.any(Tumour.class))).thenThrow(dbException);

        DuplicateRecordException exception = Assertions.assertThrows(DuplicateRecordException.class, () -> dataService.saveTumour(new TumourDTO(tumour), apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Tumour already exist :"));
    }

    @Test
    void testSaveTumourExceptionSQL() throws RecordLockedException, SQLException {

        Tumour tumour = new Tumour();
        tumour.setVariable("trid", 1);
        tumour.setVariable("extent", "RE");
        tumour.setVariable("patientrecordidtumourtable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.saveTumour(Mockito.any(Tumour.class))).thenThrow(SQLException.class);

        ServerException exception = Assertions.assertThrows(ServerException.class, () -> dataService.saveTumour(new TumourDTO(tumour), apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error while saving a Tumour"));
    }

    @Test
    void testSaveSource() throws RecordLockedException, SQLException {

        Source source = new Source();
        source.setVariable("srid", "1");
        source.setVariable("sourcerecordid", "2006601301010");
        source.setVariable("source", "016");
        source.setVariable("tumouridsourcetable", "122222222");
        Source sourceDb = new Source();
        source.setVariable("srid", "22");
        sourceDb.setVariable("sourcerecordid", "2006601301010");
        sourceDb.setVariable("source", "016");
        sourceDb.setVariable("tumouridsourcetable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(sourceDb);
        Mockito.verify(canRegDAO, Mockito.times(1)).saveSource(source);
        SourceDTO sourceDTO = dataService.saveSource(new SourceDTO(source), apiUserPrincipal);
        Assertions.assertTrue(("016").equals(sourceDTO.getVariables().get("source")));
        Assertions.assertTrue(("22").equals(sourceDTO.getVariables().get("srid")));

    }

    @Test
    void testSaveSourceExceptionDB_NotFound() throws RecordLockedException, SQLException {

        Source source = new Source();
        source.setVariable("sourcerecordid", "2006601301010");
        source.setVariable("source", "016");
        source.setVariable("tumouridsourcetable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(dbException.getSQLState()).thenReturn("23503");
        Mockito.when(canRegDAO.saveSource(Mockito.any(Source.class))).thenThrow(dbException);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> dataService.saveSource(new SourceDTO(source), apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Tumour not exist :"));

    }

    @Test
    void testSaveSourceExceptionDB_DuplicateKey() throws RecordLockedException, SQLException {

        Source source = new Source();
        source.setVariable("sourcerecordid", "2006601301010");
        source.setVariable("source", "016");
        source.setVariable("tumouridsourcetable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(dbException.getSQLState()).thenReturn("23500");
        Mockito.when(canRegDAO.saveSource(Mockito.any(Source.class))).thenThrow(dbException);

        DuplicateRecordException exception = Assertions.assertThrows(DuplicateRecordException.class, () -> dataService.saveSource(new SourceDTO(source), apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Source already exist :"));
    }

    @Test
    void testSaveSourceExceptionSQL() throws RecordLockedException, SQLException {

        Source source = new Source();
        source.setVariable("sourcerecordid", "2006601301010");
        source.setVariable("source", "016");
        source.setVariable("tumouridsourcetable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.saveSource(Mockito.any(Source.class))).thenThrow(SQLException.class);

        ServerException exception = Assertions.assertThrows(ServerException.class, () -> dataService.saveSource(new SourceDTO(source), apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error while saving a Source"));
    }
}

