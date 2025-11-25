package com.biblioteca.exception;

import io.javalin.http.Context;
import io.javalin.Javalin;
import com.biblioteca.exception.LivroNaoEncontradoException;
import com.biblioteca.exception.LivroDuplicadoException;

public class BibliotecaExceptionHandler {

    public static void register(Javalin app) {
        app.exception(LivroNaoEncontradoException.class, (e, ctx) -> {
            handleNotFound(ctx, e.getMessage());
        });

        app.exception(LivroDuplicadoException.class, (e, ctx) -> {
            handleConflict(ctx, e.getMessage());
        });

        app.exception(Exception.class, (e, ctx) -> {
            handleInternalServerError(ctx, "Erro inesperado: " + e.getMessage());
            e.printStackTrace();
        });
    }

    private static void handleNotFound(Context ctx, String message) {
        ctx.status(404);
        ctx.json(new ErrorResponse(message));
    }

    private static void handleConflict(Context ctx, String message) {
        ctx.status(409);
        ctx.json(new ErrorResponse(message));
    }

    private static void handleInternalServerError(Context ctx, String message) {
        ctx.status(500);
        ctx.json(new ErrorResponse(message));
    }

    static class ErrorResponse {
        public final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
