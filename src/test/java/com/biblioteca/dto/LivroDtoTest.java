package com.biblioteca.dto;

import com.biblioteca.dto.LivroDto;
import com.biblioteca.model.Categoria;
import com.biblioteca.model.Livro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LivroDtoTest {

    @Test
    void deveConverterModelParaDtoEModel() {
        Livro livro = new Livro("Titulo", "Autor", Categoria.FICCAO);
        LivroDto dto = LivroDto.from(livro);
        assertEquals("Titulo", dto.titulo());
        assertEquals("Autor", dto.autor());
        assertEquals("FICCAO", dto.categoria());

        Livro model = dto.toModel();
        assertEquals(livro.getTitulo(), model.getTitulo());
        assertEquals(livro.getAutor(), model.getAutor());
        assertEquals(livro.getCategoria(), model.getCategoria());
    }
}
