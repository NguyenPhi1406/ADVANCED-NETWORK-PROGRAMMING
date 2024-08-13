/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LTM;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.sql.SQLException;

/**
 *
 * @author SV
 */
public class Server {
    public static void main(String[] args) throws SQLException {
        try {
            // Khởi động RMI registry và đăng ký dịch vụ
            LocateRegistry.createRegistry(15083);
            System.out.println("RMI registry started on port 15083...");

            Service service = new ServiceImpl();
            Naming.rebind("rmi://127.0.0.1:15083/verifyMessage", service);
            Naming.rebind("rmi://127.0.0.1:15083/testHUFLIT", service);
            System.out.println("RMI Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
