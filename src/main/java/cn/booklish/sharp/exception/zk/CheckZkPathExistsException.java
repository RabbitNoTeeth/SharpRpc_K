package cn.booklish.sharp.exception.zk;


/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:49
 * @desc: zookeeper节点检查异常类
 */
public class CheckZkPathExistsException extends RuntimeException {

    public CheckZkPathExistsException(String message){
        super(message);
    }

    public CheckZkPathExistsException(String message,Throwable cause){
        super(message,cause);
    }

}
