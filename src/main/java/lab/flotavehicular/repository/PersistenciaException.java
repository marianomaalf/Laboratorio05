package lab.flotavehicular.repository;

public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String message, Throwable cause) {
        super(message, cause);
    }
}
