package cn.booklish.sharp.server.compute;

/**
 * @Author: liuxindong
 * @Description: 服务类实体工厂
 * @Create: don9 2017/12/12
 * @Modify:
 */
public interface ServiceBeanFactory {

    Object getServiceBean(Class<?> clazz);

}
