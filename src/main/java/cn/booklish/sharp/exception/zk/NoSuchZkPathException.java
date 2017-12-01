package cn.booklish.sharp.exception.zk;

public class NoSuchZkPathException extends RuntimeException {

    public NoSuchZkPathException(String message){
        super(message);
    }

    public NoSuchZkPathException(String message,Throwable cause){
        super(message,cause);
    }

}
