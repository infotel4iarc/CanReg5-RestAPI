package fr.iarc.canreg.restapi.service;

import canreg.common.Globals;
import canreg.common.checks.CheckMessage;
import canreg.common.checks.CheckRecordService;
import canreg.common.database.DatabaseRecord;
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
import fr.iarc.canreg.restapi.utils.Constants;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Service for the data: Patient, Tumour, Source.
 */
@Service
public class DataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    /** DAO for the main database. */
    @Autowired
    private CanRegDAO canRegDAO;

    /** Handler for the DAOs for holding databases. */
    @Autowired
    private HoldingDbHandler holdingDbHandler;

    /** Service to check the records before save. */
    @Autowired
    private CheckRecordService checkRecordService;

    /**
     * Get all populations.
     * @return return Map, never null
     */
    public Map<Integer, PopulationDataset> getPopulations() {
        return canRegDAO.getPopulationDatasets();
    }

    /**
     * Returns the id of a PopulationsDataset
     * @param populationID population id
     * @return PopulationDataset
     */
    public PopulationDataset getPopulation(Integer populationID) {
        return getPopulations().get(populationID);
    }

    /**
     * Returns null if the record cannot be read
     * else it returns the record from Patient
     * @param recordID record id in the database
     * @return a record of a patient
     */
    public Patient getPatient(Integer recordID) {
        return (Patient) getRecord(recordID, canRegDAO, Globals.PATIENT_TABLE_NAME);
    }

    /**
     * Returns null if the record cannot be read
     * else it returns the record from Source
     * @param recordID record id in the database
     * @return a record of a source
     */
    public Source getSource(Integer recordID) {
        return (Source) getRecord(recordID, canRegDAO, Globals.SOURCE_TABLE_NAME);
    }

    /**
     * Returns a tumour
     * @param recordID record id in the database
     * @return Tumour
     */
    public Tumour getTumour(Integer recordID) {
        return (Tumour) getRecord(recordID, canRegDAO, Globals.TUMOUR_TABLE_NAME);
    }

    /**
     * Returns null if the record cannot be read
     * else it returns the record from Source
     * @param recordID technical id in the table
     * @param dao      the dao to be used
     * @return a record of a source
     */
    private DatabaseRecord getRecord(Integer recordID, CanRegDAO dao, String table) {
        DatabaseRecord dbRecord = null;
        try {
            dbRecord = dao.getRecord(recordID, table, false);
        } catch (RecordLockedException e) {
            // Will not happen since getRecord is called with lock=false
        }
        if (dbRecord == null) {
            LOGGER.error("No {} for recordID = {}", table, recordID);
        }
        return dbRecord;
    }

    /**
     * Save a patient.<br>
     * @param patientDto       the patient input object with or without ids (regno and patientrecordid)
     * @param apiUserPrincipal the connected user
     * @return the patientDTO object with the generated ids if they were not present in input<br>
     * null if the patient already exists with the provided id (usually the 'regno' variable)
     * @throws VariableErrorException if the validation fails with at least 1 error
     * @throws ServerException        if an SQL exception happened
     */
    public PatientDTO savePatient(PatientDTO patientDto, Principal apiUserPrincipal) {
        // Build the patient
        Patient patient = new Patient();
        // Fill the variables of the patient
        patientDto.getVariables().entrySet().forEach(entry -> patient.setVariable(entry.getKey(), entry.getValue()));

        return savePatient(patient, apiUserPrincipal.getName(), true);
    }

    /**
     * Save a patient.<br>
     * @param patient   the patient input object with or without ids (regno and patientrecordid)
     * @param userName  the user
     * @param doWrite   true to write, false to test only
     * @return the patientDTO object with the generated ids if they were not present in input<br>
     * null if the patient already exists with the provided id (usually the 'regno' variable)
     * @if the record is locked, should not happen
     * @throws VariableErrorException if the validation fails with at least 1 error
     * @throws ServerException if an SQL exception happened
     */
    public PatientDTO savePatient(Patient patient, String userName, boolean doWrite) {
        try {
            CanRegDAO dao = holdingDbHandler.getDaoForApiUser(userName);
            patient.setVariable(Globals.PATIENT_TABLE_RECORD_ID_VARIABLE_NAME, null);

            // Check the input data for the patient
            List<CheckMessage> checkMessages = handleValidationResult(checkRecordService.checkPatient(patient));

            // Check if the patient already exists for the Registry Number
            int nbForPatientId = dao.countPatientByPatientID(patient);
            if (nbForPatientId > 0) {
                throw new DuplicateRecordException("Patient already exists with the same "
                        + dao.getPatientIDVariableName());
            }

            // no message or warning only
            if(doWrite) {
                int returnedId = dao.savePatient(patient);
                return PatientDTO.from((Patient) getRecord(returnedId, dao, Globals.PATIENT_TABLE_NAME), checkMessages);
            } else {
                return PatientDTO.from(patient, checkMessages);
            }

        } catch (DerbySQLIntegrityConstraintViolationException e) {
            throw new DuplicateRecordException("Patient already exists with the same " +
                    Globals.StandardVariableNames.PatientRecordID, e);

        } catch (SQLException e) {
            throw new ServerException("Error while saving a Patient: " + e.getMessage(), e);
        }
    }

    /**
     * Save a tumour.<br>
     * @param tumourDTO        tumour data
     * @param apiUserPrincipal the connected user
     * @return the tumourDTO object with the generated ids if they were not present in input<br>
     * @throws RecordLockedException if the record is locked, should not happen
     */
    public TumourDTO saveTumour(TumourDTO tumourDTO, Principal apiUserPrincipal) throws RecordLockedException {
        // Build the tumour
        Tumour tumour = new Tumour();
        // Fill the variables of the tumour
        tumourDTO.getVariables().entrySet().forEach(entry -> tumour.setVariable(entry.getKey(), entry.getValue()));
            
        return saveTumour(tumour, apiUserPrincipal.getName(), true);
    }

    /**
     * Save a tumour.<br>
     * @param tumour tumour data
     * @param userName the connected username
     * @param doWrite   true to write, false to test only                 
     * @return the tumourDTO object with the generated ids if they were not present in input<br>
     * @throws RecordLockedException if the record is locked, should not happen
     */
    public TumourDTO saveTumour(Tumour tumour, String userName, boolean doWrite) throws RecordLockedException {

        try {
            CanRegDAO dao = holdingDbHandler.getDaoForApiUser(userName);
            tumour.setVariable(Globals.TUMOUR_TABLE_RECORD_ID_VARIABLE_NAME, null);

            // Check the tumour
            List<CheckMessage> checkMessages = handleValidationResult(checkRecordService.checkTumour(tumour));

            if(doWrite) {
                int returnedId = dao.saveTumour(tumour);
                return TumourDTO.from((Tumour) getRecord(returnedId, dao, Globals.TUMOUR_TABLE_NAME), checkMessages);
            } else {
                return TumourDTO.from(tumour, checkMessages);
            }
        } catch (DerbySQLIntegrityConstraintViolationException e) {
            if (e.getSQLState().equals("23503")) {
                LOGGER.error("Patient does not exist: {} ", e.getMessage(), e);
                throw new NotFoundException("Patient does not exist: " + e.getMessage(), e);
            } else {
                LOGGER.error("Tumour already exists: {} ", e.getMessage(), e);
                throw new DuplicateRecordException("Tumour already exists: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new ServerException("Error while saving a Tumour: " + e.getMessage(), e);
        }
    }

    /**
     * Save a source.<br>
     * @param sourceDTO        source data
     * @param apiUserPrincipal the connected user
     * @return the sourceDTO object with the generated ids if they were not present in input<br>
     * @throws RecordLockedException if the record is locked, should not happen
     */
    public SourceDTO saveSource(SourceDTO sourceDTO, Principal apiUserPrincipal) throws RecordLockedException {
        // Build the source
        Source source = new Source();
        // Fill the variables of the patient
        sourceDTO.getVariables().entrySet().forEach(entry -> source.setVariable(entry.getKey(), entry.getValue()));

        return  saveSource(source, apiUserPrincipal.getName(), true);
    }
    
    /**
     * Save a source.<br>
     * @param source source data
     * @param @param userName the connected username
     * @param doWrite   true to write, false to test only
     * @return the sourceDTO object with the generated ids if they were not present in input<br>
     * @throws RecordLockedException if the record is locked, should not happen
     */
    public SourceDTO saveSource(Source source, String userName, boolean doWrite) throws RecordLockedException {

        try {
            CanRegDAO dao = holdingDbHandler.getDaoForApiUser(userName);
            source.setVariable(Globals.SOURCE_TABLE_RECORD_ID_VARIABLE_NAME, null);

            // Check the source
            List<CheckMessage> checkMessages = handleValidationResult(checkRecordService.checkSource(source));

            if(doWrite) {
                int returnedId = dao.saveSource(source);
                return SourceDTO.from((Source) getRecord(returnedId, dao, Globals.SOURCE_TABLE_NAME), checkMessages);
            } else {
                return SourceDTO.from(source, checkMessages);
            }

        } catch (DerbySQLIntegrityConstraintViolationException e) {
            if (e.getSQLState().equals("23503")) {
                LOGGER.error("Tumour does not exist: {} ", e.getMessage(), e);
                throw new NotFoundException("Tumour does not exist: " + e.getMessage(), e);
            } else {
                LOGGER.error("Source already exists :{} ", e.getMessage(), e);
                throw new DuplicateRecordException("Source already exists: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new ServerException("Error while saving a Source: " + e.getMessage(), e);
        }
    }

    /**
     * Create a population.<br>
     * @param populationDataset populationDataset
     * @return the PopulationDataset object with the generated ids <br>
     * @throws DuplicateRecordException if the record already exists for the populationDatasetIDn
     */
    public PopulationDataset createPopulation(PopulationDataset populationDataset) {
        PopulationDataset populationDatasetExist = getPopulation(populationDataset.getPopulationDatasetID());

        if (populationDatasetExist != null
                && populationDataset.getPopulationDatasetName().equals(
                populationDatasetExist.getPopulationDatasetName())) {
            throw new DuplicateRecordException("The population dataSet already exists");
        }
        int returnedId = canRegDAO.saveNewPopulationDataset(populationDataset);
        return getPopulation(returnedId);
    }

    /**
     * Edit an existing population dataset.<br>
     * @param populationDataset populationDataset with populationDatasetID set.
     * @return the PopulationDataset object <br>
     * @throws NotFoundException if the record is not found with the populationDatasetID
     * @throws ServerException   if the delete before insert fails
     */
    public PopulationDataset editPopulation(PopulationDataset populationDataset) {
        try {
            int result = canRegDAO.updatePopulationDataset(populationDataset);

            if (result == -1) {
                throw new NotFoundException("The population dataSet does not exist");
            }
            return populationDataset;
        } catch (SQLException e) {
            throw new ServerException("Error while deleting a population");
        }

    }

    /**
     * Update a patient.<br>
     * @param patientDto       the patient input object with ids ( patientrecordid)
     * @param apiUserPrincipal the connected user
     * @return the patientDTO object <br>
     * @throws VariableErrorException if the validation fails with at least 1 error
     * @throws ServerException        if an SQL exception happened
     */
    public PatientDTO editPatient(PatientDTO patientDto, Principal apiUserPrincipal) throws RecordLockedException {
        // Build the patient
        Patient inputPatient = new Patient();
        // Fill the variables of the patient
        patientDto.getVariables().entrySet().forEach(entry -> inputPatient.setVariable(entry.getKey(), entry.getValue()));

        try {
            CanRegDAO dao = holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName());

            // Check the input data for the patient
            List<CheckMessage> checkMessages = handleValidationResult(checkRecordService.checkPatient(inputPatient));

            // Check if the patient already exists for the Record ID
            int nbForPatientRecordId = dao.countPatientByPatientRecordID(inputPatient);
            if (nbForPatientRecordId > 1) {
                throw new ServerException("Update impossible, multiple patient records found");
            }
            if (nbForPatientRecordId == 0) {
                throw new NotFoundException(Constants.PATIENT_NOT_FOUND);
            }
            Patient patientForRecordID = dao.getPatientByPatientRecordID(
                    (String) inputPatient.getVariable(dao.getPatientRecordIDVariableName()));

            //set PRID for update
            inputPatient.setVariable(Globals.PATIENT_TABLE_RECORD_ID_VARIABLE_NAME,
                    patientForRecordID.getVariable(Globals.PATIENT_TABLE_RECORD_ID_VARIABLE_NAME));

            if (!dao.editPatient(inputPatient, false)) {
                throw new ServerException(Constants.NOT_UPDATING);
            }
            return PatientDTO.from(inputPatient, checkMessages);

        } catch (SQLException e) {
            throw new ServerException("Error while updating a Patient: " + e.getMessage(), e);

        }
    }

    /**
     * Update a tumour.<br>
     * @param tumourDTO        the tumour input object with id ( tumourID)
     * @param apiUserPrincipal the connected user
     * @return the tumourDTO object <br>
     * @throws VariableErrorException if the validation fails with at least 1 error
     * @throws ServerException        if an SQL exception happened
     */
    public TumourDTO editTumour(TumourDTO tumourDTO, Principal apiUserPrincipal) throws RecordLockedException {
        // Build the tumour
        Tumour inputTumour = new Tumour();
        // Fill the variables of the tumour
        tumourDTO.getVariables().entrySet().forEach(entry -> inputTumour.setVariable(entry.getKey(), entry.getValue()));

        try {
            CanRegDAO dao = holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName());

            // Check the input data for the tumour
            List<CheckMessage> checkMessages = handleValidationResult(checkRecordService.checkTumour(inputTumour));

            // Check if the tumour already exists for the Record ID
            int nbForTumourId = dao.countTumourByTumourID(inputTumour);
            if (nbForTumourId > 1) {
                throw new ServerException(Constants.MULTIPLE_TUMOUR);
            }
            if (nbForTumourId == 0) {
                throw new NotFoundException(Constants.TUMOUR_NOT_FOUND);
            }
            Tumour tumourForTumourId = dao.getTumourByTumourID(
                    (String) inputTumour.getVariable(dao.getTumourIDVariableName()));

            //set TRID for update
            inputTumour.setVariable(Globals.TUMOUR_TABLE_RECORD_ID_VARIABLE_NAME, 
                    tumourForTumourId.getVariable(Globals.TUMOUR_TABLE_RECORD_ID_VARIABLE_NAME));

            if (!dao.editTumour(inputTumour, false)) {
                throw new ServerException(Constants.NOT_UPDATING);
            }
            return TumourDTO.from(inputTumour, checkMessages);

        } catch (DerbySQLIntegrityConstraintViolationException e) {
            LOGGER.error("Patient does not exist: {} ", e.getMessage(), e);
            throw new NotFoundException(Constants.PATIENT_NOT_FOUND + e.getMessage(), e);

        } catch (SQLException e) {
            throw new ServerException("Error while updating a Tumour: " + e.getMessage(), e);
        }
    }

    /**
     * Update a source.<br>
     * @param sourceDTO        the source input object with id ( sourceRecordId)
     * @param apiUserPrincipal the connected user
     * @return the sourceDTO object <br>
     * @throws VariableErrorException if the validation fails with at least 1 error
     * @throws ServerException        if an SQL exception happened
     */
    public SourceDTO editSource(SourceDTO sourceDTO, Principal apiUserPrincipal) throws RecordLockedException {
        Source inputSource = new Source();
        // Fill the variables of the source
        sourceDTO.getVariables().entrySet().forEach(entry -> inputSource.setVariable(entry.getKey(), entry.getValue()));

        try {
            CanRegDAO dao = holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName());

            // Check the input data for the source
            List<CheckMessage> checkMessages = handleValidationResult(checkRecordService.checkSource(inputSource));

            // Check if the source already exists for the Record ID
            int nbForSourceRecordId = dao.countSourceBySourceRecordID(inputSource);
            if (nbForSourceRecordId > 1) {
                throw new ServerException(Constants.MULTIPLE_SOURCE);
            }
            if (nbForSourceRecordId == 0) {
                throw new NotFoundException(Constants.SOURCE_NOT_FOUND);
            }
            Source sourceForSourceRecordId = dao.getSourceBySourceID((String) inputSource.
                    getVariable(dao.getSourceRecordIDVariableName()));

            //set SRID for update
            inputSource.setVariable(Globals.SOURCE_TABLE_RECORD_ID_VARIABLE_NAME, 
                    sourceForSourceRecordId.getVariable(Globals.SOURCE_TABLE_RECORD_ID_VARIABLE_NAME));

            if (!dao.editSource(inputSource, false)) {
                throw new ServerException(Constants.NOT_UPDATING);
            }
            return SourceDTO.from(inputSource, checkMessages);

        } catch (DerbySQLIntegrityConstraintViolationException e) {
            LOGGER.error("Tumour does not exist: {} ", e.getMessage(), e);
            throw new NotFoundException(Constants.TUMOUR_NOT_FOUND + e.getMessage(), e);

        } catch (SQLException e) {
            throw new ServerException(Constants.ERROR_UPDATE_SOURCE + e.getMessage(), e);
        }
    }

    private List<CheckMessage> handleValidationResult(List<CheckMessage> checkMessages) {
        if (!checkMessages.isEmpty() && checkMessages.stream().anyMatch(CheckMessage::isError)) {
            // Validation error
            throw new VariableErrorException(checkMessages.toString());
        }
        return checkMessages;
    }

}
