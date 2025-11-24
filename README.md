# TP4 - Refatoração e Modernização de Sistema de Biblioteca

Projeto de refatoração de um sistema de biblioteca com foco em modularização, qualidade de código, cobertura de testes e automação através de CI/CD com GitHub Actions.

---

## Manual de Execução

### 1. Como rodar a aplicação integrada

#### Pré-requisitos

- **Java 21** instalado ([Temurin/OpenJDK recomendado](https://adoptium.net/))
- **Git** instalado
- **Gradle** (opcional) - o projeto inclui Gradle Wrapper (`gradlew`)

#### Setup inicial

1. Clone o repositório:
   ```bash
   git clone <url-do-seu-repositorio>
   cd TP4
   ```

2. Certifique-se de que o `gradlew` tem permissão de execução (Linux/macOS):
   ```bash
   chmod +x ./gradlew
   ```

#### Executando a aplicação

**Modo desenvolvimento (com hot reload):**
```bash
./gradlew bootRun
```

**Build completo e execução do JAR:**
```bash
./gradlew clean build
java -jar build/libs/TP3_PB-2.0-SNAPSHOT.jar
```

**Apenas testes:**
```bash
./gradlew clean test
```

**Testes com relatório de cobertura Jacoco:**
```bash
./gradlew clean test jacocoTestReport
```

O relatório HTML será gerado em `build/reports/jacoco/test/html/index.html`. Abra no navegador para visualizar a cobertura de código.

---

### 2. Como executar e interpretar os workflows do GitHub Actions

#### Execução automática

O workflow é acionado automaticamente em:
- **Push** para a branch `main`
- **Pull Requests** direcionados à branch `main`
- **Execução manual** via aba **Actions** do GitHub

#### Arquivo do workflow

Local: `.github/workflows/gradle-ci.yml`

#### Etapas do workflow

1. **Checkout**: Clona o código do repositório
2. **Setup Java 21**: Configura o JDK 21 (Temurin)
3. **Cache Gradle**: Reutiliza dependências e cache para acelerar builds
4. **Permissões do Gradle Wrapper**: Garante que `gradlew` pode ser executado
5. **Build**: Compila o projeto com `./gradlew clean build`
6. **Testes e Cobertura**: Executa testes e gera relatório Jacoco com `./gradlew test jacocoTestReport`
7. **Upload de Artifacts**: Salva relatórios para download

#### Como interpretar os resultados

**Status do workflow:**
- **Green (sucesso)**: Build e testes passaram completamente
- **Red (falha)**: Falha na compilação ou testes falharam
- **Yellow (em execução)**: Workflow sendo processado

**Logs detalhados:**
1. Acesse a aba **Actions** do repositório no GitHub
2. Clique no workflow que deseja analisar
3. Clique no job "build" para ver detalhes
4. Procure por linhas de erro ou stacktrace

**Relatórios como artifacts:**
1. No workflow bem-sucedido, desça até a seção **Artifacts**
2. Baixe `jacoco-report` e `test-report` como arquivos ZIP
3. Extraia e abra `index.html` em cada pasta para visualizar:
   - **Jacoco**: Cobertura de testes por classe/método (meta: ≥85%)
   - **Test Report**: Resultados detalhados dos testes

#### Exemplo de interpretação

```
Workflow Status: Success
├── Build time: 45s
├── Tests passed: 124/124
└── Coverage: 86%
```

Se algum teste falhar:
```
Workflow Status: Failed
├── Step: Build with Gradle
├── Error: BUILD FAILED in 32s
└── Cause: com.biblioteca.service.BibliotecaServiceTest > testListarLivros FAILED
    └── AssertionError: Expected 5, but got 3
```

---

### 3. Principais mudanças feitas durante a refatoração

#### Atualização de versão do Java

- **Antes**: Java 8 (`java.sourceCompatibility = JavaVersion.VERSION_1_8`)
- **Depois**: Java 21 (`java.sourceCompatibility = JavaVersion.VERSION_21`)
- **Impacto**: Possibilita uso de resources, records, sealed classes e melhorias de performance

#### Migração de Maven para Gradle Kotlin DSL

- **Antes**: `pom.xml` (Maven)
- **Depois**: `build.gradle.kts` (Gradle com Kotlin DSL)
- **Benefícios**: Build mais rápido, melhor legibilidade, melhor suporte a CI/CD

#### Inclusão do Gradle Wrapper

- **Adição**: `gradlew`, `gradlew.bat`, `gradle/wrapper/`
- **Benefício**: Builds reprodutíveis e automação em ambientes diferentes (máquinas locais, CI)

#### Implementação de Records (Java 16+)

- **Implementado**: `LivroDto` como `record` em vez de classe POJO
- **Vantagem**: Menos boilerplate, imutabilidade por padrão, melhor legibilidade

#### Configuração de Testes e Cobertura

- **Framework**: JUnit 5 (Jupiter) com Spring Boot Test
- **Cobertura**: Jacoco 0.8.14
- **Meta**: ≥85% de cobertura de testes
- **Estrutura**: Testes organizados em `src/test/java` com package mirrors do código principal

#### Integração Contínua com GitHub Actions

- **Arquivo**: `.github/workflows/gradle-ci.yml`
- **Funcionalidades**:
  - Build automático em cada push/PR
  - Execução de testes completa
  - Geração automática de relatório Jacoco
  - Cache de dependências Gradle
  - Upload de artifacts para análise

#### Organização modular de código

- **Estrutura de packages**:
  ```
  com.biblioteca
  ├── model        (Entidades: Livro, Categoria)
  ├── dto          (Data Transfer Objects: LivroDto)
  ├── repository   (Acesso a dados)
  ├── service      (Lógica de negócio)
  ├── controller   (Endpoints REST - se aplicável)
  └── test         (Testes e configurações)
  ```
- **Benefício**: Separação clara de responsabilidades, facilita manutenção e testes

#### Deduplicação de dependências

- **Antes**: Múltiplas versões e duplicação de dependências no Maven
- **Depois**: Versioning centralizado via `libs.versions.toml` (Gradle)
- **Resultado**: Menos conflitos de dependência, builds mais previsíveis

#### Melhorias em tratamento de erros e validações

- **Adição**: Anotações `@Valid`, `@NotNull`, `@NotBlank` via Spring Validation
- **Benefício**: Validação automática em endpoints, mensagens de erro mais claras

#### Correção de compatibilidade JDK

- **Problema**: Erros de instrumentação Jacoco com Java 24
- **Solução**: Configuração explícita para Java 21, exclusão de classes JDK do relatório Jacoco
- **Resultado**: Testes e cobertura funcionam sem erros

#### Documentação e logs

- **Adição**: README.md completo com manual de execução
- **Benefício**: Novos desenvolvedores conseguem rodar o projeto rapidamente

---

## Estrutura do Projeto

```
TP4/
├── .github/
│   └── workflows/
│       └── gradle-ci.yml          # Workflow CI/CD do GitHub Actions
├── gradle/
│   └── wrapper/                   # Gradle Wrapper (versão fixa)
├── src/
│   ├── main/java/com/biblioteca/
│   │   ├── model/
│   │   ├── dto/
│   │   ├── repository/
│   │   ├── service/
│   │   └── ...
│   └── test/java/com/biblioteca/
│       ├── model/
│       ├── dto/
│       ├── repository/
│       ├── service/
│       ├── selenium/
│       └── test/config/
├── build.gradle.kts               # Configuração Gradle (Kotlin DSL)
├── settings.gradle.kts            # Configuração de módulos
├── gradle.properties              # Propriedades do Gradle
├── gradlew                        # Gradle Wrapper (Unix/Linux/Mac)
├── gradlew.bat                    # Gradle Wrapper (Windows)
├── pom.xml                        # Configuração Maven (legada)
├── README.md                      # Este arquivo
└── .gitignore                     # Arquivos ignorados pelo Git
```

Github: https://github.com/gustalgebaile/TP4_PB

---
