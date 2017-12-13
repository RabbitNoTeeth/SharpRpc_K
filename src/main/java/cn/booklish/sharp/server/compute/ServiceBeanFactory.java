package cn.booklish.sharp.server.compute;

/**
 * @Author: liuxindong
 * @Description:  服务类实体工厂,服务器端需要提供其实现,以便在反射计算Rpc请求时能获得相应的service实体
 * @Created: 2017/12/13 9:00
 * @Modified:
 */
public interface ServiceBeanFactory {

    Object getServiceBean(Class<?> clazz);

}
