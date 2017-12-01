package cn.booklish.sharp.exception.zk;

public class CheckZkPathExistsException extends RuntimeException {

    public CheckZkPathExistsException(String message){
        super(message);
    }

    public CheckZkPathExistsException(String message,Throwable cause){
        super(message,cause);
    }

}
