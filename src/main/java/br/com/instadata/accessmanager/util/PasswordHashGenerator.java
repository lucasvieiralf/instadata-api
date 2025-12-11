package br.com.instadata.accessmanager.util;

import org.springframework.boot.CommandLineRunner;
// import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordHashGenerator implements CommandLineRunner {

    // private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // String senha = "superadmin123"; // Senha que vamos usar para SQLs (Altera depois ou não rs)
        // String hash = passwordEncoder.encode(senha);
        
        // System.out.println("==========================================================");
        // System.out.println("HASH GERADO PARA A SENHA: " + senha);
        // System.out.println(hash);
        // System.out.println("==========================================================");
    }
}