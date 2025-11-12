package com.biblioteca.service;

import com.biblioteca.exception.LivroNaoEncontradoException;
import com.biblioteca.model.Livro;
import com.biblioteca.model.LivroNulo;
import com.biblioteca.repository.LivroRepository;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BibliotecaService {
    private static final String MSG_NAO_ENCONTRADO = "Livro não encontrado: ";
    private static final String MSG_NAO_ENCONTRADO_ATUALIZAR = "Livro não encontrado para atualizar: ";
    private static final String MSG_NAO_ENCONTRADO_REMOVER = "Livro não encontrado para remover: ";

    private final LivroRepository repository;

    public BibliotecaService(LivroRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }
    public void adicionarLivro(Livro livro) {
        repository.adicionar(Objects.requireNonNull(livro));
    }
    public void atualizarLivro(String tituloAntigo, Livro novoLivro) {
        var existente = repository.buscarPorTitulo(tituloAntigo);
        if (existente instanceof LivroNulo) {
            throw new LivroNaoEncontradoException(MSG_NAO_ENCONTRADO_ATUALIZAR + tituloAntigo);
        }
        repository.atualizar(tituloAntigo, Objects.requireNonNull(novoLivro));
    }
    public void removerLivro(String titulo) {
        var existente = repository.buscarPorTitulo(titulo);
        if (existente instanceof LivroNulo) {
            throw new LivroNaoEncontradoException(MSG_NAO_ENCONTRADO_REMOVER + titulo);
        }
        repository.remover(titulo);
    }
    public Livro buscarLivro(String titulo) {
        var livro = repository.buscarPorTitulo(titulo);
        if (livro instanceof LivroNulo) {
            throw new LivroNaoEncontradoException(MSG_NAO_ENCONTRADO + titulo);
        }
        return livro;
    }
    public List<Livro> listarLivros() {
        var livros = repository.listarTodos();
        livros.forEach(System.out::println);
        return Collections.unmodifiableList(livros);
    }
    public void limparBase() {
        repository.limpar();
    }
}