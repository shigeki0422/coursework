package com.jpassion.myproject.mydomain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jpassion.myproject.validation.AirportCodeValid;
import com.jpassion.myproject.validation.DateValid;
import com.jpassion.myproject.validation.NumbersValid;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findUsersesByNameEquals", "findUsersesByUsernameEquals" })
public class Users {

    /**
     * name
     */
    @NotNull
    @Size(max = 100)
    private String name;

    /**
     * address
     */
    @NotNull
    private String address;

    /**
     * email
     */
    @NotNull
    @Pattern(regexp = "^([0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-\\w]*[0-9a-zA-Z]\\.)+[a-zA-Z]{2,9})$")
    private String email;

    /**
     * username
     */
    @NotNull
    @Size(max = 50)
    private String username;

    /**
     * password
     */
    @NotNull
    @Size(max = 255)
    private String password;

    public String getPassword() {
        return this.password;
    }

    /**
     * Set password which is to be encrypted 
     * @param password
     */
    public void setPassword(String password) {
    	try{
            this.password = sha256(password);
    	}
    	catch(NoSuchAlgorithmException ex){
    		
    	}
    }

    /**
     * Encrypt string
     * @param original
     * @return encrypted string
     * @throws NoSuchAlgorithmException
     */
    public String sha256(String original) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(original.getBytes());
        byte[] digest = md.digest();
        return new String(Hex.encodeHexString(digest));
    }

    /**
     * role of the user. ROLE_USER for normal user, ROLE_ADMIN for admin
     */
    @NotNull
    @Value("ROLE_USER")
    @Size(max = 50)
    private String roles;

    /**
     * credit card number
     */
    @Size(min = 16, max = 16)
    @NumbersValid
    private String cardNumber;

    /**
     * expiration date of the credit card
     */
    @Size(min = 4, max = 4)
    @DateValid
    private String expirationDate;
    

    /**
     * Returns user who is logging in at the moment
     * @return user who is logging in
     */
    public static Users getLoggedInUser(){
    	String username = SecurityContextHolder.getContext().getAuthentication().getName();
       	List<Users> result = Users.findUsersesByUsernameEquals(username).getResultList();

    	if(result.size() == 0)
            return null;
    	
    	return result.get(0);
    }
    
    /**
     * Returns the sequence number of the user
     * @return sequence number of the user
     */
    public String getSequencenumber(){
    	return String.valueOf(this.getId() % 1000);
    }
}
