package cn.booklish.sharp.test.service;

import cn.booklish.sharp.annotation.RpcService;

/**
 * @author Don9
 * @create 2017-12-11-15:10
 **/
@RpcService(pathPrefix = "/test")
public class TestImpl implements Test {
    @Override
    public String run() {
        return "caocaocao";
    }
}
