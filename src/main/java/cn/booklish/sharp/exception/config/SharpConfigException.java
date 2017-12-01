package cn.booklish.sharp.exception.config;

/**
 * sharp配置文件异常类
 */
public class SharpConfigException extends RuntimeException {

    public SharpConfigException(String message){
        super(message);
    }

    public SharpConfigException(String message, Throwable cause){
        super(message,cause);
    }

}
