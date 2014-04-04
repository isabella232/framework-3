/*******************************************************************************
 * Copyright Searchbox - http://www.searchbox.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.searchbox.framework.config;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

import com.searchbox.framework.repository.UserRepository;
import com.searchbox.framework.service.AuthUserService;
import com.searchbox.framework.service.OpenIdUserDetailsService;
import com.searchbox.framework.service.SimpleSocialUserDetailsService;

@Configuration
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  protected static final String USE_SECURITY = "use.security";
  
  @Autowired
  private DataSource dataSource;

  @Autowired
  private UserRepository userRepository;

  @Resource
  private Environment env;

  @Override
  public void configure(WebSecurity web) throws Exception {
    if (env.getProperty(USE_SECURITY, Boolean.class, false)) {
      web.ignoring().antMatchers("/js/**", "/img/**", "/css/**", "/assets/**",
          "/static/**");
    } else {
      web.ignoring().antMatchers("/**");
    }
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    if (env.getProperty(USE_SECURITY, Boolean.class, false)) {
      http
      
      // Configures form login
      .formLogin()
          .loginPage("/")
          .loginProcessingUrl("/login/authenticate")
          .failureUrl("/?error=bad_credentials")
          .and()

      // Configures the logout function
      .logout()
          .deleteCookies("JSESSIONID")
          .logoutUrl("/logout")
          .logoutSuccessUrl("/")
          .and()
          
      // Configures url based authorization
      .authorizeRequests()
          // Anyone can access the urls
          .antMatchers("/","/*","/auth/**", "/login/**", "/signin/**",
              "/signup/**", "/user/register/**",
              "/user/reset/**",
              "/user/resetPassword/**").permitAll()
          // The system part is protected
          .antMatchers("/system/**").hasAnyRole("SYSTEM")
          // The admin part is protected
          .antMatchers("/admin/**").hasAnyRole("SYSTEM", "ADMIN")
          // The rest of the our application is protected.
          .antMatchers("/**").hasAnyRole("SYSTEM", "ADMIN", "USER")
          .and()
          
      // Adds the SocialAuthenticationFilter to Spring Security's
      // filter chain.
      .apply(new SpringSocialConfigurer())
          .and()
      .openidLogin()
        .loginPage("/")
        .loginProcessingUrl("/login/openid")
        .failureUrl("/?error=openid_fail")
        .permitAll()
        .authenticationUserDetailsService(openIdUserDetailsService())
        .attributeExchange("https://www.google.com/.*")
            .attribute("email")
                .type("http://axschema.org/contact/email")
                .required(true)
                .and()
            .attribute("firstname")
                .type("http://axschema.org/namePerson/first")
                .required(true)
                .and()
            .attribute("lastname")
                .type("http://axschema.org/namePerson/last")
                .required(true)
                .and()
            .and()
        .attributeExchange(".*yahoo.com.*")
            .attribute("email")
                .type("http://axschema.org/contact/email")
                .required(true)
                .and()
            .attribute("fullname")
                .type("http://axschema.org/namePerson")
                .required(true)
                .and()
            .and()
        .attributeExchange(".*myopenid.com.*")
            .attribute("email")
                .type("http://schema.openid.net/contact/email")
                .required(true)
                .and()
            .attribute("fullname")
                .type("http://schema.openid.net/namePerson")
                .required(true);
    }
  }

  /**
   * Configures the authentication manager bean which processes authentication
   * requests.
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService()).passwordEncoder(
        passwordEncoder());
  }

  /**
   * This is used to hash the password of the user.
   */
  @Bean(name = "passwordEncoder")
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }
  
  @Bean
  public OpenIdUserDetailsService openIdUserDetailsService() {
    return new OpenIdUserDetailsService(userRepository);
  }

  /**
   * This bean is used to load the user specific data when social sign in is
   * used.
   */
  @Bean
  public SocialUserDetailsService simpleSocialUserDetails() {
    return new SimpleSocialUserDetailsService(userDetailsService());
  }

  /**
   * This bean is load the user specific data when form login is used.
   */
  @Override
  @Bean
  public UserDetailsService userDetailsService() {
    return new AuthUserService(userRepository);
  }
  
  
  @Bean
  public PersistentTokenRepository persistentTokenRepository() {
      JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
      tokenRepository.setDataSource(dataSource);
      return tokenRepository;
  }
  
  @Bean
  public RememberMeServices rememberMeServices() {
      PersistentTokenBasedRememberMeServices rememberMeServices = new PersistentTokenBasedRememberMeServices(
              "uniqueSecret",
              userDetailsService(),
              persistentTokenRepository()
      );
      rememberMeServices.setAlwaysRemember(true);
      return rememberMeServices;
  }
}
