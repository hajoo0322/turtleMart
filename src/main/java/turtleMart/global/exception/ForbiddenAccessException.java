package turtleMart.global.exception;

public class ForbiddenAccessException extends CustomRuntimeException {
    public ForbiddenAccessException(ErrorCode errorCode) {
        super(errorCode);

    }

    public ForbiddenAccessException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
