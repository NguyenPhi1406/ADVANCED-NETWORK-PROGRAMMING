/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package LTM;

/**
 *
 * @author Admin
 */
import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String fullname;
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public User(int id, String fullname,String password) {
        this.id = id;
        this.fullname = fullname;
        this.password = password;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", full_Name='" + fullname + '\'' + ", password='" + password+
                '}';
    }
}
