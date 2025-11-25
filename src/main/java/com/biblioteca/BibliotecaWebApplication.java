package com.biblioteca;

import com.biblioteca.controller.BibliotecaRestController;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.BibliotecaService;
import io.javalin.Javalin;

public class BibliotecaWebApplication {

    public static void main(String[] args) {
        LivroRepository livroRepository = new LivroRepository();
        BibliotecaService service = new BibliotecaService(livroRepository);

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/static";
                staticFileConfig.location = io.javalin.http.staticfiles.Location.CLASSPATH;
            });
        }).start(7000);

        app.get("/", ctx -> ctx.redirect("/lista.html"));

        new BibliotecaRestController(app, service);
    }
}
