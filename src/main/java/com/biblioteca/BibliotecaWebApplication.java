package com.biblioteca;

import io.javalin.Javalin;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.BibliotecaService;

@SpringBootApplication
public class BibliotecaWebApplication {

    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
    }

    @Bean
    public LivroRepository livroRepository() {
        return new LivroRepository();
    }

    @Bean
    public BibliotecaService bibliotecaService(LivroRepository repository) {
        return new BibliotecaService(repository);
    }
}