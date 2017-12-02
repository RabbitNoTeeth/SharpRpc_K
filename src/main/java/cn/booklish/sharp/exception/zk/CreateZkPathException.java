package cn.booklish.sharp.exception.zk;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:49
 * @desc: zookeeper节点创建异常
 */
public class CreateZkPathException extends RuntimeException {

    public CreateZkPathException(String message){
        super(message);
    }

    public CreateZkPathException(String message,Throwable cause){
        super(message,cause);
    }

}
