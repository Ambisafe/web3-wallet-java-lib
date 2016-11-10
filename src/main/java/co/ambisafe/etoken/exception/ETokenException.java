package co.ambisafe.etoken.exception;

public class ETokenException extends RuntimeException {

    public ETokenException() {
    }

    public ETokenException(String message) {
        super(message);
    }

    public ETokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ETokenException(Throwable cause) {
        super(cause);
    }
}
