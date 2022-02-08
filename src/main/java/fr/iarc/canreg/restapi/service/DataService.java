package fr.iarc.canreg.restapi.service;

import canreg.common.Globals;
import canreg.common.database.*;
import canreg.server.database.CanRegDAO;
import canreg.server.database.RecordLockedException;
import fr.iarc.canreg.restapi.exception.ServerException;
import fr.iarc.canreg.restapi.model.PatientDTO;

import java.security.Principal;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataService implements DataServiceInterface {
  private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

  @Autowired
  public CanRegDAO canRegDAO;

  @Autowired
  public HoldingDbHandler holdingDbHandler;
  
  /**
   * Get all populations.
   *
   * @return return Map, never null
   */
  @Override
  public Map<Integer, PopulationDataset> getPopulations() {
    return canRegDAO.getPopulationDatasets();
  }

  /**
   * Returns the id of a PopulationsDataset
   *
   * @param populationID
   * @return PopulationDataset
   */
  @Override
  public PopulationDataset getPopulation(Integer populationID) {
    return getPopulations().get(populationID);
  }

  /**
   * Returns null if the record cannot be read
   * else it returns the record from Patient
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
   * Returns null if the record cannot be read
   * else it returns the record from Source
   *
   * @param recordID
   * @return a record of a source
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


  @Override
  public Tumour getTumour(Integer recordID) throws RecordLockedException {
    DatabaseRecord record = canRegDAO.getRecord(recordID, Globals.TUMOUR_TABLE_NAME, false);
    if (record == null) {
      LOGGER.error("No tumours for recordID = {}", recordID);
    }
    return (Tumour) record;
  }

  @Override
  public int setPatient(PatientDTO patient, Principal apiUser) {
    Patient patients = new Patient();
    patients.setVariable("Patient", patient);
    try {
      User user = new User();
      user.setUserName(apiUser.getName());
    return  holdingDbHandler.getDaoForApiUser(user).savePatient(patients);
    } catch (SQLException e) {
      LOGGER.error("Erreur lors de l'enregistrement de Patient: {}", e);
      throw new ServerException("Erreur lors de l'enregistrement de Patient: : " + e.getMessage(), e);
    }
  }

}
