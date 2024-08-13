/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.keygendata;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

public class Keygendata {
    public static void main(String[] args) {
        try {
            // Tạo cặp khóa RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            String privateKeyStr = Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded());
            String publicKeyStr = Base64.getEncoder().encodeToString(pair.getPublic().getEncoded());
            PrivateKey privateKey = pair.getPrivate();

            // Kết nối đến cơ sở dữ liệu MySQL
            String url = "jdbc:mysql://localhost:3306/key_stor";
            String user = "root";
            String password = "123456";
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                // Lưu khóa vào bảng keys
                String sql = "INSERT INTO keygen (public_key, private_key) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, publicKeyStr);
                    pstmt.setString(2, privateKeyStr);
                    pstmt.executeUpdate();
                    System.out.println("Keys generated and saved to database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
                        String privateKeyPath = "private_key.pem";
            try (FileOutputStream fos = new FileOutputStream("D:/keygen/keygen1.pem")) {
                fos.write("-----BEGIN PRIVATE KEY-----\n".getBytes());
                fos.write(Base64.getMimeEncoder().encode(privateKey.getEncoded()));
                fos.write("\n-----END PRIVATE KEY-----\n".getBytes());
            }catch (IOException e) {
            e.printStackTrace();
        }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}


