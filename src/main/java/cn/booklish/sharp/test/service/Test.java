package cn.booklish.sharp.test.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Don9
 * @create 2017-12-11-15:10
 **/
public interface Test extends Remote {

    User run(int id) throws RemoteException;

}
