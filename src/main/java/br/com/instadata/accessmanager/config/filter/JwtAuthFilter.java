package br.com.instadata.accessmanager.config.filter;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.instadata.accessmanager.service.impl.AppUserDetailsServiceImpl;
import br.com.instadata.accessmanager.service.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j // Habilita o 'log'
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserDetailsServiceImpl appUserDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Log de debug: útil para ver se a requisição está chegando
        log.debug("Processando requisição para: {}", request.getRequestURI());

        // 1. Verifica se o header existe e se começa com "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Header 'Authorization' ausente ou inválido. Pulando filtro JWT.");
            filterChain.doFilter(request, response); // Se não, passa para o próximo filtro
            return;
        }

        // 2. Extrai o token (remove o "Bearer ")
        jwt = authHeader.substring(7);
        log.debug("Token JWT extraído.");

        try {
            // 3. Extrai o email de dentro do token
            userEmail = jwtService.extractUsername(jwt);
            log.debug("Email extraído do token: {}", userEmail);

            // 4. Verifica se o email existe E se o usuário ainda não está autenticado
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                log.debug("Usuário não autenticado no contexto. Carregando UserDetails...");
                
                // 5. Carrega os detalhes do usuário do banco de dados
                UserDetails userDetails = this.appUserDetailsService.loadUserByUsername(userEmail);

                // 6. Valida o token (compara com o userDetails e verifica expiração)
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    
                    log.debug("Token é válido. Autenticando usuário: {}", userEmail);

                    // 7. Se válido, cria o token de autenticação para o Spring
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Credenciais (senha) são nulas, devido ao uso de token
                            userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 8. Coloca o usuário no Contexto de Segurança
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Usuário autenticado com sucesso: {}", userEmail);

                } else {
                    // Log de AVISO: Isso é importante!
                    log.warn("Falha ao validar token JWT para usuário: {}", userEmail);
                }
            }

            // 9. Passa a requisição para o próximo filtro da cadeia
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Log de ERRO: Ex. Token expirado, assinatura inválida
            log.error("Erro ao processar token JWT: {}", e.getMessage());
            filterChain.doFilter(request, response);
        }
    }
}