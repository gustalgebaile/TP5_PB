package com.biblioteca.controller;

import com.biblioteca.dto.LivroDto;
import com.biblioteca.service.BibliotecaService;
import io.javalin.Javalin;

public class BibliotecaRestController {

    private final BibliotecaService service;

    public BibliotecaRestController(Javalin app, BibliotecaService service) {
        this.service = service;

        app.get("/api/livros", ctx -> {
            ctx.json(service.listarLivros().stream()
                    .map(LivroDto::from).toList());
        });

        app.get("/api/livros/{titulo}", ctx -> {
            String titulo = ctx.pathParam("titulo");
            ctx.json(LivroDto.from(service.buscarLivro(titulo)));
        });

        app.post("/api/livros", ctx -> {
            LivroDto dto = ctx.bodyAsClass(LivroDto.class);
            service.adicionarLivro(dto.toModel());
            ctx.status(200);
        });

        app.put("/api/livros/{titulo}", ctx -> {
            String titulo = ctx.pathParam("titulo");
            LivroDto dto = ctx.bodyAsClass(LivroDto.class);
            service.atualizarLivro(titulo, dto.toModel());
            ctx.status(200);
        });

        app.delete("/api/livros/{titulo}", ctx -> {
            String titulo = ctx.pathParam("titulo");
            service.removerLivro(titulo);
            ctx.status(200);
        });
    }
}
