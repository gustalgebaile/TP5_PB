package com.biblioteca.dto;

import com.biblioteca.model.Categoria;
import com.biblioteca.model.Livro;

public record LivroDto(String titulo, String autor, String categoria) {
    public static LivroDto from(Livro l) {
        return new LivroDto(l.getTitulo(), l.getAutor(), l.getCategoria().name());
    }
    public Livro toModel() {
        Categoria cat = Categoria.valueOf(categoria.toUpperCase());
        return new Livro(titulo, autor, cat);
    }
}