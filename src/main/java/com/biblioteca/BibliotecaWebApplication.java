package com.biblioteca;

import com.biblioteca.dto.LivroDto;
import com.biblioteca.service.BibliotecaService;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;


public class BibliotecaWebApplication {

    public static void main(String[] args) {
        LivroRepository livroRepository = new LivroRepository();
        BibliotecaService service = new BibliotecaService(livroRepository);

        Javalin app = Javalin.create().start(8080);

        app.routes(() -> {
            path("api/livros", () -> {
                get(ctx -> {
                    ctx.json(service.listarLivros().stream().map(LivroDto::from).toList());
                });
                get("{titulo}", ctx -> {
                    String titulo = ctx.pathParam("titulo");
                    ctx.json(LivroDto.from(service.buscarLivro(titulo)));
                });
                post(ctx -> {
                    LivroDto dto = ctx.bodyAsClass(LivroDto.class);
                    service.adicionarLivro(dto.toModel());
                    ctx.status(200);
                });
                put("{titulo}", ctx -> {
                    String titulo = ctx.pathParam("titulo");
                    LivroDto dto = ctx.bodyAsClass(LivroDto.class);
                    service.atualizarLivro(titulo, dto.toModel());
                    ctx.status(200);
                });
                delete("{titulo}", ctx -> {
                    String titulo = ctx.pathParam("titulo");
                    service.removerLivro(titulo);
                    ctx.status(200);
                });
            });
        });
    }
}