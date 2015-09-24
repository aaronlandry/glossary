package com.superiad.glossary.authentication;

import com.superiad.glossary.controllers.environment.WebContext;
import com.superiad.glossary.model.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Aaron
 */
@Service
public class AuthenticationSuccessHandlerImpl extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private WebContext webContext;
    
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest hsr, HttpServletResponse hsr1, Authentication a) throws IOException, ServletException {
        super.onAuthenticationSuccess(hsr, hsr1, a);
        /* DO WHATEVER CUSTOM THING WE WANT TO DO ON LOGIN */
        try {
            SpringUser sUser = webContext.getSpringUser();
            User user = webContext.getActiveUser();
            sUser.setUser(user);
            user.update();
        }
        catch(Exception e) {
            LoggerFactory.getLogger(AuthenticationSuccessHandlerImpl.class)
                .error("Error authenticating user.",e);
        }
    }
    
}
