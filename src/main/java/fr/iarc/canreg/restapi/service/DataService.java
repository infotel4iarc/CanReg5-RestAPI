package fr.iarc.canreg.restapi.service;

import canreg.common.Globals;
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
import fr.iarc.canreg.restapi.model.PatientDTO;
import fr.iarc.canreg.restapi.model.SourceDTO;
import fr.iarc.canreg.restapi.model.TumourDTO;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.SQLException;
import java.util.Map;

@Service
public class DataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataService.class);

    @Autowired
    public CanRegDAO canRegDAO;

    @Autowired
    public HoldingDbHandler holdingDbHandler;

    /**
     * Get all populations.
     * @return return Map, never null
     */
    public Map<Integer, PopulationDataset> getPopulations() {
        return canRegDAO.getPopulationDatasets();
    }

    /**
     * Returns the id of a PopulationsDataset
     * @param populationID
     * @return PopulationDataset
     */
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
    public Patient getPatient(Integer recordID) throws RecordLockedException {
        return (Patient) getRecord(recordID, canRegDAO, Globals.PATIENT_TABLE_NAME);
    }

    /**
     * Returns null if the record cannot be read
     * else it returns the record from Source
     *
     * @param recordID
     * @return a record of a source
     * @throws RecordLockedException
     */
    public Source getSource(Integer recordID) throws RecordLockedException {
        return (Source) getRecord(recordID, canRegDAO, Globals.SOURCE_TABLE_NAME);
    }

    /**
     * Returns a tumour
     *
     * @param recordID record id in the database
     * @return Tumour
     * @throws RecordLockedException
     */
    public Tumour getTumour(Integer recordID) throws RecordLockedException {
        return (Tumour) getRecord(recordID, canRegDAO, Globals.TUMOUR_TABLE_NAME);
    }

    /**
     * Returns null if the record cannot be read
     * else it returns the record from Source
     *
     * @param recordID technical id in the table
     * @param dao      the dao to be used
     * @return a record of a source
     * @throws RecordLockedException
     */
    private DatabaseRecord getRecord(Integer recordID, CanRegDAO dao, String table) throws RecordLockedException {
        DatabaseRecord dbRecord = dao.getRecord(recordID,table, false);
        if (dbRecord == null) {
            LOGGER.error("No {} for recordID = {}", table,recordID);
        }
        return  dbRecord;
    }


    /**
     * Save a patient.<br>
     * @param patientDto the patient input object with or without ids (regno and patientrecordid)
     * @param apiUserPrincipal the connected user
     * @return the patientDTO object with the generated ids if they were not present in input<br>
     *         null if the patient already exists with the provided id (usually the 'regno' variable)
     * @throws RecordLockedException if the record is locked, should not happen
     */
    public PatientDTO savePatient(PatientDTO patientDto, Principal apiUserPrincipal) throws RecordLockedException {
        // Build the patient
        Patient patient = new Patient();
        // Fill the variables of the patient
        patientDto.getVariables().entrySet().forEach(entry -> patient.setVariable(entry.getKey(), entry.getValue()));

        try {
            CanRegDAO dao = holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName());
            patient.setVariable(Globals.PATIENT_TABLE_RECORD_ID_VARIABLE_NAME, null);
            int returnedId = dao.savePatient(patient);
            return new PatientDTO((Patient) getRecord(returnedId, dao,Globals.PATIENT_TABLE_NAME));
        }catch (DerbySQLIntegrityConstraintViolationException e){
            LOGGER.error("Patient already exist : {}", e.getMessage());
            throw new DuplicateRecordException("Patient already exist :" + e.getMessage(), e);

        }catch (SQLException e) {
            LOGGER.error("Error while saving a Patient: {}", e.getMessage(), e);
            throw new ServerException("Error while saving a Patient: " + e.getMessage(), e);
        }
    }

    /**
     * Save a tumour.<br>
     * @param tumourDTO
     * @param apiUserPrincipal the connected user
     * @return the tumourDTO object with the generated ids if they were not present in input<br>
     * @throws RecordLockedException if the record is locked, should not happen
     */
    public TumourDTO saveTumour(TumourDTO tumourDTO, Principal apiUserPrincipal)throws RecordLockedException {
        // Build the tumour
        Tumour tumour = new Tumour();
        // Fill the variables of the tumour
        tumourDTO.getVariables().entrySet().forEach(entry -> tumour.setVariable(entry.getKey(), entry.getValue()));

        try {
            CanRegDAO dao = holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName());
            tumour.setVariable(Globals.TUMOUR_TABLE_RECORD_ID_VARIABLE_NAME,null);
            int returnedId = dao.saveTumour(tumour);
            return new TumourDTO((Tumour) getRecord(returnedId, dao,Globals.TUMOUR_TABLE_NAME));
        }catch (DerbySQLIntegrityConstraintViolationException e){
            if(e.getSQLState().equals("23503")) {
                LOGGER.error("Patient not exist :{} ", e.getMessage(), e);
                throw new NotFoundException("Patient not exist :" + e.getMessage(), e);
            }else{
                LOGGER.error("Tumour  already exist :{} ", e.getMessage(), e);
                throw new DuplicateRecordException("Tumour already exist :" + e.getMessage(), e);
            }
        }catch (SQLException e) {
            LOGGER.error("Error while saving a Tumour: {}", e.getMessage(), e);
            throw new ServerException("Error while saving a Tumour: " + e.getMessage(), e);
        }
    }

    /**
     * Save a source.<br>
     * @param sourceDTO
     * @param apiUserPrincipal the connected user
     * @return the sourceDTO object with the generated ids if they were not present in input<br>
     * @throws RecordLockedException if the record is locked, should not happen
     */
    public SourceDTO saveSource(SourceDTO sourceDTO, Principal apiUserPrincipal)throws RecordLockedException {
        // Build the source
        Source source = new Source();
        // Fill the variables of the patient
        sourceDTO.getVariables().entrySet().forEach(entry -> source.setVariable(entry.getKey(), entry.getValue()));

        try {
            CanRegDAO dao = holdingDbHandler.getDaoForApiUser(apiUserPrincipal.getName());
            source.setVariable(Globals.SOURCE_TABLE_RECORD_ID_VARIABLE_NAME,null);
            int returnedId = dao.saveSource(source);
            return new SourceDTO((Source) getRecord(returnedId, dao,Globals.SOURCE_TABLE_NAME));
        }catch (DerbySQLIntegrityConstraintViolationException e){
            if(e.getSQLState().equals("23503")) {
                LOGGER.error("Tumour not exist :{} ", e.getMessage(), e);
                throw new NotFoundException("Tumour not exist :" + e.getMessage(), e);
            }else{
                LOGGER.error("Source  already exist :{} ", e.getMessage(), e);
                throw new DuplicateRecordException("Source already exist :" + e.getMessage(), e);
            }
        }catch (SQLException e) {
            LOGGER.error("Error while saving a Source: {}", e.getMessage(), e);
            throw new ServerException("Error while saving a Source: " + e.getMessage(), e);
        }
    }
}
