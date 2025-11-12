package com.biblioteca.controller;

import com.biblioteca.dto.LivroDto;
import com.biblioteca.service.BibliotecaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
public class BibliotecaRestController {

    private final BibliotecaService service;

    public BibliotecaRestController(BibliotecaService service) {
        this.service = service;
    }

    @GetMapping
    public List<LivroDto> listar() {
        return service.listarLivros().stream()
                .map(LivroDto::from)
                .toList();
    }

    @GetMapping("/{titulo}")
    public LivroDto buscar(@PathVariable String titulo) {
        return LivroDto.from(service.buscarLivro(titulo));
    }

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody LivroDto dto) {
        service.adicionarLivro(dto.toModel());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{titulo}")
    public ResponseEntity<Void> atualizar(@PathVariable String titulo, @RequestBody LivroDto dto) {
        service.atualizarLivro(titulo, dto.toModel());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{titulo}")
    public ResponseEntity<Void> remover(@PathVariable String titulo) {
        service.removerLivro(titulo);
        return ResponseEntity.ok().build();
    }
}