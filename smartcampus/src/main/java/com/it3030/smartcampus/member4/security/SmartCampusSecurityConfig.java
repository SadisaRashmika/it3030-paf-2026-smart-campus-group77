package com.it3030.smartcampus.member4.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SmartCampusSecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new PlainOrBcryptPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return username -> userRepository.findByEmail(username)
				.map(user -> toUserDetails(user, passwordEncoder))
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		http.csrf(csrf -> csrf.disable());
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/", "/index.html", "/ui/**", "/error", "/api/public/**").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.requestMatchers("/api/lecturer/**").hasRole("LECTURER")
				.requestMatchers("/api/student/**").hasRole("STUDENT")
				.anyRequest().authenticated());
		http.formLogin(form -> form.disable());
		http.httpBasic(basic -> basic.disable());
		http.exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) ->
				response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized")));
		return http.build();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	private UserDetails toUserDetails(UserAccount user, PasswordEncoder passwordEncoder) {
		String passwordHash = user.getPasswordHash();
		if (passwordHash == null || passwordHash.isBlank()) {
			passwordHash = passwordEncoder.encode("inactive-account");
		}
		return User.withUsername(user.getEmail())
				.password(passwordHash)
				.authorities(user.getRole().authority())
				.accountLocked(!user.isActive())
				.disabled(!user.isActive())
				.build();
	}
}