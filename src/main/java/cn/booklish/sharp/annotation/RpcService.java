package cn.booklish.sharp.annotation;

import java.lang.annotation.*;

/**
 * @Author: liuxindong
 * @Description:  标记需要发布为Rpc的服务类,开启自动扫描后,会自动扫描带有该注解的类并发布为Rpc服务
 * @Created: 2017/12/13 8:45
 * @Modified:
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    String path() default "";

}
