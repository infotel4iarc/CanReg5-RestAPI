package fr.iarc.canreg.restapi.service;

import canreg.common.checks.CheckMessage;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.Source;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.NotFoundException;
import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.exception.VariableErrorException;
import fr.iarc.canreg.restapi.model.SourceDTO;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test of DataService for Source.
 */
@ExtendWith(MockitoExtension.class)
class DataServiceSourceTest extends DataServiceParentForJunit {

    @Test
    void testSaveSource() throws RecordLockedException, SQLException {

        Source source = new Source();
        source.setVariable("srid", "1");
        source.setVariable("sourcerecordid", "2006601301010");
        source.setVariable("source", "016");
        source.setVariable("tumouridsourcetable", "122222222");

        Source sourceDb = new Source();
        sourceDb.setVariable("srid", "22");
        sourceDb.setVariable("sourcerecordid", "2006601301010");
        sourceDb.setVariable("source", "016");
        sourceDb.setVariable("tumouridsourcetable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(sourceDb);
        Mockito.when(canRegDAO.saveSource(Mockito.any(Source.class))).thenReturn(1);
        
        SourceDTO sourceDTO = dataService.saveSource(SourceDTO.from(source, null), apiUserPrincipal);

        Mockito.verify(canRegDAO, Mockito.times(1)).saveSource(Mockito.any());
        Assertions.assertEquals("016", sourceDTO.getVariables().get("source"));
        Assertions.assertNull(sourceDTO.getVariables().get("srid"));
        Assertions.assertEquals("2006601301010", sourceDTO.getVariables().get("sourcerecordid"));

    }

    @Test
    void testSaveSourceWithWarning() throws RecordLockedException, SQLException {

        Source source = new Source();
        source.setVariable("srid", "1");
        source.setVariable("sourcerecordid", "2006601301010");
        source.setVariable("source", "");
        source.setVariable("tumouridsourcetable", "122222222");

        Source sourceDb = new Source();
        sourceDb.setVariable("srid", "22");
        sourceDb.setVariable("sourcerecordid", "2006601301010");
        sourceDb.setVariable("source", "");
        sourceDb.setVariable("tumouridsourcetable", "122222222");

        List<CheckMessage> checkMessages = new ArrayList<>();
        checkMessages.add(new CheckMessage("source", "", "this variable is mandatory", false));
        
        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkSource(Mockito.any(Source.class))).thenReturn(checkMessages);
        Mockito.when(canRegDAO.saveSource(Mockito.any(Source.class))).thenReturn(1);
        Mockito.when(canRegDAO.getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean())).thenReturn(sourceDb);

        SourceDTO sourceDTO = dataService.saveSource(SourceDTO.from(source, null), apiUserPrincipal);

        Mockito.verify(canRegDAO, Mockito.times(1)).saveSource(Mockito.any());
        Assertions.assertEquals("", sourceDTO.getVariables().get("source"));
        Assertions.assertNull(sourceDTO.getVariables().get("srid"));
        Assertions.assertEquals("2006601301010", sourceDTO.getVariables().get("sourcerecordid"));
        Assertions.assertEquals("[{level='warning', variable='source', value='', message='this variable is mandatory'}]",
                sourceDTO.getVariables().get(CheckRecordService.VARIABLE_FORMAT_ERRORS).toString());
        Assertions.assertNull(sourceDTO.getVariables().get(CheckRecordService.VARIABLE_RAW_DATA));
    }

    @Test
    void testSaveSourceWithError() throws RecordLockedException, SQLException {

        Source source = new Source();
        source.setVariable("srid", "1");
        source.setVariable("sourcerecordid", "2006601301010");
        source.setVariable("source", "123456789123");
        source.setVariable("tumouridsourcetable", "122222222");

        List<CheckMessage> checkMessages = new ArrayList<>();
        checkMessages.add(new CheckMessage("source", "123456789123", "mocked error", true));

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(checkRecordService.checkSource(Mockito.any(Source.class))).thenReturn(checkMessages);

        SourceDTO inputDto = SourceDTO.from(source, null);
        VariableErrorException exception = Assertions.assertThrows(VariableErrorException.class,
                () -> dataService.saveSource(inputDto, apiUserPrincipal));

        Mockito.verify(canRegDAO, Mockito.times(0)).saveSource(Mockito.any());
        Mockito.verify(canRegDAO, Mockito.times(0)).getRecord(Mockito.anyInt(), Mockito.anyString(), Mockito.anyBoolean());
        Assertions.assertEquals("[{level='error', variable='source', value='123456789123', message='mocked error'}]",
                exception.getMessage());
        
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

        SourceDTO sourceDTO = SourceDTO.from(source, null);
        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> dataService.saveSource(sourceDTO, apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Tumour does not exist: "));

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

        SourceDTO sourceDTO = SourceDTO.from(source, null);
        DuplicateRecordException exception = Assertions.assertThrows(DuplicateRecordException.class,
                () -> dataService.saveSource(sourceDTO, apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Source already exists: "));
    }

    @Test
    void testSaveSourceExceptionSQL() throws RecordLockedException, SQLException {

        Source source = new Source();
        source.setVariable("sourcerecordid", "2006601301010");
        source.setVariable("source", "016");
        source.setVariable("tumouridsourcetable", "122222222");

        Mockito.when(holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName())).thenReturn(canRegDAO);
        Mockito.when(canRegDAO.saveSource(Mockito.any(Source.class))).thenThrow(SQLException.class);

        SourceDTO sourceDTO = SourceDTO.from(source, null);
        ServerException exception = Assertions.assertThrows(ServerException.class,
                () -> dataService.saveSource(sourceDTO, apiUserPrincipal));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error while saving a Source"));
    }
}

