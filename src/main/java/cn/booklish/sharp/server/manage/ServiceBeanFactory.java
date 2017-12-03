package cn.booklish.sharp.server.manage;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @data: 2017/12/3 22:25
 * @desc:
 */
public interface ServiceBeanFactory {

    Object getServiceBean(Class<?> clazz);

}
