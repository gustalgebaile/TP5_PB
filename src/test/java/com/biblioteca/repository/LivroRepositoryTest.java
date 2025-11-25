package com.biblioteca.repository;

import com.biblioteca.exception.LivroDuplicadoException;
import com.biblioteca.model.Categoria;
import com.biblioteca.model.Livro;
import com.biblioteca.model.LivroNulo;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LivroRepositoryTest {

    private LivroRepository repository;

    @BeforeEach
    void setUp() {
        repository = new LivroRepository();
    }

    @Test
    void deveAdicionarLivroComSucesso() {
        Livro livro = new Livro("Titulo", "Autor", Categoria.FICCAO);
        repository.adicionar(livro);
        Livro salvo = repository.buscarPorTitulo("Titulo");
        assertEquals(livro, salvo);
    }

    @Test
    void deveLancarExcecaoAoAdicionarDuplicado() {
        Livro livro = new Livro("Titulo", "Autor", Categoria.FICCAO);
        repository.adicionar(livro);
        Livro duplicado = new Livro("Titulo", "Outro Autor", Categoria.CIENCIA);
        assertThrows(LivroDuplicadoException.class, () -> repository.adicionar(duplicado));
    }

    @Test
    void deveAtualizarLivro() {
        Livro livro = new Livro("Titulo", "Autor", Categoria.FICCAO);
        repository.adicionar(livro);
        Livro atualizado = new Livro("TituloNovo", "AutorNovo", Categoria.CIENCIA);
        repository.atualizar("Titulo", atualizado);
        assertEquals(atualizado, repository.buscarPorTitulo("TituloNovo"));
        assertEquals(LivroNulo.INSTANCE, repository.buscarPorTitulo("Titulo"));
    }

    @Test
    void deveRemoverLivro() {
        Livro livro = new Livro("Titulo", "Autor", Categoria.FICCAO);
        repository.adicionar(livro);
        repository.remover("Titulo");
        assertEquals(LivroNulo.INSTANCE, repository.buscarPorTitulo("Titulo"));
    }

    @Test
    void deveListarTodosLivros() {
        Livro l1 = new Livro("T1", "A1", Categoria.FICCAO);
        Livro l2 = new Livro("T2", "A2", Categoria.CIENCIA);
        repository.adicionar(l1);
        repository.adicionar(l2);
        List<Livro> livros = repository.listarTodos();
        assertTrue(livros.contains(l1));
        assertTrue(livros.contains(l2));
        assertEquals(2, livros.size());
    }
}

