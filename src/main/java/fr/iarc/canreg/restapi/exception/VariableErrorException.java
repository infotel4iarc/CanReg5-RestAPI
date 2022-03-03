package fr.iarc.canreg.restapi.exception;

/**
 * Error with one or more variables of the record.
 */
public class VariableErrorException extends RuntimeException{
    /**
     * Constructor.
     * @param message message
     */
    public VariableErrorException(String message) {
        super(message);
    }

    
}
