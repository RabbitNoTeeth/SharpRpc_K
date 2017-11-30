package cn.booklish.sharp.exception;

public class UpdateZkPathException extends RuntimeException {

    public UpdateZkPathException(String message){
        super(message);
    }

    public UpdateZkPathException(String message,Throwable cause){
        super(message,cause);
    }

}
