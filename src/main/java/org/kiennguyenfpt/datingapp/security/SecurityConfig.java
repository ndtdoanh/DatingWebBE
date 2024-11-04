package org.kiennguyenfpt.datingapp.security;

import org.kiennguyenfpt.datingapp.repositories.UserRepository;
import org.kiennguyenfpt.datingapp.services.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Thêm cấu hình CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll() // Cho phép tất cả truy cập vào auth
                        .requestMatchers("/api/v1/payment/**").permitAll()
                        .requestMatchers("/api/v1/profiles/me").authenticated() // Cần xác thực
                        .requestMatchers("/api/v1/users/update-profile").authenticated() // Cần xác thực
                        .requestMatchers("/api/v1/swipes/swipe").authenticated() // Cần xác thực
                        .requestMatchers("/api/v1/matches").authenticated() // Cần xác thực
                        .requestMatchers("/api/v1/messages/send").authenticated() // Cần xác thực
                        .requestMatchers("/api/v1/messages/match/{matchId}").authenticated() // Cần xác thực
                        .requestMatchers("/ws/**").permitAll() // Cho phép truy cập công khai đến WebSocket endpoint
                        .requestMatchers("/api/subscribe/**").permitAll() // Cho phép có thể kết nối và lắng nghe sự kiện từ server
                        .requestMatchers("/api/v1/admin/**").hasRole("Admin") // Chỉ cho phép người dùng có vai trò ADMIN truy cập
                        .anyRequest().authenticated() // Tất cả các yêu cầu khác đều cần xác thực
                )
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Không giữ phiên
                );

        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:8081")); // Chỉ định các nguồn (origins) được phép truy cập
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Chỉ định các phương thức được phép
        configuration.setAllowedHeaders(List.of("*")); // Cho phép tất cả các header
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type")); // Các header được cho phép hiển thị
        configuration.setAllowCredentials(true); // Cho phép gửi thông tin xác thực (credentials)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Áp dụng cấu hình cho tất cả các endpoint
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter(jwtUtil(), userDetailsService());
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }
}
