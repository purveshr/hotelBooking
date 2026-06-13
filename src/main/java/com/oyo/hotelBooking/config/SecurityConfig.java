
package com.oyo.hotelBooking.config;

import com.oyo.hotelBooking.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/user/signUp",
                                "/user/login",
                                "/user/welcome",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // public read APIs
                        .requestMatchers(HttpMethod.GET, "/hotels/**").permitAll()
                        .requestMatchers("/rooms/hotel/**").permitAll()

                        // hotels
                        .requestMatchers(HttpMethod.POST, "/hotels").hasAnyRole("ADMIN", "HOTEL_OWNER")
                        .requestMatchers(HttpMethod.POST, "/hotels/batch").hasAnyRole("ADMIN", "HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/hotels/**").hasAnyRole("ADMIN", "HOTEL_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/hotels/**").hasAnyRole("ADMIN", "HOTEL_OWNER")

                        // rooms
                        .requestMatchers(HttpMethod.POST, "/rooms").hasRole("HOTEL_OWNER")
                        .requestMatchers(HttpMethod.PUT, "/rooms/**").hasAnyRole("ADMIN", "HOTEL_OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/rooms/**").hasAnyRole("ADMIN", "HOTEL_OWNER")

                        // users
                        .requestMatchers("/user/changeUserRole").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable());

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
