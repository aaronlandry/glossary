package com.superiad.glossary.controllers.environment;

import com.superiad.glossary.authentication.NoActiveUserException;
import com.superiad.glossary.authentication.SpringUser;
import com.superiad.glossary.model.User;
import com.superiad.glossary.persistence.EntityNotFoundException;
import com.superiad.glossary.persistence.UserRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Service that includes lookups for the current session and session-scoped
 * variables, such as the user.
 * @author Aaron
 */
@Component("webContext")
public class WebContext {
    
    // Injected services/beans
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    @Qualifier("userRepositoryImpl")
    private UserRepository userRepository;
    
    /**
     * Returns the currently logged in user in an attached (persistent) state.
     * @return User entity.
     * @throws NoActiveUserException  
     */
    public User getActiveUser() throws NoActiveUserException {
        Object principal = null;
        try {
            principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }
        catch(Exception e) {
            throw new NoActiveUserException(e);
        }
        if (principal instanceof SpringUser) {
            SpringUser wrapper = (SpringUser)principal;
            // if the user is detached, load a new version
            if (!entityManager.contains(wrapper.getUser())) {
                try {
                    wrapper.setUser(userRepository.findById(wrapper.getId()));
                }
                catch(EntityNotFoundException e) {
                    throw new NoActiveUserException(e);
                }
            }
            return wrapper.getUser();
        } 
        throw new NoActiveUserException();
    }
    
    public SpringUser getSpringUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (SpringUser)principal;
    }
    
    /**
     * Returns the currently logged in user wrapped in a SpringUser container.  This is to be used
     * to lookup embedded session information like an original user (if vicarious).
     * @return User in a SpringUser container
     * @throws NoActiveUserException 
     */
    public SpringUser getWrappedUser() throws NoActiveUserException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof SpringUser) {
            return (SpringUser)principal;
        }
        throw new NoActiveUserException();
    }
    
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.
            currentRequestAttributes()).getRequest();
    }
    
    public String getBaseUrl() {
        return String.format("%s://%s:%d/superiad/",getRequest().getScheme(),  getRequest().getServerName(), getRequest().getServerPort());
    }
    
    public String getClientIp() {
        try {
            return getRequest().getRemoteAddr();
        }
        catch(IllegalStateException e) {
            return "";
        }
    }
    
    public String getRequestParams() {
        try {
            return getRequest().getQueryString();
        }
        catch(IllegalStateException e) {
            return "";
        }
    }
    
    public String getRequestPath() {
        try {
            return getRequest().getServletPath();
        }
        catch(IllegalStateException e) {
            return "";
        }
    }
    
    public String getHttpMethod() {
        try {
            return getRequest().getMethod();
        }
        catch(IllegalStateException e) {
            return "";
        }
    }
    
}
