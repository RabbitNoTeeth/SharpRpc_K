package cn.booklish.sharp.register;


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
