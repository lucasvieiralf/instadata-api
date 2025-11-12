package br.com.grape.accessmanager.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.grape.accessmanager.config.errors.CustomAccessDeniedHandler;
import br.com.grape.accessmanager.config.errors.CustomAuthEntryPoint;
import br.com.grape.accessmanager.config.filter.JwtAuthFilter;
import br.com.grape.accessmanager.service.impl.AppUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    // == Dependências Injetadas ==
    private final JwtAuthFilter jwtAuthFilter;
    private final AppUserDetailsServiceImpl appUserDetailsService;

    // Handlers de erro customizados
    private final CustomAuthEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /**
     * Define o encoder de senhas (BCrypt).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * O "Provedor de Autenticação".
     * Define como o Spring Security busca usuários e verifica senhas.
     * (Versão corrigida, usando o construtor)
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // 1. Cria o provider passando o UserDetailsService
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(appUserDetailsService);
        // 2. Configura o encoder
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * O Gerenciador de Autenticação.
     * Usado pelo AuthService para processar o login.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Configuração global de CORS.
     * Define quais origens (frontends) podem acessar a API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Especifique aqui as origens do seu frontend
        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200" // Ex: Angular
        // "https://dominio-de-producao.com"
        ));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Cabeçalhos permitidos
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Cache-Control"));

        // Permite o envio de credenciais
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica para todas as rotas
        return source;
    }

    // == O Bean Principal da Cadeia de Filtros ==

    /**
     * A Cadeia de Filtros de Segurança: A configuração principal.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Desabilita CSRF, HTTP Basic e Form Login (API 100% Stateless)
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())

                // Habilita a configuração de CORS definida no Bean 'corsConfigurationSource'
                .cors(Customizer.withDefaults())

                // Configuração de Autorização de Rotas (Whitelist)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/**", // Endpoints de autenticação
                                "/v3/api-docs/**", // Documentação Swagger/OpenAPI
                                "/swagger-ui/**", // UI do Swagger
                                "/actuator/health", // Health Check
                                "/error")
                        .permitAll()
                        .anyRequest().authenticated() // Todas as outras rotas exigem autenticação
                )

                // Configuração do Tratamento de Exceções
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthEntryPoint) // Trata erros 401 (Não Autorizado)
                        .accessDeniedHandler(customAccessDeniedHandler) // Trata erros 403 (Proibido)
                )

                // Configuração da Política de Sessão
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Nunca criar sessões
                )

                // Define o provedor de autenticação customizado
                .authenticationProvider(authenticationProvider())

                // Adiciona o filtro JWT antes do filtro padrão
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}