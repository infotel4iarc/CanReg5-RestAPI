package fr.iarc.canreg.restapi.security.user;

import canreg.common.database.User;
import canreg.server.database.CanRegDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    CanRegDAO canRegDAO;

    /**
     * Role required to connect to CanReg API
     */
    @Value("${role:}")
    private String role;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Map<String, User> result = canRegDAO.getUsers();

        result.entrySet().removeIf(entry -> !entry.getValue().getUserRightLevel().name().equals(role));

        result.entrySet().removeIf(entry -> !entry.getValue().getUserName().equals(username));

        User user = result.get(username);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new MyUserPrincipal(user);

    }
}
