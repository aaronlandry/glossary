package com.superiad.glossary.authentication;

import com.superiad.glossary.model.User;
import com.superiad.glossary.persistence.EntityNotFoundException;
import com.superiad.glossary.persistence.UserRepository;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * An extension for the UserDetails class that provides access to the User
 * Entity that spawned it.  This grants the system session-scoped access 
 * (via ContextService) to the currently logged in User entity (rather than
 * just the plain username), avoiding the need for redundant database calls.
 * @author Aaron
 */
@Configurable
public class SpringUser implements Serializable, UserDetails {

    private static final long serialVersionUID = 1201392234549297485L;
    public static final boolean DEFAULT_ENABLED = true;
    public static final boolean DEFAULT_ACCOUNT_NON_EXPIRED = true;
    public static final boolean DEFAULT_CREDENTIALS_NON_EXPIRED = true;
    public static final boolean DEFAULT_ACCOUNT_NON_LOCKED = true;
    private User user;
    private Long id;
    private String originalUserName;
    private Long originalUserID;
    private Collection<GrantedAuthority> authorities = new ArrayList<>();
    @Autowired
    private UserRepository userRepositoryImpl;
    
    public SpringUser(User user) {
        initializeOrReset(user);
    }

    protected final void initializeOrReset(User user) {
        this.user = user;
        this.id = user.getId();
        this.originalUserID = null;
        this.originalUserName = null;
        // WE DON'T ACTUALLY USE THE ROLE
        // THE USER JUST NEEDS TO HAVE ONE
        // (SECURITY IS DONE ON THE SERVICE LEVEL)
        this.authorities.clear();
        this.authorities.addAll(AuthorityUtils.createAuthorityList("ROLE_IRRELEVANT"));
    }
    
    /**
     * Gets the database id of the underlying AbstractUser Entity.
     * @return 
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the underlying AbstractUser Entity.
     * @return AbstractUser entity
     */
    public User getUser() {
        return user;
    }

    public void enableVicarious(User user) {
        User originalUser = getUser();
        initializeOrReset(user);
        originalUserID = originalUser.getId();
        originalUserName = originalUser.toString();
    }
    
    public void disableVicarious() throws EntityNotFoundException {
        initializeOrReset(userRepositoryImpl.findById(originalUserID));
    }
    
    public Long getOriginalUserID() {
        return originalUserID;
    }
    
    public String getOriginalUserName() {
        return originalUserName;
    }
    
    public Boolean getVicarious() {
        return isVicarious();
    }
    
    public Boolean isVicarious() {
        return originalUserID != null;
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Sets the AbstractUser Entity associated with the wrapper.
     * @param user Managed AbstractUser entity.
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return DEFAULT_ACCOUNT_NON_EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return DEFAULT_ACCOUNT_NON_LOCKED;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return DEFAULT_CREDENTIALS_NON_EXPIRED; // WE DO NOT CHECK THIS HERE...
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
}