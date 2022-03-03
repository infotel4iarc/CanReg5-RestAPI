package fr.iarc.canreg.restapi.service;

import canreg.common.checks.CheckMessage;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.Patient;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.exception.VariableErrorException;
import fr.iarc.canreg.restapi.model.PatientDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of DataService for Patient.
 */
@ExtendWith(MockitoExtension.class)
class DataServicePatientTest extends DataServiceParentForJunit {

    @Test
    void testSavePatient() throws SQLException, RecordLockedException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");

        Patient patientDb = new Patient();
        patientDb.setVariable("prid", 234);
        patientDb.setVariable("famn", "Smith");
        patientDb.setVariable("patientrecordid", "20220001");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkPatient(Mockito.any(Patient.class))).thenReturn(new ArrayList<>());
        Mockito.when(canRegDAO.savePatient(Mockito.any(Patient.class))).thenReturn(1);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(patientDb);

        PatientDTO patientDTO = dataService.savePatient(PatientDTO.from(patient, null), apiUserPrincipal);

        Mockito.verify(canRegDAO, Mockito.times(1)).savePatient(Mockito.any(Patient.class));
        Assertions.assertEquals("Smith", patientDTO.getVariables().get("famn"));
        Assertions.assertEquals("20220001", patientDTO.getVariables().get("patientrecordid"));
        Assertions.assertNull(patientDTO.getVariables().get("prid"));
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
        patientDb.setVariable("patientrecordid", "20220001");

        List<CheckMessage> checkMessages = new ArrayList<>();
        checkMessages.add(new CheckMessage("famn", "", "this variable is mandatory", false));

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkPatient(Mockito.any(Patient.class))).thenReturn(checkMessages);
        Mockito.when(canRegDAO.savePatient(Mockito.any(Patient.class))).thenReturn(1);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(patientDb);

        PatientDTO patientDTO = dataService.savePatient(PatientDTO.from(patient, null), apiUserPrincipal);

        Mockito.verify(canRegDAO, Mockito.times(1)).savePatient(Mockito.any(Patient.class));
        Assertions.assertEquals("Smith", patientDTO.getVariables().get("famn"));
        Assertions.assertEquals("20220001", patientDTO.getVariables().get("patientrecordid"));
        Assertions.assertNull(patientDTO.getVariables().get("prid"));
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
    void testSavePatientExistsWithSameRegno() throws SQLException, RecordLockedException {

        Patient patient = new Patient();
        patient.setVariable("regno", 123);
        patient.setVariable("famn", "Smith");
        patient.setVariable("birthd", "19050120");

        List<CheckMessage> checkMessages = new ArrayList<>();

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkPatient(Mockito.any(Patient.class))).thenReturn(checkMessages);
        Mockito.when(canRegDAO.countPatientByPatientID(Mockito.any(Patient.class))).thenReturn(1);
        Mockito.when(canRegDAO.getPatientIDVariableName()).thenReturn("RegNo");
        
        PatientDTO inputDto = PatientDTO.from(patient, null);
        DuplicateRecordException exception = Assertions.assertThrows(DuplicateRecordException.class,
                () -> dataService.savePatient(inputDto, apiUserPrincipal));

        Mockito.verify(canRegDAO, Mockito.times(0)).savePatient(Mockito.any(Patient.class));
        Mockito.verify(canRegDAO, Mockito.times(0)).getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean());
        Assertions.assertEquals("Patient already exists with the same RegNo",
                exception.getMessage());
    }

    @Test
    void testSavePatientExistsWithSamePatientRecordID() throws SQLException {

        Patient patient = new Patient();
        patient.setVariable("prid", 1);
        patient.setVariable("famn", "Smith");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.savePatient(Mockito.any(Patient.class))).thenThrow(DerbySQLIntegrityConstraintViolationException.class);

        PatientDTO inputDto = PatientDTO.from(patient, null);
        DuplicateRecordException exception = Assertions.assertThrows(DuplicateRecordException.class,
                () -> dataService.savePatient(inputDto, apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertEquals("Patient already exists with the same PatientRecordID",
                exception.getMessage());

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

}

