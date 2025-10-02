package itmo.soa.service1.exception;

public class NotValidInputException extends RuntimeException {
    public NotValidInputException(String message) {
        super(message);
    }
}
