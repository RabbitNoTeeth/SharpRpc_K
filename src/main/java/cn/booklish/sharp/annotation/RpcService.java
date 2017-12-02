package cn.booklish.sharp.annotation;

import java.lang.annotation.*;

/**
 * @author: 刘新冬(www.booklish.cn)
 * @date: 2017/12/2 14:48
 * @desc: 标记需要发布为Rpc服务的类
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    String pathPrefix() default "";

}
