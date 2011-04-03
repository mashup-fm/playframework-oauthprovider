package models;
 
import javax.persistence.Entity;

import play.Logger;
import play.db.jpa.Model;
import play.mvc.Scope.Session;
 
@Entity
public class User extends Model {
 
    public String userName;

    public String password;
    
    public String name;
    
    public boolean isAdmin;
    
    public User() {}
    
    public User(String userName, String password, String name, boolean isAdmin) {
		super();
		this.userName = userName;
		this.password = password;
		this.name = name;
		this.isAdmin = isAdmin;
	}
    
    public static User connect(String userName, String password) {
        return find("byUserNameAndPassword", userName, password).first();
    }
    
    public String toString() {
        return name + " (" + userName + ")";
    }
    
    public static User findConnectedUser(String connected) {
    	User user = User.find("byUserName", connected).first();
        return user;
    }
 
}