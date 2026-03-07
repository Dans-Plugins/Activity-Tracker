package dansplugins.activitytracker.exceptions;

/**
 * Exception thrown when a session is expected but not found.
 * This can occur when attempting to retrieve a session that doesn't exist
 * or when session references are broken.
 * 
 * @author Daniel McCoy Stephenson
 */
public class NoSessionException extends Exception {
    
    public NoSessionException(String message) {
        super(message);
    }
    
    public NoSessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
