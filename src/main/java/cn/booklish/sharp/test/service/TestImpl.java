package cn.booklish.sharp.test.service;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author Don9
 * @create 2017-12-11-15:10
 **/
public class TestImpl extends UnicastRemoteObject implements Test  {
    public TestImpl() throws RemoteException {
    }

    @Override
    public User run(int id) throws RemoteException {
        return new User(id);
    }
}
