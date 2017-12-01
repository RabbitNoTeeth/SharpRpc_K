package cn.booklish.sharp.exception.zk;

public class GetZkPathDataException extends RuntimeException {

    public GetZkPathDataException(String message){
        super(message);
    }

    public GetZkPathDataException(String message, Throwable cause){
        super(message,cause);
    }

}
