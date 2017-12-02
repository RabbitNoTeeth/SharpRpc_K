package cn.booklish.sharp.exception.zk;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:50
 * @desc: zookeeper节点数据获取异常
 */
public class GetZkPathDataException extends RuntimeException {

    public GetZkPathDataException(String message){
        super(message);
    }

    public GetZkPathDataException(String message, Throwable cause){
        super(message,cause);
    }

}
