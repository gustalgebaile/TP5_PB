package com.biblioteca.service;

import com.biblioteca.exception.LivroNaoEncontradoException;
import com.biblioteca.model.Categoria;
import com.biblioteca.model.Livro;
import com.biblioteca.model.LivroNulo;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BibliotecaServiceComprehensiveTest {

    @Mock
    private LivroRepository repository;

    @InjectMocks
    private BibliotecaService service;

    private Livro livroTeste;
    private ByteArrayOutputStream outputStreamCaptor;

    @BeforeEach
    void setUp() {
        livroTeste = new Livro("1984", "George Orwell", Categoria.FICCAO);
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(System.out);
    }

    @Nested
    @DisplayName("Testes de Construção do Service")
    class TestesConstucao {

        @Test
        @DisplayName("Deve lançar exceção quando repository é nulo")
        void deveLancarExcecaoQuandoRepositoryEhNulo() {
            assertThatThrownBy(() -> new BibliotecaService(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Deve criar service com repository válido")
        void deveCriarServiceComRepositoryValido() {
            LivroRepository repo = new LivroRepository();
            assertThatNoException().isThrownBy(() -> new BibliotecaService(repo));
        }
    }

    @Nested
    @DisplayName("Testes de Adição de Livros")
    class TestesAdicaoLivros {

        @Test
        @DisplayName("Deve adicionar livro válido")
        void deveAdicionarLivroValido() {
            service.adicionarLivro(livroTeste);

            verify(repository).adicionar(livroTeste);
        }

        @Test
        @DisplayName("Deve lançar exceção ao adicionar livro nulo")
        void deveLancarExcecaoAoAdicionarLivroNulo() {
            assertThatThrownBy(() -> service.adicionarLivro(null))
                    .isInstanceOf(NullPointerException.class);

            verify(repository, never()).adicionar(any());
        }

        @Test
        @DisplayName("Deve propagar exceção do repository")
        void devePropagarExcecaoDoRepository() {
            doThrow(new RuntimeException("Erro no repository"))
                    .when(repository).adicionar(any());

            assertThatThrownBy(() -> service.adicionarLivro(livroTeste))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Erro no repository");
        }

        @ParameterizedTest
        @EnumSource(Categoria.class)
        @DisplayName("Deve adicionar livros de todas as categorias")
        void deveAdicionarLivrosDeTodasAsCategorias(Categoria categoria) {
            Livro livro = new Livro("Livro " + categoria, "Autor", categoria);

            service.adicionarLivro(livro);

            verify(repository).adicionar(livro);
        }
    }

    @Nested
    @DisplayName("Testes de Busca de Livros")
    class TestesBuscaLivros {

        @Test
        @DisplayName("Deve retornar livro quando encontrado")
        void deveRetornarLivroQuandoEncontrado() {
            when(repository.buscarPorTitulo("1984")).thenReturn(livroTeste);

            Livro resultado = service.buscarLivro("1984");

            assertThat(resultado).isEqualTo(livroTeste);
            verify(repository).buscarPorTitulo("1984");
        }

        @Test
        @DisplayName("Deve lançar exceção quando livro não encontrado")
        void deveLancarExcecaoQuandoLivroNaoEncontrado() {
            when(repository.buscarPorTitulo("Inexistente")).thenReturn(LivroNulo.INSTANCE);

            assertThatThrownBy(() -> service.buscarLivro("Inexistente"))
                    .isInstanceOf(LivroNaoEncontradoException.class)
                    .hasMessage("Livro não encontrado: Inexistente");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "titulo qualquer", "1984", "Dom Casmurro"})
        @DisplayName("Deve buscar livros com diferentes títulos")
        void deveBuscarLivrosComDiferentesTitulos(String titulo) {
            Livro livroMock = new Livro(titulo.trim().isEmpty() ? "Título Padrão" : titulo,
                    "Autor", Categoria.FICCAO);
            when(repository.buscarPorTitulo(titulo)).thenReturn(livroMock);

            if (titulo.trim().isEmpty()) {
                when(repository.buscarPorTitulo(titulo)).thenReturn(LivroNulo.INSTANCE);
                assertThatThrownBy(() -> service.buscarLivro(titulo))
                        .isInstanceOf(LivroNaoEncontradoException.class);
            } else {
                Livro resultado = service.buscarLivro(titulo);
                assertThat(resultado).isEqualTo(livroMock);
            }
        }
    }

    @Nested
    @DisplayName("Testes de Atualização de Livros")
    class TestesAtualizacaoLivros {

        @Test
        @DisplayName("Deve atualizar livro existente")
        void deveAtualizarLivroExistente() {
            when(repository.buscarPorTitulo("1984")).thenReturn(livroTeste);

            Livro novoLivro = new Livro("1984 Updated", "George Orwell", Categoria.FICCAO);

            service.atualizarLivro("1984", novoLivro);

            verify(repository).buscarPorTitulo("1984");
            verify(repository).atualizar("1984", novoLivro);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar atualizar livro inexistente")
        void deveLancarExcecaoAoTentarAtualizarLivroInexistente() {
            when(repository.buscarPorTitulo("Inexistente")).thenReturn(LivroNulo.INSTANCE);

            Livro novoLivro = new Livro("Novo", "Autor", Categoria.CIENCIA);

            assertThatThrownBy(() -> service.atualizarLivro("Inexistente", novoLivro))
                    .isInstanceOf(LivroNaoEncontradoException.class)
                    .hasMessage("Livro não encontrado para atualizar: Inexistente");

            verify(repository, never()).atualizar(any(), any());
        }

        @Test
        @DisplayName("Deve lançar exceção ao atualizar com livro nulo")
        void deveLancarExcecaoAoAtualizarComLivroNulo() {
            assertThatThrownBy(() -> service.atualizarLivro("Qualquer", null))
                    .isInstanceOf(NullPointerException.class);

            verify(repository, never()).buscarPorTitulo(any());
            verify(repository, never()).atualizar(any(), any());
        }

        static Stream<Arguments> provideDadosParaAtualizacao() {
            return Stream.of(
                    Arguments.of("1984", "Novo Título", "George Orwell", Categoria.FICCAO),
                    Arguments.of("Dom Casmurro", "Dom Casmurro", "Machado de Assis", Categoria.ROMANCE),
                    Arguments.of("Clean Code", "Clean Code", "Robert Martin", Categoria.TECNOLOGIA)
            );
        }

        @ParameterizedTest
        @MethodSource("provideDadosParaAtualizacao")
        @DisplayName("Deve atualizar livros com diferentes dados")
        void deveAtualizarLivrosComDiferentesDados(String tituloOriginal,
                                                   String novoTitulo,
                                                   String novoAutor,
                                                   Categoria novaCategoria) {
            Livro livroOriginal = new Livro(tituloOriginal, "Autor Original", Categoria.HISTORIA);
            when(repository.buscarPorTitulo(tituloOriginal)).thenReturn(livroOriginal);

            Livro novoLivro = new Livro(novoTitulo, novoAutor, novaCategoria);

            service.atualizarLivro(tituloOriginal, novoLivro);

            verify(repository).atualizar(tituloOriginal, novoLivro);
        }
    }

    @Nested
    @DisplayName("Testes de Remoção de Livros")
    class TestesRemocaoLivros {

        @Test
        @DisplayName("Deve remover livro existente")
        void deveRemoverLivroExistente() {
            when(repository.buscarPorTitulo("1984")).thenReturn(livroTeste);

            service.removerLivro("1984");

            verify(repository).buscarPorTitulo("1984");
            verify(repository).remover("1984");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar remover livro inexistente")
        void deveLancarExcecaoAoTentarRemoverLivroInexistente() {
            when(repository.buscarPorTitulo("Inexistente")).thenReturn(LivroNulo.INSTANCE);

            assertThatThrownBy(() -> service.removerLivro("Inexistente"))
                    .isInstanceOf(LivroNaoEncontradoException.class)
                    .hasMessage("Livro não encontrado para remover: Inexistente");

            verify(repository, never()).remover(any());
        }

        @ParameterizedTest
        @ValueSource(strings = {"1984", "Dom Casmurro", "Clean Code", "Sapiens"})
        @DisplayName("Deve remover diferentes livros")
        void deveRemoverDiferentesLivros(String titulo) {
            Livro livro = new Livro(titulo, "Autor", Categoria.FICCAO);
            when(repository.buscarPorTitulo(titulo)).thenReturn(livro);

            service.removerLivro(titulo);

            verify(repository).remover(titulo);
        }
    }

    @Nested
    @DisplayName("Testes de Listagem de Livros")
    class TestesListagemLivros {

        @Test
        @DisplayName("Deve retornar lista vazia quando não há livros")
        void deveRetornarListaVaziaQuandoNaoHaLivros() {
            when(repository.listarTodos()).thenReturn(List.of());

            List<Livro> resultado = service.listarLivros();

            assertThat(resultado).isEmpty();
            verify(repository).listarTodos();
        }

        @Test
        @DisplayName("Deve retornar lista com livros")
        void deveRetornarListaComLivros() {
            Livro livro1 = new Livro("1984", "George Orwell", Categoria.FICCAO);
            Livro livro2 = new Livro("Clean Code", "Robert Martin", Categoria.TECNOLOGIA);
            List<Livro> livros = List.of(livro1, livro2);

            when(repository.listarTodos()).thenReturn(livros);

            List<Livro> resultado = service.listarLivros();

            assertThat(resultado).hasSize(2).containsExactly(livro1, livro2);
            verify(repository).listarTodos();
        }

        @Test
        @DisplayName("Deve imprimir livros no console")
        void deveImprimirLivrosNoConsole() {
            Livro livro = new Livro("1984", "George Orwell", Categoria.FICCAO);
            when(repository.listarTodos()).thenReturn(List.of(livro));

            service.listarLivros();

            assertThat(outputStreamCaptor.toString())
                    .contains("1984 - George Orwell (Ficção)");
        }

        @Test
        @DisplayName("Lista retornada deve ser imutável")
        void listaRetornadaDeveSerImutavel() {
            when(repository.listarTodos()).thenReturn(List.of(livroTeste));

            List<Livro> resultado = service.listarLivros();

            assertThatThrownBy(() -> resultado.add(new Livro("Novo", "Autor", Categoria.CIENCIA)))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("Testes de Limpeza da Base")
    class TestesLimpezaBase {

        @Test
        @DisplayName("Deve limpar a base de dados")
        void deveLimparABaseDeDados() {
            service.limparBase();

            verify(repository).limpar();
        }

        @Test
        @DisplayName("Deve propagar exceções da limpeza")
        void devePropagarExcecoesDaLimpeza() {
            doThrow(new RuntimeException("Erro na limpeza"))
                    .when(repository).limpar();

            assertThatThrownBy(() -> service.limparBase())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Erro na limpeza");
        }
    }

    @Nested
    @DisplayName("Testes de Cenários de Falha")
    class TestesCenariosFalha {

        @Test
        @DisplayName("Deve lidar com falha na busca durante atualização")
        void deveLidarComFalhaNaBuscaDuranteAtualizacao() {
            when(repository.buscarPorTitulo("1984"))
                    .thenThrow(new RuntimeException("Erro de conexão"));

            Livro novoLivro = new Livro("Novo", "Autor", Categoria.CIENCIA);

            assertThatThrownBy(() -> service.atualizarLivro("1984", novoLivro))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Erro de conexão");
        }

        @Test
        @DisplayName("Deve lidar com falha na busca durante remoção")
        void deveLidarComFalhaNaBuscaDuranteRemocao() {
            when(repository.buscarPorTitulo("1984"))
                    .thenThrow(new RuntimeException("Erro de conexão"));

            assertThatThrownBy(() -> service.removerLivro("1984"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Erro de conexão");
        }

        @Test
        @DisplayName("Deve lidar com timeout em operações")
        void deveLidarComTimeoutEmOperacoes() {
            when(repository.listarTodos())
                    .thenThrow(new RuntimeException("Timeout"));

            assertThatThrownBy(() -> service.listarLivros())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Timeout");
        }
    }

    @Nested
    @DisplayName("Testes de Integração com Mocks")
    class TestesIntegracaoComMocks {

        @Test
        @DisplayName("Deve executar fluxo completo de CRUD")
        void deveExecutarFluxoCompletoDeCRUD() {
            // Create
            service.adicionarLivro(livroTeste);
            verify(repository).adicionar(livroTeste);

            // Read
            when(repository.buscarPorTitulo("1984")).thenReturn(livroTeste);
            Livro encontrado = service.buscarLivro("1984");
            assertThat(encontrado).isEqualTo(livroTeste);

            // Update
            Livro livroAtualizado = new Livro("1984 Updated", "George Orwell", Categoria.FICCAO);
            service.atualizarLivro("1984", livroAtualizado);
            verify(repository).atualizar("1984", livroAtualizado);

            // Delete
            service.removerLivro("1984");
            verify(repository).remover("1984");
        }
    }

    @Nested
    @DisplayName("Testes de Validação de Mensagens")
    class TestesValidacaoMensagens {

        @Test
        @DisplayName("Mensagem de livro não encontrado para busca")
        void mensagemDeLivroNaoEncontradoParaBusca() {
            when(repository.buscarPorTitulo("Teste")).thenReturn(LivroNulo.INSTANCE);

            assertThatThrownBy(() -> service.buscarLivro("Teste"))
                    .hasMessage("Livro não encontrado: Teste");
        }

        @Test
        @DisplayName("Mensagem de livro não encontrado para atualização")
        void mensagemDeLivroNaoEncontradoParaAtualizacao() {
            when(repository.buscarPorTitulo("Teste")).thenReturn(LivroNulo.INSTANCE);

            assertThatThrownBy(() -> service.atualizarLivro("Teste", livroTeste))
                    .hasMessage("Livro não encontrado para atualizar: Teste");
        }

        @Test
        @DisplayName("Mensagem de livro não encontrado para remoção")
        void mensagemDeLivroNaoEncontradoParaRemocao() {
            when(repository.buscarPorTitulo("Teste")).thenReturn(LivroNulo.INSTANCE);

            assertThatThrownBy(() -> service.removerLivro("Teste"))
                    .hasMessage("Livro não encontrado para remover: Teste");
        }
    }
}