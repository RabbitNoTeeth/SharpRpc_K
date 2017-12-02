package cn.booklish.sharp.exception.zk;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:49
 * @desc: zookeeper节点删除异常
 */
public class DeleteZkPathException extends RuntimeException {

    public DeleteZkPathException(String message){
        super(message);
    }

    public DeleteZkPathException(String message,Throwable cause){
        super(message,cause);
    }

}
