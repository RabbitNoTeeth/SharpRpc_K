package cn.booklish.sharp.exception.zookeeper;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:50
 * @desc: zookeeper节点不存在
 */
public class NoSuchZkPathException extends RuntimeException {

    public NoSuchZkPathException(String message){
        super(message);
    }

    public NoSuchZkPathException(String message,Throwable cause){
        super(message,cause);
    }

}
