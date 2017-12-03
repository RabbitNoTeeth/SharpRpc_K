package cn.booklish.sharp.exception.zookeeper;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:50
 * @desc: zookeeper节点更新异常
 */
public class UpdateZkPathException extends RuntimeException {

    public UpdateZkPathException(String message){
        super(message);
    }

    public UpdateZkPathException(String message,Throwable cause){
        super(message,cause);
    }

}
