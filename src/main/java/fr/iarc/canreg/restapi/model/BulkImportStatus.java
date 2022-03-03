package fr.iarc.canreg.restapi.model;

/**
 * Import status.
 */
public enum BulkImportStatus {
    /** File uploaded, not imported yet. */
    UPLOADED,
    /** Import in progress. */
    IN_PROGRESS,
    /** Import done. */
    DONE
}
