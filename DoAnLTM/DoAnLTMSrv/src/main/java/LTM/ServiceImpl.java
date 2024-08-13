/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LTM;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.*;
import java.util.Base64;


public class ServiceImpl extends UnicastRemoteObject implements Service {
private Connection connection;

    public ServiceImpl() throws RemoteException, SQLException {
        super();
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/key_stor", "root", "123456");
    }

    @Override
    public String verifyMessage(String message, String digitalSignatureStr) throws RemoteException {
        try {
            // Lấy public key từ cơ sở dữ liệu dựa trên ID (hoặc định danh khác)
            int userId = 3; // Thay đổi để phù hợp với ID của người dùng
            String query = "SELECT public_key FROM keygen WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            String publicKeyStr = "";
            if (resultSet.next()) {
                publicKeyStr = resultSet.getString("public_key");
            } else {
                return "Public key not found for user with ID: " + userId;
            }

            // Convert public key từ string sang đối tượng PublicKey
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            
            // Convert digital signature từ string sang byte array
            byte[] digitalSignature = Base64.getDecoder().decode(digitalSignatureStr);


            // Xác minh chữ ký số bằng public key
            byte[] messageBytes = message.getBytes();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(messageBytes);
            boolean verified = signature.verify(digitalSignature);
            
            if (verified) {
                return "VERIFIED;HUFLIT";
            } else {
                return "VERIFICATION FAILED";
            }
        }catch (NoSuchAlgorithmException | InvalidKeySpecException | SignatureException | SQLException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error occurred: " + e.getMessage();
        }
    }
    @Override
    public List<String> getAllUsernames() throws RemoteException {
        List<String> usernames = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_database", "root", "123456");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT username FROM users")) {
            while (rs.next()) {
                usernames.add(rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usernames;
    }
    @Override
    public List<String> getAllPasswords() throws RemoteException {
        List<String> passwords = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_database", "root", "123456");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT password FROM users")) {
            while (rs.next()) {
                passwords.add(rs.getString("password"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passwords;
    }
    @Override
    public List<User> Getallusers() throws RemoteException {
        List<User> userList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionDatabase.getConnection();

            // Chuẩn bị câu truy vấn SQL
            String sql = "SELECT * FROM users";
            stmt = conn.prepareStatement(sql);

            // Thực thi truy vấn và nhận kết quả
            rs = stmt.executeQuery();

            // Duyệt qua các dòng kết quả và thêm vào danh sách User
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("username");
                String password = rs.getString("password");
                User user = new User(id, fullName,password);
                userList.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Đóng các đối tượng ResultSet, PreparedStatement và Connection
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return userList;
    }

    @Override
    public void UpdateUser(User user) throws RemoteException {
        Connection conn = null;
        PreparedStatement stmt = null;

    try {
        conn = ConnectionDatabase.getConnection();

        String sql = "UPDATE users SET username = ?, password = ? WHERE id = ?";
        stmt = conn.prepareStatement(sql);
        System.out.println("New username >>>>>>>: " + user.getFullname());
        System.out.println("New password >>>>>>>: " + user.getPassword());
        stmt.setString(1, user.getFullname());
        stmt.setString(2, user.getPassword());
        stmt.setInt(3, user.getId());
    
        // Thực thi truy vấn
        stmt.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        // Đóng PreparedStatement và Connection
        try {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

    @Override
    public void AddUser(String fullName, String password) throws RemoteException {
    try {
        Connection conn = ConnectionDatabase.getConnection();
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, fullName);
        stmt.setString(2, password);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
  
    
    @Override
    public void DeleteUser(int UserID) throws RemoteException {
        try {
        Connection conn = ConnectionDatabase.getConnection();
        String sql = "DELETE FROM users WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, UserID);
        stmt.executeUpdate();
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }
}
