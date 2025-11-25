package com.biblioteca.service;

import com.biblioteca.exception.LivroDuplicadoException;
import com.biblioteca.exception.LivroNaoEncontradoException;
import com.biblioteca.model.Categoria;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.BibliotecaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BibliotecaServiceTest {

    private BibliotecaService service;

    @BeforeEach
    void setUp() {
        service = new BibliotecaService(new LivroRepository());
    }

    @Test
    void deveAdicionarLivroComSucesso() {
        Livro livro = new Livro("Titulo", "Autor", Categoria.FICCAO);
        service.adicionarLivro(livro);
        Livro buscado = service.buscarLivro("Titulo");
        assertEquals(livro, buscado);
    }

    @Test
    void deveLancarDuplicadoAoAdicionar() {
        Livro livro = new Livro("Titulo", "Autor", Categoria.FICCAO);
        service.adicionarLivro(livro);
        assertThrows(LivroDuplicadoException.class, () -> service.adicionarLivro(livro));
    }

    @Test
    void deveAtualizarLivroComSucesso() {
        Livro original = new Livro("Titulo", "Autor", Categoria.FICCAO);
        service.adicionarLivro(original);
        Livro atualizado = new Livro("TituloNovo", "AutorNovo", Categoria.CIENCIA);
        service.atualizarLivro("Titulo", atualizado);
        assertEquals(atualizado, service.buscarLivro("TituloNovo"));
        assertThrows(LivroNaoEncontradoException.class, () -> service.buscarLivro("Titulo"));
    }

    @Test
    void deveLancarExcecaoAoAtualizarLivroInexistente() {
        Livro livro = new Livro("Titulo", "Autor", Categoria.FICCAO);
        assertThrows(LivroNaoEncontradoException.class, () -> service.atualizarLivro("Inexistente", livro));
    }

    @Test
    void deveRemoverLivroComSucesso() {
        Livro livro = new Livro("Titulo", "Autor", Categoria.FICCAO);
        service.adicionarLivro(livro);
        service.removerLivro("Titulo");
        assertThrows(LivroNaoEncontradoException.class, () -> service.buscarLivro("Titulo"));
    }

    @Test
    void deveLancarExcecaoAoRemoverLivroInexistente() {
        assertThrows(LivroNaoEncontradoException.class, () -> service.removerLivro("Inexistente"));
    }

    @Test
    void deveListarTodosLivros() {
        Livro l1 = new Livro("T1", "A1", Categoria.FICCAO);
        Livro l2 = new Livro("T2", "A2", Categoria.CIENCIA);
        service.adicionarLivro(l1);
        service.adicionarLivro(l2);
        List<Livro> todos = service.listarLivros();
        assertEquals(2, todos.size());
        assertTrue(todos.contains(l1));
        assertTrue(todos.contains(l2));
    }
}
