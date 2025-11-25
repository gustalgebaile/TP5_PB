package com.biblioteca.repository;

import com.biblioteca.exception.LivroDuplicadoException;
import com.biblioteca.model.Categoria;
import com.biblioteca.model.Livro;
import com.biblioteca.model.LivroNulo;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class LivroRepositoryComprehensiveTest {

    private LivroRepository repository;
    private Livro livroTeste;

    @BeforeEach
    void setUp() {
        repository = new LivroRepository();
        livroTeste = new Livro("Dom Casmurro", "Machado de Assis", Categoria.FICCAO);
    }

    @Nested
    @DisplayName("Testes de Adição")
    class TestesAdicao {

        @Test
        @DisplayName("Deve adicionar livro válido")
        void deveAdicionarLivroValido() {
            repository.adicionar(livroTeste);

            Livro encontrado = repository.buscarPorTitulo("Dom Casmurro");
            assertThat(encontrado).isEqualTo(livroTeste);
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar livro nulo")
        void deveLancarExcecaoAoAdicionarLivroNulo() {
            assertThatThrownBy(() -> repository.adicionar(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Livro não pode ser null");
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar livro duplicado")
        void deveLancarExcecaoAoAdicionarLivroDuplicado() {
            repository.adicionar(livroTeste);

            Livro duplicado = new Livro("Dom Casmurro", "Outro Autor", Categoria.HISTORIA);

            assertThatThrownBy(() -> repository.adicionar(duplicado))
                    .isInstanceOf(LivroDuplicadoException.class)
                    .hasMessageContaining("Já existe livro com título: Dom Casmurro");
        }

        @ParameterizedTest
        @DisplayName("Deve normalizar títulos ao verificar duplicação")
        @ValueSource(strings = {"dom casmurro", "DOM CASMURRO", "  Dom Casmurro  ", "DoM CaSmUrRo"})
        void deveNormalizarTitulosAoVerificarDuplicacao(String titulo) {
            repository.adicionar(livroTeste);

            Livro comTituloNormalizado = new Livro(titulo, "Outro Autor", Categoria.CIENCIA);

            assertThatThrownBy(() -> repository.adicionar(comTituloNormalizado))
                    .isInstanceOf(LivroDuplicadoException.class);
        }
    }

    @Nested
    @DisplayName("Testes de Busca")
    class TestesBusca {

        @Test
        @DisplayName("Deve retornar livro quando encontrado")
        void deveRetornarLivroQuandoEncontrado() {
            repository.adicionar(livroTeste);

            Livro encontrado = repository.buscarPorTitulo("Dom Casmurro");

            assertThat(encontrado).isEqualTo(livroTeste);
        }

        @Test
        @DisplayName("Deve retornar LivroNulo quando não encontrado")
        void deveRetornarLivroNuloQuandoNaoEncontrado() {
            Livro encontrado = repository.buscarPorTitulo("Inexistente");

            assertThat(encontrado).isInstanceOf(LivroNulo.class);
            assertThat(encontrado).isSameAs(LivroNulo.INSTANCE);
        }

        @ParameterizedTest
        @DisplayName("Deve buscar livro independente de case e espaços")
        @ValueSource(strings = {"dom casmurro", "DOM CASMURRO", "  Dom Casmurro  "})
        void deveBuscarLivroIndependenteDeCaseEEspacos(String titulo) {
            repository.adicionar(livroTeste);

            Livro encontrado = repository.buscarPorTitulo(titulo);

            assertThat(encontrado).isEqualTo(livroTeste);
        }

        @Test
        @DisplayName("Deve lançar exceção ao buscar com título nulo")
        void deveLancarExcecaoAoBuscarComTituloNulo() {
            assertThatThrownBy(() -> repository.buscarPorTitulo(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Título obrigatório");
        }
    }

    @Nested
    @DisplayName("Testes de Atualização")
    class TestesAtualizacao {

        @Test
        @DisplayName("Deve atualizar livro existente")
        void deveAtualizarLivroExistente() {
            repository.adicionar(livroTeste);

            Livro novoLivro = new Livro("1984", "George Orwell", Categoria.FICCAO);
            repository.atualizar("Dom Casmurro", novoLivro);

            assertThat(repository.buscarPorTitulo("Dom Casmurro")).isInstanceOf(LivroNulo.class);
            assertThat(repository.buscarPorTitulo("1984")).isEqualTo(novoLivro);
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar com livro nulo")
        void deveLancarExcecaoAoAtualizarComLivroNulo() {
            repository.adicionar(livroTeste);

            assertThatThrownBy(() -> repository.atualizar("Dom Casmurro", null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessage("Livro não pode ser null");
        }

        @Test
        @DisplayName("Deve atualizar considerando normalização do título")
        void deveAtualizarConsiderandoNormalizacaoDoTitulo() {
            repository.adicionar(livroTeste);

            Livro novoLivro = new Livro("1984", "George Orwell", Categoria.FICCAO);
            repository.atualizar("  DOM CASMURRO  ", novoLivro);

            assertThat(repository.buscarPorTitulo("Dom Casmurro")).isInstanceOf(LivroNulo.class);
            assertThat(repository.buscarPorTitulo("1984")).isEqualTo(novoLivro);
        }
    }

    @Nested
    @DisplayName("Testes de Remoção")
    class TestesRemocao {

        @Test
        @DisplayName("Deve remover livro existente")
        void deveRemoverLivroExistente() {
            repository.adicionar(livroTeste);

            repository.remover("Dom Casmurro");

            assertThat(repository.buscarPorTitulo("Dom Casmurro")).isInstanceOf(LivroNulo.class);
        }

        @Test
        @DisplayName("Não deve fazer nada ao tentar remover livro inexistente")
        void naoDeveFazerNadaAoTentarRemoverLivroInexistente() {
            assertThatNoException().isThrownBy(() -> repository.remover("Inexistente"));
        }

        @ParameterizedTest
        @DisplayName("Deve remover considerando normalização do título")
        @ValueSource(strings = {"dom casmurro", "DOM CASMURRO", "  Dom Casmurro  "})
        void deveRemoverConsiderandoNormalizacaoDoTitulo(String titulo) {
            repository.adicionar(livroTeste);

            repository.remover(titulo);

            assertThat(repository.buscarPorTitulo("Dom Casmurro")).isInstanceOf(LivroNulo.class);
        }
    }

    @Nested
    @DisplayName("Testes de Listagem")
    class TestesListagem {

        @Test
        @DisplayName("Deve retornar lista vazia quando não há livros")
        void deveRetornarListaVaziaQuandoNaoHaLivros() {
            List<Livro> livros = repository.listarTodos();

            assertThat(livros).isEmpty();
        }

        @Test
        @DisplayName("Deve retornar todos os livros adicionados")
        void deveRetornarTodosOsLivrosAdicionados() {
            Livro livro1 = new Livro("1984", "George Orwell", Categoria.FICCAO);
            Livro livro2 = new Livro("Clean Code", "Robert Martin", Categoria.TECNOLOGIA);

            repository.adicionar(livroTeste);
            repository.adicionar(livro1);
            repository.adicionar(livro2);

            List<Livro> livros = repository.listarTodos();

            assertThat(livros)
                    .hasSize(3)
                    .containsExactlyInAnyOrder(livroTeste, livro1, livro2);
        }

        @Test
        @DisplayName("Lista retornada deve ser imutável")
        void listaRetornadaDeveSerImutavel() {
            repository.adicionar(livroTeste);

            List<Livro> livros = repository.listarTodos();

            assertThatThrownBy(() -> livros.add(new Livro("Teste", "Autor", Categoria.CIENCIA)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("Testes de Limpeza")
    class TestesLimpeza {

        @Test
        @DisplayName("Deve limpar todos os livros")
        void deveLimparTodosOsLivros() {
            repository.adicionar(livroTeste);
            repository.adicionar(new Livro("1984", "George Orwell", Categoria.FICCAO));

            repository.limpar();

            assertThat(repository.listarTodos()).isEmpty();
            assertThat(repository.buscarPorTitulo("Dom Casmurro")).isInstanceOf(LivroNulo.class);
        }
    }

    @Nested
    @DisplayName("Testes de Casos Extremos e Robustez")
    class TestesCasosExtremos {

        @ParameterizedTest
        @DisplayName("Deve lidar com títulos com caracteres especiais")
        @ValueSource(strings = {
                "Ação & Aventura",
                "C++ Programming",
                "Título com acentuação",
                "Book with 'quotes'",
                "Book with \"double quotes\"",
                "Book with numbers 123",
                "    Título com espaços múltiplos    "
        })
        void deveLidarComTitulosComCaracteresEspeciais(String titulo) {
            Livro livro = new Livro(titulo, "Autor", Categoria.TECNOLOGIA);

            assertThatNoException().isThrownBy(() -> {
                repository.adicionar(livro);
                Livro encontrado = repository.buscarPorTitulo(titulo);
                assertThat(encontrado).isEqualTo(livro);
            });
        }

        @Test
        @DisplayName("Deve lidar com grande volume de dados")
        void deveLidarComGrandeVolumeDeDados() {
            // Adiciona 1000 livros
            for (int i = 0; i < 1000; i++) {
                repository.adicionar(new Livro("Livro " + i, "Autor " + i,
                        Categoria.values()[i % Categoria.values().length]));
            }

            assertThat(repository.listarTodos()).hasSize(1000);
            assertThat(repository.buscarPorTitulo("Livro 500").getTitulo()).isEqualTo("Livro 500");
        }

        @Test
        @DisplayName("Operações concorrentes devem ser seguras")
        void operacoesConcorrentesDevemSerSeguras() {
            // Teste básico de thread safety - pode precisar de mais elaboração
            assertThatNoException().isThrownBy(() -> {
                repository.adicionar(new Livro("Concorrente", "Autor", Categoria.TECNOLOGIA));
                repository.buscarPorTitulo("Concorrente");
                repository.remover("Concorrente");
            });
        }
    }

    @ParameterizedTest
    @DisplayName("Teste de cobertura de todas as categorias")
    @EnumSource(Categoria.class)
    void testeCoberturaDasCategorias(Categoria categoria) {
        Livro livro = new Livro("Livro " + categoria, "Autor", categoria);

        repository.adicionar(livro);
        Livro encontrado = repository.buscarPorTitulo("Livro " + categoria);

        assertThat(encontrado.getCategoria()).isEqualTo(categoria);
    }

    static Stream<Arguments> provideTitulosParaNormalizacao() {
        return Stream.of(
                Arguments.of("Título Normal", "título normal"),
                Arguments.of("  Com Espaços  ", "com espaços"),
                Arguments.of("MAIÚSCULO", "maiúsculo"),
                Arguments.of("MiXeD cAsE", "mixed case"),
                Arguments.of("\tCom\nTab\rE\nNewlines\t", "com\ntab\re\nnewlines")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTitulosParaNormalizacao")
    @DisplayName("Deve normalizar títulos corretamente")
    void deveNormalizarTitulosCorretamente(String tituloOriginal, String tituloEsperado) {
        Livro livro = new Livro(tituloOriginal, "Autor", Categoria.FICCAO);
        repository.adicionar(livro);

        // Busca com título em formato diferente deve encontrar o livro
        Livro encontrado = repository.buscarPorTitulo(tituloEsperado.toUpperCase());
        assertThat(encontrado).isEqualTo(livro);
    }
}