package co.ambisafe.etoken.exceptions;

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
