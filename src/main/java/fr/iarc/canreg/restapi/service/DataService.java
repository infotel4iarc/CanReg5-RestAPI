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
     * @param recordID id of a patient
     * @return a patient
     * @throws RecordLockedException
     */
    public Patient getPatient(Integer recordID) throws RecordLockedException {

        DatabaseRecord p =  canRegDAO.getRecord(recordID, Globals.PATIENT_TABLE_NAME, false);


        System.err.println( " patient "+ p);
        return (Patient) canRegDAO.getRecord(recordID, Globals.PATIENT_TABLE_NAME, false);
    }


        /**
         * @param recordID
         * @return
         * @throws RecordLockedException
         */
        public Tumour getTumours ( int recordID) throws RecordLockedException {
            return (Tumour) canRegDAO.getRecord(recordID, Globals.TUMOUR_TABLE_NAME, false);
        }

        public Source getSources ( int recordID, boolean lock) throws RecordLockedException {
            return (Source) canRegDAO.getRecord(recordID, Globals.SOURCE_TABLE_NAME, false);
        }


        public DatabaseRecord getRecord ( int recordID, String tableName,boolean lock) throws RecordLockedException {
            switch (tableName) {
                case Globals.PATIENT_TABLE_NAME:
                    return getPatient(recordID);

                case Globals.TUMOUR_TABLE_NAME:
                    return getTumours(recordID);

                case Globals.SOURCE_TABLE_NAME:
                    return getSources(recordID, lock);

                default:
                    return null;
            }
        }

    }
