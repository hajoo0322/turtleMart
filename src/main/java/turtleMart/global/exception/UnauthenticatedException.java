package turtleMart.global.exception;

/**
 * 401 Unauthrized
 */
public class UnauthenticatedException extends CustomRuntimeException {
    public UnauthenticatedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UnauthenticatedException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
