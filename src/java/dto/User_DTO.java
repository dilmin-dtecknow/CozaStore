package dto;

import com.google.gson.annotations.Expose;
import entity.User_Type;
import java.io.Serializable;

public class User_DTO implements Serializable{
    
    @Expose
    private String first_name;
    
    @Expose
    private String last_name;
    
    @Expose
    private String email;
    
    @Expose(deserialize = true,serialize = false)
    private String password;
    
    @Expose
    private User_Type user_type;

    public User_DTO() {
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User_Type getUser_type() {
        return user_type;
    }

    public void setUser_type(User_Type user_type) {
        this.user_type = user_type;
    }

    public String getCode() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
