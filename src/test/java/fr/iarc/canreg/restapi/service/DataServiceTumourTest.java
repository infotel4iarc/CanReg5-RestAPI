package fr.iarc.canreg.restapi.service;

import canreg.common.checks.CheckMessage;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.Tumour;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.NotFoundException;
import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.exception.VariableErrorException;
import fr.iarc.canreg.restapi.model.TumourDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of DataService for Tumour.
 */
@ExtendWith(MockitoExtension.class)
class DataServiceTumourTest extends DataServiceParentForJunit {

    @Test
    void testSaveTumour() throws RecordLockedException {

        Tumour tumour = new Tumour();
        tumour.setVariable("trid", 1);
        tumour.setVariable("extent", "RE");
        tumour.setVariable("patientrecordidtumourtable", "122222222");

        Tumour tumourDB = new Tumour();
        tumourDB.setVariable("trid", 1);
        tumourDB.setVariable("extent", "RE");
        tumourDB.setVariable("patientrecordidtumourtable", "122222222");
        tumourDB.setVariable("tumourid", "202200010101");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(tumourDB);
        TumourDTO tumourDTO = dataService.saveTumour(TumourDTO.from(tumour, null), apiUserPrincipal);
        Assertions.assertEquals("RE", tumourDTO.getVariables().get("extent"));
        Assertions.assertEquals("202200010101", tumourDTO.getVariables().get("tumourid"));
        Assertions.assertNull(tumourDTO.getVariables().get("trid"));
        Assertions.assertNull(tumourDTO.getVariables().get(CheckRecordService.VARIABLE_RAW_DATA));
        Assertions.assertNull(tumourDTO.getVariables().get(CheckRecordService.VARIABLE_FORMAT_ERRORS));
    }

    @Test
    void testSavePatientWithWarning() throws SQLException, RecordLockedException {
        Tumour tumour = new Tumour();
        tumour.setVariable("trid", 1);
        tumour.setVariable("extent", "RE");
        tumour.setVariable("patientrecordidtumourtable", "122222222");

        Tumour tumourDB = new Tumour();
        tumourDB.setVariable("trid", 1);
        tumourDB.setVariable("extent", "RE");
        tumourDB.setVariable("patientrecordidtumourtable", "122222222");
        tumourDB.setVariable("tumourid", "202200010101");

        List<CheckMessage> checkMessages = new ArrayList<>();
        checkMessages.add(new CheckMessage("age", "", "this variable is mandatory", false));
        
        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkTumour(Mockito.any(Tumour.class))).thenReturn(checkMessages);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(tumourDB);
        
        TumourDTO tumourDTO = dataService.saveTumour(TumourDTO.from(tumour, null), apiUserPrincipal);

        Mockito.verify(canRegDAO, Mockito.times(1)).saveTumour(Mockito.any(Tumour.class));
        Assertions.assertEquals("RE", tumourDTO.getVariables().get("extent"));
        Assertions.assertEquals("202200010101", tumourDTO.getVariables().get("tumourid"));
        Assertions.assertNull(tumourDTO.getVariables().get("trid"));

        Assertions.assertEquals("[{level='warning', variable='age', value='', message='this variable is mandatory'}]",
                tumourDTO.getVariables().get(CheckRecordService.VARIABLE_FORMAT_ERRORS).toString());
        Assertions.assertNull(tumourDTO.getVariables().get(CheckRecordService.VARIABLE_RAW_DATA));
    }
    
    @Test
    void testSaveTumourWithError() throws RecordLockedException, SQLException {

        Tumour tumour = new Tumour();
        tumour.setVariable("trid", 1);
        tumour.setVariable("extent", "RE");
        tumour.setVariable("patientrecordidtumourtable", "122222222");
        // Age is not a number object
        tumour.setVariable("age", "89a");

        Tumour tumourDB = new Tumour();
        tumourDB.setVariable("trid", 1);
        tumourDB.setVariable("extent", "RE");
        tumourDB.setVariable("patientrecordidtumourtable", "122222222");
        tumourDB.setVariable("tumourid", "202200010101");

        List<CheckMessage> checkMessages = new ArrayList<>();
        checkMessages.add(new CheckMessage("age", "89a", "this value is not an integer", true));

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkTumour(Mockito.any(Tumour.class))).thenReturn(checkMessages);
        
        TumourDTO inputDto = TumourDTO.from(tumour, null);
        VariableErrorException exception = Assertions.assertThrows(VariableErrorException.class,
                () -> dataService.saveTumour(inputDto, apiUserPrincipal));

        Mockito.verify(canRegDAO, Mockito.times(0)).saveTumour(Mockito.any());
        Mockito.verify(canRegDAO, Mockito.times(0)).getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean());
        Assertions.assertEquals("[{level='error', variable='age', value='89a', message='this value is not an integer'}]",
                exception.getMessage());
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
        TumourDTO tumourDTO = TumourDTO.from(tumour, null);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> dataService.saveTumour(tumourDTO, apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Patient does not exist: "));

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
        TumourDTO tumourDTO = TumourDTO.from(tumour, null);
        DuplicateRecordException exception = Assertions.assertThrows(DuplicateRecordException.class, 
                () -> dataService.saveTumour(tumourDTO, apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Tumour already exists: "));
    }

    @Test
    void testSaveTumourExceptionSQL() throws RecordLockedException, SQLException {

        Tumour tumour = new Tumour();
        tumour.setVariable("trid", 1);
        tumour.setVariable("extent", "RE");
        tumour.setVariable("patientrecordidtumourtable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.saveTumour(Mockito.any(Tumour.class))).thenThrow(SQLException.class);
        TumourDTO tumourDTO = TumourDTO.from(tumour, null);
        ServerException exception = Assertions.assertThrows(ServerException.class, 
                () -> dataService.saveTumour(tumourDTO, apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error while saving a Tumour"));
    }


}

