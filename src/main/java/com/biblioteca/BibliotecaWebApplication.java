package com.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.BibliotecaService;

@SpringBootApplication
public class BibliotecaWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaWebApplication.class, args);
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