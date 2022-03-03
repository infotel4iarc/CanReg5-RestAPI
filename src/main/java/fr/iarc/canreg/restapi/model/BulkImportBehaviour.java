package fr.iarc.canreg.restapi.model;

/**
 * Import behaviour.
 */
public enum BulkImportBehaviour {
    /** Create the records only. */
    CREATE_ONLY,
    /** Reject if the record already exists. */
    //REJECT,
    /** Update: update only the variables with an input value not empty. */
    //UPDATE,
    /** Overwrite: update all the values, even the ones with an empty input value. */
    //OVERWRITE
}
