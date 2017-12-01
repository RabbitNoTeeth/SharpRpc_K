package cn.booklish.test;

import cn.booklish.sharp.annotation.RpcService;

/**
 * @author Don9
 * @create 2017-11-21-14:05
 **/

@RpcService(pathPrefix = "/aaa/bbb/ccc")
public class TestImpl implements TestInterface {
    public String compute(Integer x, Integer y) {
        return x+"/"+y+" = "+(x/y);
    }
}
