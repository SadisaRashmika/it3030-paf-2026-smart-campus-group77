package com.it3030.smartcampus.member4.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletRequest;

import com.it3030.smartcampus.member4.model.UserAccount;
import com.it3030.smartcampus.member4.repository.UserRepository;
import com.it3030.smartcampus.member4.service.NotificationService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
				.filter(user -> {
					if (!user.isActive()) {
						throw new UsernameNotFoundException("Account is deactivated. Please activate your account.");
					}
					return true;
				})
				.map(user -> toUserDetails(user, passwordEncoder))
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http,
										DatabaseBackedOAuth2UserService databaseBackedOAuth2UserService,
										DatabaseBackedOidcUserService databaseBackedOidcUserService,
										NotificationService notificationService,
										ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider,
										@Value("${app.frontend.base-url:http://localhost:8081/ui/index.html}") String frontendBaseUrl) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		http.csrf(csrf -> csrf.disable());
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
		http.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/", "/index.html", "/ui/**", "/error", "/api/public/**", "/oauth2/**", "/login/oauth2/**").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.requestMatchers("/api/member2/bookings/**").authenticated()
				.requestMatchers("/api/timetable/**").hasRole("TIMETABLE_MANAGER")
				.requestMatchers("/api/lecturer/**").hasRole("LECTURER")
				.requestMatchers("/api/student/**").hasRole("STUDENT")
				.anyRequest().authenticated());
		ClientRegistrationRepository clientRegistrationRepository = clientRegistrationRepositoryProvider.getIfAvailable();
		if (clientRegistrationRepository != null) {
			http.oauth2Login(oauth2 -> oauth2
					.authorizationEndpoint(endpoint -> endpoint.authorizationRequestResolver(
							authorizationRequestResolver(clientRegistrationRepository)))
					.userInfoEndpoint(userInfo -> userInfo
							.userService(databaseBackedOAuth2UserService)
							.oidcUserService(databaseBackedOidcUserService))
					.successHandler((request, response, authentication) -> {
						String principal = resolvePrincipal(authentication);
						notificationService.createLoginAlert(principal, "Google Login");
						String target = buildAuthRedirectUrl(frontendBaseUrl, "google-success", null);
						response.sendRedirect(target);
					})
					.failureHandler((request, response, exception) -> {
						String target = buildAuthRedirectUrl(frontendBaseUrl, "google-failed", exception == null ? null : exception.getMessage());
						response.sendRedirect(target);
					}));
		}
		http.formLogin(form -> form.disable());
		http.httpBasic(basic -> basic.disable());
		http.exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) ->
				response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized")));
		return http.build();
	}

	private String resolvePrincipal(org.springframework.security.core.Authentication authentication) {
		if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oauth2User) {
			Object emailAttr = oauth2User.getAttributes().get("email");
			if (emailAttr instanceof String email && !email.isBlank()) {
				return email.trim().toLowerCase();
			}
		}

		return authentication == null ? null : authentication.getName();
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	private UserDetails toUserDetails(UserAccount user, PasswordEncoder passwordEncoder) {
		String passwordHash = user.getPasswordHash();
		// Check if temporary password exists and hasn't expired
		if (user.getTemporaryPasswordHash() != null && !user.temporaryPasswordExpired(java.time.Instant.now())) {
			// Use temporary password for authentication
			passwordHash = user.getTemporaryPasswordHash();
		} else if (passwordHash == null || passwordHash.isBlank()) {
			passwordHash = passwordEncoder.encode("inactive-account");
		}
		return User.withUsername(user.getEmail())
				.password(passwordHash)
				.authorities(user.getRole().authority())
				.accountLocked(!user.isActive())
				.disabled(!user.isActive())
				.build();
	}

	private String sanitizeFrontendUrl(String frontendBaseUrl) {
		if (frontendBaseUrl == null || frontendBaseUrl.isBlank()) {
			return "http://localhost:8081/ui/index.html";
		}

		String trimmed = frontendBaseUrl.trim();
		return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
	}

	private String buildAuthRedirectUrl(String frontendBaseUrl, String authStatus, String reason) {
		String base = sanitizeFrontendUrl(frontendBaseUrl);
		String separator = base.contains("?") ? "&" : "?";
		StringBuilder builder = new StringBuilder(base)
				.append(separator)
				.append("auth=")
				.append(URLEncoder.encode(authStatus, StandardCharsets.UTF_8));

		if (reason != null && !reason.isBlank()) {
			builder.append("&reason=")
					.append(URLEncoder.encode(reason, StandardCharsets.UTF_8));
		}

		return builder.toString();
	}

	private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
			ClientRegistrationRepository clientRegistrationRepository) {
		DefaultOAuth2AuthorizationRequestResolver delegate =
				new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

		return new OAuth2AuthorizationRequestResolver() {
			@Override
			public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
				return customize(delegate.resolve(request));
			}

			@Override
			public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
				return customize(delegate.resolve(request, clientRegistrationId));
			}

			private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest request) {
				if (request == null) {
					return null;
				}

				Map<String, Object> additionalParameters = new LinkedHashMap<>(request.getAdditionalParameters());
				additionalParameters.put("prompt", "select_account");

				return OAuth2AuthorizationRequest.from(request)
						.additionalParameters(additionalParameters)
						.build();
			}
		};
	}
}