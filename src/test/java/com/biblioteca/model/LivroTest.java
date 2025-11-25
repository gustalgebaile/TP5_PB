package com.biblioteca.model;

import com.biblioteca.model.Categoria;
import com.biblioteca.model.Livro;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LivroTest {

    @Test
    void criaLivroValido() {
        Livro livro = new Livro("1984", "George Orwell", Categoria.FICCAO);
        assertEquals("1984", livro.getTitulo());
        assertEquals("George Orwell", livro.getAutor());
        assertEquals(Categoria.FICCAO, livro.getCategoria());
        assertEquals("Ficção", livro.etiquetaCategoria());
        assertTrue(livro.toString().contains("1984"));
        assertTrue(livro.toString().contains("George Orwell"));
        assertTrue(livro.toString().contains("Ficção"));}

    @Test
    void livroEqualsHashCode() {
        Livro l1 = new Livro("Titulo", "Autor", Categoria.CIENCIA);
        Livro l2 = new Livro("Titulo", "Autor", Categoria.CIENCIA);
        Livro l3 = new Livro("Outro", "Autor", Categoria.CIENCIA);

        assertEquals(l1, l2);
        assertNotEquals(l1, l3);
        assertEquals(l1.hashCode(), l2.hashCode());}

    @Test
    void equalsComNullOutroTipo() {
        Livro livro = new Livro("A", "B", Categoria.HISTORIA);
        assertNotEquals(null, livro);
        assertNotEquals("string qualquer", livro);}

    @Test
    void obrigatorioTituloAutorCategoria() {
        assertThrows(NullPointerException.class,
                () -> new Livro(null, "Autor", Categoria.FICCAO));
        assertThrows(NullPointerException.class,
                () -> new Livro("Titulo", null, Categoria.FICCAO));
        assertThrows(NullPointerException.class,
                () -> new Livro("Titulo", "Autor", null));}

    @Test
    void tituloOuAutorEmBrancoLancaExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("", "Autor", Categoria.FICCAO));
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("Titulo", "   ", Categoria.FICCAO));}
}
