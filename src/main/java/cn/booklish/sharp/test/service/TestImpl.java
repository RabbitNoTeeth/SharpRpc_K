package cn.booklish.sharp.test.service;


/**
 * @author Don9
 * @create 2017-12-11-15:10
 **/
public class TestImpl implements Test {
    @Override
    public User run(int id) {
        return new User(id);
    }
}
