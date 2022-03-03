package fr.iarc.canreg.restapi.service;

import canreg.common.database.PopulationDataset;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.exception.DuplicateRecordException;
import fr.iarc.canreg.restapi.exception.NotFoundException;
import fr.iarc.canreg.restapi.exception.ServerException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test of DataService for Population dataset.
 */
@ExtendWith(MockitoExtension.class)
class DataServicePopulationTest extends DataServiceParentForJunit {

    @Test
    void testSavePopulations() throws SQLException, RecordLockedException {

        PopulationDataset populationDataset = new PopulationDataset();
        populationDataset.setPopulationDatasetID(10);
        populationDataset.setPopulationDatasetName("test create");
        Map<Integer, PopulationDataset> mapPopulationDataset = new HashMap<>();
        mapPopulationDataset.put(0, populationDataset);

        PopulationDataset populationDatasetDb = new PopulationDataset();
        populationDatasetDb.setPopulationDatasetID(1);
        populationDatasetDb.setPopulationDatasetName("test create");

        Map<Integer, PopulationDataset> mapPopulationDatasetDb = new HashMap<>();
        mapPopulationDatasetDb.put(0, populationDatasetDb);
        Mockito.when(dataService.getPopulations()).thenReturn(mapPopulationDataset);

        PopulationDataset result = dataService.createPopulation(populationDataset);

        Mockito.verify(canRegDAO, Mockito.times(1)).saveNewPopulationDataset(Mockito.any(PopulationDataset.class));
        Assertions.assertEquals(10, result.getPopulationDatasetID());
    }
    @Test
    void testSavePopulationException() throws SQLException {

        PopulationDataset populationDataset = new PopulationDataset();
        populationDataset.setPopulationDatasetID(1);
        populationDataset.setPopulationDatasetName("test create");
        Map<Integer, PopulationDataset> mapPopulationDataset = new HashMap<>();
        mapPopulationDataset.put(1, populationDataset);

        PopulationDataset populationDatasetDb = new PopulationDataset();
        populationDatasetDb.setPopulationDatasetID(1);
        populationDatasetDb.setPopulationDatasetName("test create");

        Map<Integer, PopulationDataset> mapPopulationDatasetDb = new HashMap<>();
        mapPopulationDatasetDb.put(1, populationDatasetDb);


        Mockito.when(dataService.getPopulations()).thenReturn(mapPopulationDatasetDb);


        DuplicateRecordException exception = Assertions.assertThrows(DuplicateRecordException.class,
                () -> dataService.createPopulation(populationDataset));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("The population dataSet already exists"));

    }

    @Test
    void testEditPopulations() throws SQLException, RecordLockedException {

        PopulationDataset populationDataset = new PopulationDataset();
        populationDataset.setPopulationDatasetID(1);
        populationDataset.setPopulationDatasetName("test create");
        Map<Integer, PopulationDataset> mapPopulationDataset = new HashMap<>();
        mapPopulationDataset.put(1, populationDataset);

        PopulationDataset populationDatasetDb = new PopulationDataset();
        populationDatasetDb.setPopulationDatasetID(1);
        populationDatasetDb.setPopulationDatasetName("test create");

        Map<Integer, PopulationDataset> mapPopulationDatasetDb = new HashMap<>();
        mapPopulationDatasetDb.put(0, populationDatasetDb);
        Mockito.when(canRegDAO.updatePopulationDataset(populationDataset)).thenReturn(populationDataset.getPopulationDatasetID());
        PopulationDataset result = dataService.editPopulation(populationDataset);

        Mockito.verify(canRegDAO, Mockito.times(1)).updatePopulationDataset(Mockito.any(PopulationDataset.class));
        Assertions.assertEquals(1, result.getPopulationDatasetID());
    }

    @Test
    void testEditPopulationExceptionNotFound() throws SQLException {

        PopulationDataset populationDataset = new PopulationDataset();
        populationDataset.setPopulationDatasetID(1);
        populationDataset.setPopulationDatasetName("test edit");
        Map<Integer, PopulationDataset> mapPopulationDataset = new HashMap<>();
        mapPopulationDataset.put(0, populationDataset);

        PopulationDataset populationDatasetDb = new PopulationDataset();
        populationDatasetDb.setPopulationDatasetID(1);
        populationDatasetDb.setPopulationDatasetName("test edit");

        Map<Integer, PopulationDataset> mapPopulationDatasetDb = new HashMap<>();
        mapPopulationDatasetDb.put(0, populationDatasetDb);

        Mockito.when(canRegDAO.updatePopulationDataset(populationDataset)).thenReturn(-1);

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> dataService.editPopulation(populationDataset));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("The population dataSet does not exist"));

    }


    @Test
    void testEditPopulationException() throws SQLException {

        PopulationDataset populationDataset = new PopulationDataset();
        populationDataset.setPopulationDatasetID(1);
        populationDataset.setPopulationDatasetName("test edit");
        Map<Integer, PopulationDataset> mapPopulationDataset = new HashMap<>();
        mapPopulationDataset.put(1, populationDataset);

        PopulationDataset populationDatasetDb = new PopulationDataset();
        populationDatasetDb.setPopulationDatasetID(1);
        populationDatasetDb.setPopulationDatasetName("test edit");

        Map<Integer, PopulationDataset> mapPopulationDatasetDb = new HashMap<>();
        mapPopulationDatasetDb.put(1, populationDatasetDb);

        Mockito.when(canRegDAO.updatePopulationDataset(Mockito.any(PopulationDataset.class))).thenThrow(SQLException.class);;

        ServerException exception = Assertions.assertThrows(ServerException.class,
                () -> dataService.editPopulation(populationDataset));

        Assertions.assertNotNull(exception);
        Assertions.assertTrue(exception.getMessage().contains("Error while deleting a population"));

    }

}

