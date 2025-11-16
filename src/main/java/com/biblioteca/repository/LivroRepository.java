package com.biblioteca.repository;

import com.biblioteca.exception.LivroDuplicadoException;
import com.biblioteca.exception.LivroNaoEncontradoException;
import com.biblioteca.model.Livro;
import com.biblioteca.model.LivroNulo;

import java.util.*;

public class LivroRepository {
    private static final String MSG_DUPLICADO = "Já existe livro com título: ";
    private final Map<String, Livro> porTitulo = new HashMap<>();

    public void adicionar(Livro livro) {
        Objects.requireNonNull(livro, "Livro não pode ser null");
        String key = normalize(livro.getTitulo());
        if (porTitulo.containsKey(key)) {
            throw new LivroDuplicadoException(MSG_DUPLICADO + livro.getTitulo());
        }
        porTitulo.put(key, livro);
    }
    public void atualizar(String tituloAntigo, Livro novoLivro) {
        Objects.requireNonNull(novoLivro, "Livro não pode ser null");
        String keyAntigo = normalize(tituloAntigo);
        if (!porTitulo.containsKey(keyAntigo)) {
            throw new LivroNaoEncontradoException("Livro não encontrado: " + tituloAntigo);
        }
        porTitulo.remove(keyAntigo);
        porTitulo.put(normalize(novoLivro.getTitulo()), novoLivro);
    }
    public void remover(String titulo) {
        porTitulo.remove(normalize(titulo));
    }
    public Livro buscarPorTitulo(String titulo) {
        return porTitulo.getOrDefault(normalize(titulo), LivroNulo.INSTANCE);
    }
    public List<Livro> listarTodos() {
        return List.copyOf(porTitulo.values());
    }
    private String normalize(String titulo) {
        return Objects.requireNonNull(titulo, "Título obrigatório").trim().toLowerCase(Locale.ROOT);
    }
    public void limpar() {
        porTitulo.clear();
    }
}
