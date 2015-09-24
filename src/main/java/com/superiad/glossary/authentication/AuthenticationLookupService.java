package com.superiad.glossary.authentication;

import com.superiad.glossary.model.User;
import com.superiad.glossary.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * A service that looks up user based on username and wraps it in a SpringUser object.
 * @author Aaron
 */
@Service
public class AuthenticationLookupService implements UserDetailsService {
    
    @Autowired
    @Qualifier("userRepositoryImpl")
    private UserRepository userRepository;
 
    @Override
    public UserDetails loadUserByUsername(String username) {
        if (username != null && !username.isEmpty()) {
            try {
                User user = userRepository.findByUsername(username);
                if (user == null) {
                    return null;
                }
                return new SpringUser(user);
            }
            catch(Exception e) {
                return null;
                //throw new RuntimeException(e);
            }
        }
        return null;
  }
    
}