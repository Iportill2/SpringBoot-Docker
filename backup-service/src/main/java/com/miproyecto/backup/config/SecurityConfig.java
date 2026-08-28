package com.miproyecto.backup.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> {
            })
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            @Value("${backup.security.user:backup_service}") String username,
            @Value("${backup.security.password:}") String password,
            PasswordEncoder passwordEncoder) {

        if (password == null || password.isBlank() || "change_me_backup".equals(password)) {
            throw new IllegalStateException(
                    "Backup-service no está configurado: define BACKUP_SERVICE_USER / BACKUP_SERVICE_PASSWORD");
        }

        UserDetails serviceUser = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("SERVICE")
                .build();

        return new InMemoryUserDetailsManager(serviceUser);
    }
}
