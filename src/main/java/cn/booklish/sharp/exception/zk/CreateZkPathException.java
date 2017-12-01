package cn.booklish.sharp.exception.zk;

public class CreateZkPathException extends RuntimeException {

    public CreateZkPathException(String message){
        super(message);
    }

    public CreateZkPathException(String message,Throwable cause){
        super(message,cause);
    }

}
