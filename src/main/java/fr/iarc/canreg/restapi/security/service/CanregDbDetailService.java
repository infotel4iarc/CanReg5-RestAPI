package fr.iarc.canreg.restapi.security.service;

import canreg.common.database.User;
import canreg.server.database.CanRegDAO;
import fr.iarc.canreg.restapi.security.user.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom UserDetailsService: read the user in Canreg database.
 */
@Service
public class CanregDbDetailService implements UserDetailsService {

    @Autowired
    private CanRegDAO canRegDAO;

    /**
     * Role required to connect to CanReg API
     */
    @Value("${role:}")
    private String role;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = canRegDAO.getUserByUsername(username);
        if(user != null
          && user.getUserRightLevel().name().equals(role)) {
          return new UserPrincipal(user);
        }
        throw new UsernameNotFoundException("User not found");
    }
}
