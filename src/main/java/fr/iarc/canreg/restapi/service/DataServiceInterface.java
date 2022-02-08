package fr.iarc.canreg.restapi.service;

import canreg.common.database.Patient;
import canreg.common.database.PopulationDataset;
import canreg.common.database.Source;
import canreg.common.database.Tumour;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.model.PatientDTO;

import java.util.Map;

public interface DataServiceInterface {

    Map<Integer, PopulationDataset> getPopulations();

    PopulationDataset getPopulation(Integer populationID);

    Patient getPatient(Integer recordID) throws RecordLockedException;

    Source getSource(Integer recordID) throws RecordLockedException;

    Tumour getTumour(Integer recordID) throws RecordLockedException;

    void setPatient(PatientDTO patient);
}

