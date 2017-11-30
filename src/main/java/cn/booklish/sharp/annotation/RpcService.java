package cn.booklish.sharp.annotation;

import java.lang.annotation.*;

/**
 * 标记需要发布为Rpc服务的类
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    String pathPrefix() default "";

}
