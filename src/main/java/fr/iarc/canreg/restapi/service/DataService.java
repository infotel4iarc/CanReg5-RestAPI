package fr.iarc.canreg.restapi.service;

import canreg.common.Globals;
import canreg.common.database.DatabaseRecord;
import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.CanRegDAO;
import canreg.server.database.RecordLockedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class  DataService {

    @Autowired
    public CanRegDAO canRegDAO;


    public Map<Integer, PopulationDataset> getPopulations() {
        return canRegDAO.getPopulationDatasets();
    }

    public Patient getPatient(int recordID, boolean lock) throws RecordLockedException {
        return (Patient) canRegDAO.getRecord(recordID, Globals.PATIENT_TABLE_NAME, false);
    }

    public Tumour getTumours(int recordID, boolean lock) throws RecordLockedException {
        return (Tumour) canRegDAO.getRecord(recordID, Globals.TUMOUR_TABLE_NAME, false);
    }

    public Source getSources(int recordID, boolean lock) throws RecordLockedException {
        return (Source) canRegDAO.getRecord(recordID, Globals.SOURCE_TABLE_NAME, false);
    }


    public DatabaseRecord getRecord(int recordID, String tableName, boolean lock) throws RecordLockedException {

        switch (tableName) {
            case Globals.PATIENT_TABLE_NAME:
                return getPatient(recordID, lock);

            case Globals.TUMOUR_TABLE_NAME:
                return getTumours(recordID, lock);

            case Globals.SOURCE_TABLE_NAME:
                return getSources(recordID, lock);

            default:
                return null;
        }
    }

}
