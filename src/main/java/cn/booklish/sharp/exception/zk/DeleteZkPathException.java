package cn.booklish.sharp.exception.zk;

public class DeleteZkPathException extends RuntimeException {

    public DeleteZkPathException(String message){
        super(message);
    }

    public DeleteZkPathException(String message,Throwable cause){
        super(message,cause);
    }

}
