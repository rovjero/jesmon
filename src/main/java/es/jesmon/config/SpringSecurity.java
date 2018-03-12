package es.jesmon.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@Order(1)
public class SpringSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
    private AccessDeniedHandler accessDeniedHandler;

	@Autowired
	AfterLoginFilter afterLoginFilter;
	
    // roles admin allow to access /admin/**
    // roles user allow to access /user/**
    // custom 403 access denied handler
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
					.antMatchers("/login/**", "/").permitAll()
					.antMatchers("/img/**", "/").permitAll()
					.antMatchers("/admin/**").hasAnyRole("ADMIN")
					.antMatchers("/tramitador/**").hasAnyRole("TRAMITADOR")
					.antMatchers("/cliente/**").hasAnyRole("CLIENTE")
					.antMatchers("/*/*Incidencia").hasAnyRole("CLIENTE", "TRAMITADOR")
					.anyRequest().authenticated()
                .and().addFilterAfter(afterLoginFilter, BasicAuthenticationFilter.class)
                .formLogin()
					.loginPage("/login/login")
					.permitAll()
					.and()
                .logout()
					.permitAll()
					.and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    }
    
    // create two users, admin and user
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	
    	final String findUserQuery = "select NIF,password,activo "
				+ "from usuarios where NIF = ?";
		final String findRoles = "select NIF, role from usuarios "
				+ "where NIF = ?";
		
    	auth.jdbcAuthentication().passwordEncoder(passwordEncoder()).dataSource(dataSource()).usersByUsernameQuery(findUserQuery).authoritiesByUsernameQuery(findRoles);
    	
    	/*
        auth.inMemoryAuthentication()
                .withUser("user").password("password").roles("USER")
                .and()
                .withUser("admin").password("password").roles("ADMIN");*/
    }
    
    @Bean(name = "dataSource")
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        driverManagerDataSource.setUrl("jdbc:mysql://localhost:3306/jesmon");
        driverManagerDataSource.setUsername("root");
        driverManagerDataSource.setPassword("1234");
        return driverManagerDataSource;
    }
	
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /*
    @Bean(name = "afterLoginFilter")
    public AfterLoginFilter getAfterLoginFilter() {
    	return new AfterLoginFilter();
    }
    */
}