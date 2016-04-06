package com.jpassion.myproject.web;
import com.jpassion.myproject.mydomain.Users;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.core.userdetails.User;;

@RequestMapping("/userses")
@Controller
@RooWebScaffold(path = "userses", formBackingObject = Users.class)
public class UsersController {

    /**
     * Register page which happens after user input
     * @param users
     * @param bindingResult
     * @param uiModel
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "register", method = RequestMethod.POST, produces = "text/html")
    public String register(@Valid Users users, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
        	users.setPassword("");
            populateEditForm(uiModel, users);
            return "userses/register";
        }
        
        if(Users.findUsersesByNameEquals(users.getUsername()).getResultList().size() != 0){
            return "userses/duplicated_username";
        }
        uiModel.asMap().clear();
        users.persist();
        return "userses/registered";
    }
    
    /**
     * Register page
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "register", params = "form", produces = "text/html")
    public String register(Model uiModel) {
        populateEditForm(uiModel, new Users());
        return "userses/register";
    }
    
    /**
     * Input credit card info page which happens after user input
     * @param users
     * @param bindingResult
     * @param uiModel
     * @param httpServletRequest
     * @return
     */
    @RequestMapping(value = "credit", produces = "text/html")
    public String credit(@Valid Users users, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {

    	if (bindingResult.hasErrors()) {
    		for(org.springframework.validation.ObjectError error : bindingResult.getAllErrors()){
    			//check only card related errors
        		if(error.getCode().equals("DateValid") ||
        				error.getCode().equals("NumbersValid")){
                    populateEditForm(uiModel, users);
                	System.out.println("bbb");
                    return "userses/credit";
        		}
        		System.out.println(error.getCode());
    		}
        }
    	System.out.println("aaa");
        Users loggedInUser = Users.getLoggedInUser();
        if(loggedInUser == null)
        	return "redirect:/login";

        users.setUsername(loggedInUser.getUsername());
        users.setPassword(loggedInUser.getPassword());
        users.setRoles(loggedInUser.getRoles());
        users.setName(loggedInUser.getName());
        users.setAddress(loggedInUser.getAddress());
        users.setEmail(loggedInUser.getEmail());
        users.merge();
        return "redirect:/userses/credit?form";
    }
    
    /**
     * Input credit card info page
     * @param uiModel
     * @return
     */
    @RequestMapping(value = "credit", params = "form", produces = "text/html")
    public String creditForm(Model uiModel) {
    	Users loggedInUser = Users.getLoggedInUser();
    	if(loggedInUser == null)
    		return "redirect:/login";
    
    	populateEditForm(uiModel, loggedInUser);
        return "userses/credit";
    }
}
