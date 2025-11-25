# TP5 - Modernização de Sistema de Biblioteca

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
   cd TP5
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

### 2. Como executar os workflows do GitHub Actions

#### Execução automática

O workflow é acionado automaticamente em:
- **Push** para a branch `main`
- **Pull Requests** direcionados à branch `main`
- **Execução manual** via aba **Actions** do GitHub

#### Arquivo do workflow

Local: `.github/workflows/`