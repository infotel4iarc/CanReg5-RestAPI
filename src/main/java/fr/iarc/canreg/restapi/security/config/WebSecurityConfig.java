package fr.iarc.canreg.restapi.security.config;


import canreg.common.database.User;
import canreg.server.database.CanRegDAO;
import fr.iarc.canreg.restapi.security.CustomPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.InMemoryUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${role:}")
    private String role;


    @Autowired
     AuthenticationEntryPoint authEntryPoint;
    @Autowired
    private CanRegDAO canRegDAO;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // All requests send to the Web Server request must be authenticated
        http.authorizeRequests().anyRequest().authenticated();

        // Use AuthenticationEntryPoint to authenticate user/password
        http.httpBasic().authenticationEntryPoint(authEntryPoint);
    }




@Bean
    public CustomPasswordEncoder passwordEncoder() {

    CustomPasswordEncoder sCryptPasswordEncoder = new CustomPasswordEncoder();

        return sCryptPasswordEncoder;
    }


   @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {


        InMemoryUserDetailsManagerConfigurer<AuthenticationManagerBuilder> //
                mngConfig = auth.inMemoryAuthentication();

        mngConfig.getUserDetailsService();

        Map<String, User> result = canRegDAO.getUsers();


        result.entrySet().removeIf(entry -> !entry.getValue().getUserRightLevel().name().equals(role));


        List<User> userList =  new ArrayList(result.values());

        for(User user  : userList){
            mngConfig.withUser(
      org.springframework.security.core.userdetails.User.withUsername(user.getUserName()).password( this.passwordEncoder().encode(String.valueOf(user.getPassword()))).roles(user.getUserRightLevel().name()).build());



        }


    }


}