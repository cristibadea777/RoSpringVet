package eu.badeacristian.RoSpringVet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import eu.badeacristian.RoSpringVet.services.UserService;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserService userService;
	
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
	
	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
	
    @Autowired
    private Http403ForbiddenEntryPoint forbiddenEntryPoint;

    @Bean
    public Http403ForbiddenEntryPoint forbiddenEntryPoint() {
        return new Http403ForbiddenEntryPoint();
    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
		
		.authorizeRequests()

		.antMatchers(
					"/registration**",
	                "/templates/**",
	                "/fragments/**",
	                "/js/**",
	                "/images/**").permitAll()

		//doar angajatii pot vizualiza stapanii
		.antMatchers("/stapani").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziStapani/**").hasAuthority("ROL_ANGAJAT") 
		
		//doar angajatii pot vizualiza animalele, animale stapan
		.antMatchers("/animale").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziAnimale/**").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziAnimaleStapan/**").hasAuthority("ROL_ANGAJAT") 
		
		
		
		//doar angajatii pot vizualiza programarile
		.antMatchers("/programari").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziProgramari/**").hasAuthority("ROL_ANGAJAT")
		
		 //doar angajatii pot vizualiza vizitele
		.antMatchers("/vizite").hasAuthority("ROL_ANGAJAT")
		.antMatchers("/veziVizite/**").hasAuthority("ROL_ANGAJAT")
		
		//doar angajatii pot vizualiza vizitele unui stapan, animal
		.antMatchers("/viziteStapan/**").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziViziteStapan/**").hasAuthority("ROL_ANGAJAT")
		.antMatchers("/viziteAnimal/**").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziViziteAnimal/**").hasAuthority("ROL_ANGAJAT") 
		
		//doar angajatii pot vizualiza programarile unui stapan, animal
		.antMatchers("/programariStapan/**").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziProgramariStapan/**").hasAuthority("ROL_ANGAJAT")
		.antMatchers("/programariAnimal/**").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziProgramariAnimal/**").hasAuthority("ROL_ANGAJAT")
		
		//doar angajatii pot vizualiza tratamentele
		.antMatchers("/tratamente").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziTratamente/**").hasAuthority("ROL_ANGAJAT") 		
		
		//doar angajatii pot vizualiza programarile neconfirmate
		.antMatchers("/programariNeconfirmate").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziProgramariNeconfirmate/**").hasAuthority("ROL_ANGAJAT")
		
		//doar angajatii pot vizualiza programarile stapan neconfirmate
		.antMatchers("/programariStapanNeconfirmate/**").hasAuthority("ROL_ANGAJAT") 
		.antMatchers("/veziProgramariStapanNeconfirmate/**").hasAuthority("ROL_ANGAJAT")
		
		//doar angajatii pot vizualiza profilul animalului
		.antMatchers("/profilAnimal/**").hasAuthority("ROL_ANGAJAT") 
		
		//doar angajatii pot vizualiza profilul stapanului
		.antMatchers("/profilStapan/**").hasAuthority("ROL_ANGAJAT")
		
		//doar angajatii pot vizualiza tratamentele active animal
		.antMatchers("/tratamenteActive/**").hasAuthority("ROL_ANGAJAT")
		.antMatchers("/veziTratamenteAnimal/**").hasAuthority("ROL_ANGAJAT")
		

		
		.and()
		.formLogin()
		.loginPage("/login")
		.permitAll()
		
		.and()
		.logout()
		.invalidateHttpSession(true)
		.clearAuthentication(true)
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.logoutSuccessUrl("/login?logout")
		.permitAll()
		
		//pt a ne lasa sa incarcam imagini
		.and()
        .httpBasic()
        .authenticationEntryPoint(forbiddenEntryPoint)
        .and()
        .csrf().disable();
	
		
	}

}
