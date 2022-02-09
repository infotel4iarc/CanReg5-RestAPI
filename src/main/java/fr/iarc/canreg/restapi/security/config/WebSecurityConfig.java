package fr.iarc.canreg.restapi.security.config;

import fr.iarc.canreg.restapi.security.password.CustomPasswordEncoder;
import fr.iarc.canreg.restapi.security.service.CanregDbDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * Web security configuration, using Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final CustomPasswordEncoder passwordEncoder;
  
  @Autowired
  private CanregDbDetailService userDetailsService;

  /**
   * Constructor.
   */
  public WebSecurityConfig() {
    this.passwordEncoder = new CustomPasswordEncoder();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // All requests send to the Web Server request must be authenticated
    http.authorizeRequests().anyRequest().authenticated();

    // Use  authenticate user/password
    http.httpBasic();

    //Every request must reauthenticate
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.csrf().disable();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
        .passwordEncoder(this.passwordEncoder)
        .and()
        .authenticationProvider(authenticationProvider());
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
    final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(this.passwordEncoder);
    return authProvider;
  }

}