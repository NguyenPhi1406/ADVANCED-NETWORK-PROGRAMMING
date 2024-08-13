/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package LTM;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Service extends Remote {
    List<String> getAllUsernames() throws RemoteException;
    List<String> getAllPasswords() throws RemoteException;
    String verifyMessage(String message, String privateKeyStr) throws RemoteException;
    public List<User> Getallusers() throws RemoteException;
    public void AddUser(String Username, String Password) throws RemoteException;
    public void DeleteUser(int UserID) throws RemoteException;
    public void UpdateUser(User user) throws RemoteException;
}
                                                                                                                    