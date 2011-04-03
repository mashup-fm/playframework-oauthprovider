package controllers;
 
import play.*;
import play.mvc.*;
import play.data.validation.*;
 
import java.util.*;
 
import models.*;
 
@With(Secure.class)
public class Admin extends CRUD {
    
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
        	Logger.info("Connected User: %s", Security.connected());
        	User user = User.findConnectedUser(Security.connected());
            renderArgs.put("user", user.name);
            String userInfo = String.format("%s (%s)", user.name, user.userName);
            renderArgs.put("userInfo", userInfo);
        }
    }
    
    public static void index() {
    	render();
    }
  
}
