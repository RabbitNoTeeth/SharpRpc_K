package cn.booklish.sharp.exception.config;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:49
 * @desc: sharp配置文件异常类
 */
public class SharpConfigException extends RuntimeException {

    public SharpConfigException(String message){
        super(message);
    }

    public SharpConfigException(String message, Throwable cause){
        super(message,cause);
    }

}
