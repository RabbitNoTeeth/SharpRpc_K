package cn.booklish.sharp.register;


/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 15:51
 * @desc: rpc服务注册实体
 */
public class RegisterBean {

    private final String path;

    private final String serviceTypeName;

    private final String serviceAddress;

    private final String value;

    public RegisterBean(String path, String serviceTypeName, String serviceAddress){
        this.path = path;
        this.serviceTypeName = serviceTypeName;
        this.serviceAddress = serviceAddress;
        this.value = this.serviceTypeName + ";" + this.serviceAddress;
    }

    public String getPath() {
        return path;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "["+this.path+","+this.value+"]";
    }
}
