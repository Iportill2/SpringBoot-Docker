package com.miproyecto.clienterest.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/login",
                        "/register",
                        "/register/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/images/**",
                        "/webjars/**",
                        "/favicon.ico",
                        "/favicon-32.png",
                        "/error",
                        "/legal/**").permitAll()
                .requestMatchers("/menu/admin/**", "/menu/backups/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST,
                        "/menu/crm/crear",
                        "/menu/crm/eliminar/**",
                        "/menu/clientes",
                        "/menu/clientes/editar/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/menu/crm/editar/**").hasAnyRole("ADMIN", "EMPLEADO")
                .requestMatchers("/menu/**").authenticated()
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));
        return http.build();
    }
}