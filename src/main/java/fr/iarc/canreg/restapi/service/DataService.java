package fr.iarc.canreg.restapi.service;

import canreg.common.Globals;
import canreg.common.database.DatabaseRecord;
import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.CanRegDAO;
import canreg.server.database.RecordLockedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataService implements DataServiceInterface {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    @Autowired
    public CanRegDAO canRegDAO;

    /**
     * @return return Map, never null
     */
    public Map<Integer, PopulationDataset> getPopulations() {
        return canRegDAO.getPopulationDatasets();
    }

    /**
     * Returns null if the record cannot be read
     * otherwise it returns the record from Patient
     *
     * @param recordID
     * @return a record of a patient
     * @throws RecordLockedException
     */
    @Override
    public Patient getPatient(Integer recordID) throws RecordLockedException {
        DatabaseRecord record = canRegDAO.getRecord(recordID, Globals.PATIENT_TABLE_NAME, false);
        if (record == null) {
            LOGGER.error("No patient for recordId = {}", recordID);
        }
        return (Patient) record;
    }

    /**
     *
     * @param recordID
     * @return
     * @throws RecordLockedException
     */

    @Override
    public Source getSource(Integer recordID) throws RecordLockedException {
        DatabaseRecord record = canRegDAO.getRecord(recordID, Globals.SOURCE_TABLE_NAME, false);
        if (record == null) {
            LOGGER.error("No source for recordID = {}", recordID);
        }
        return (Source) record;
    }


    /**
     * @param recordID
     * @return
     * @throws RecordLockedException
     */
    @Override
    public Tumour getTumour(Integer recordID) throws RecordLockedException {
        DatabaseRecord record = canRegDAO.getRecord(recordID, Globals.TUMOUR_TABLE_NAME, false);
        if (record == null) {
            LOGGER.error("Tumours  cannot be found: {}", record);
        }
        return (Tumour) record;
    }

}
