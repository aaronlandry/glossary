package com.superiad.glossary.controllers.environment;

import com.superiad.glossary.authentication.NoActiveUserException;
import com.superiad.glossary.model.User;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author Aaron
 */
@ControllerAdvice
public class GlobalControllerAdvice {
    
    @Autowired
    private WebContext webContext;
    
    // add user and wrapped user to every request
    @ModelAttribute
    public void populateModel(Model model, HttpServletRequest request) {
        try {
            model.addAttribute("user",webContext.getActiveUser());
            model.addAttribute("wrappedUser",webContext.getWrappedUser());
        }
        catch(NoActiveUserException e) {
            model.addAttribute("user",new User());    // JUST FOR RAW ENROLLMENT
        }
    }
    
}
