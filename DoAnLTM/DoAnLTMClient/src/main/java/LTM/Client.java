/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LTM;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            // Lookup the VerifyService
            Service service = (Service) Naming.lookup("rmi://127.0.0.1:15083/verifyMessage");
            // Call the verifyMessage method
            String message = "Hello From Client";
            String privateKeyPath = "D:/keygen/keygen1.pem";
            String privateKeyStr = new String(Files.readAllBytes(Paths.get(privateKeyPath)))
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");
            
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyStr);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(privateKeySpec);
            
            // Sign the message using the private key
            byte[] messageBytes = message.getBytes();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(messageBytes);
            byte[] digitalSignature = signature.sign();
            String digitalSignatureStr = Base64.getEncoder().encodeToString(digitalSignature);

            
            String result = service.verifyMessage(message, digitalSignatureStr);
            System.out.println("Verification result: " + result);

            if(result.equals("VERIFIED;HUFLIT")){
                String[] responseFromServer = result.split(";");
                // Lookup the UserService
                Service userService = (Service) Naming.lookup("rmi://127.0.0.1:15083/test" + responseFromServer[1]);
                // Call UserService methods here
                Menu(userService);
                //List<String> usernames = service.getAllUsernames();
                //List<String> passwords = service.getAllPasswords();
                // System.out.println("Usernames:");
                //for (String username : usernames) {
                //    System.out.println(username);
                }
                //System.out.println("\nPasswords:");
               // for (String password : passwords) {
              //      System.out.println(password);
                //}
          // }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void Menu(Service iUsers) throws RemoteException{
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== MENU ===");
            System.out.println("1. Xem danh sach user");
            System.out.println("2. Them user moi");
            System.out.println("3. Cap nhap user");
            System.out.println("4. Xoa user");
            System.out.println("5. Thoat chuong trinh");
            System.out.print("Hay nhap so de lua chon (1-5): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character after nextInt()

            switch (choice) {
                case 1:
                List<User> UserList = iUsers.Getallusers();
                System.out.println("danh sach users:");
                for(User user:UserList){
                    System.out.println("ID: " + user.getId() + ", Full name: " 
                        + user.getFullname()+", Password: "+ user.getPassword());
                }
                    break;
                case 2:
                     System.out.print("Nhap username: ");
                     String usernameToAdd = scanner.nextLine();
                     System.out.print("Nhap password: ");
                     String passwordToAdd = scanner.nextLine();
                     iUsers.AddUser(usernameToAdd,passwordToAdd);
                     System.out.println("Dang them username: " + 
                          usernameToAdd+" va passoword: " + passwordToAdd);
                     System.out.println("Them user thanh cong");
                     break;
                case 3:
                    System.out.print("Nhap ID user de cap nhap: ");
                    int userIdToUpdate = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Nhap username moi: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Nhap password moi: ");
                    String newPassword = scanner.nextLine();
                    User updateUser = new User(userIdToUpdate, newUsername, newPassword);
                    iUsers.UpdateUser(updateUser);
                    System.out.println("User cap nhap thanh cong");
                    break;
                case 4:
                    System.out.print("Nhap ID user can xoa: ");
                    int userIdToDelete = scanner.nextInt();
                    iUsers.DeleteUser(userIdToDelete);
                    System.out.println("Xoa user thanh cong.");
                    break;
                case 5:
                    exit = true;
                    System.out.println("Dang thoat");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        }
        scanner.close();
    }
    
}
