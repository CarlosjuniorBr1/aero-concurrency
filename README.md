# Aeroporto Concorrente

Esta aplicação é um simulador de concorrência em Java que representa o processo de registro de decolagens em um aeroporto, no qual vários voos disputam simultaneamente conexões limitadas de banco de dados. O projeto adapta o problema clássico do Jantar dos Filósofos para demonstrar, na prática, conceitos como processos, threads, compartilhamento de recursos, exclusão mútua, deadlock, condição de corrida, operações atômicas, monitoramento de desempenho e containerização com Docker.

Projeto acadêmico desenvolvido em **Java 17** para demonstrar conceitos de concorrência, sincronização, deadlock, condição de corrida, operações atômicas, monitoramento de recursos e containerização com Docker.

O sistema utiliza o contexto de um aeroporto para representar uma adaptação do problema clássico do **Jantar dos Filósofos**:

- cada **voo** representa um filósofo;
- cada **conexão com o banco de dados** representa um garfo;
- cada voo precisa adquirir duas conexões para registrar sua decolagem;
- diferentes estratégias de execução demonstram problemas e soluções relacionados à concorrência.

---

## Objetivos do projeto

O projeto foi criado para demonstrar, de forma prática:

- criação e execução de múltiplas threads;
- uso de `ExecutorService`;
- compartilhamento de recursos entre threads;
- ocorrência de deadlock;
- prevenção de deadlock por ordenação de locks;
- ocorrência de condição de corrida;
- uso de `AtomicInteger` para atualizações seguras;
- medição de tempo, vazão, CPU e memória;
- exportação dos resultados para CSV;
- execução da aplicação em container Docker;
- limitação de CPU e memória por meio do Docker Compose.

---

## Tecnologias utilizadas

- Java 17
- Maven
- Java Concurrency API
- `ExecutorService`
- `ReentrantLock`
- `AtomicInteger`
- Docker
- Docker Compose
- CSV para exportação dos benchmarks

---

## Modelagem do problema

A aplicação utiliza os seguintes elementos:

| Elemento do projeto | Equivalente no Jantar dos Filósofos |
|---|---|
| `Flight` | Filósofo |
| `DatabaseConnection` | Garfo |
| `DatabasePool` | Conjunto de garfos |
| Registro de decolagem | Refeição |
| Duas conexões necessárias | Dois garfos necessários |

Cada voo precisa obter duas conexões do pool antes de realizar o registro da decolagem. Quando as conexões são adquiridas de forma inadequada, pode ocorrer deadlock.

---

## Estratégias implementadas

### 1. `UnsafeFlightStrategy`

A estratégia insegura tenta adquirir primeiro uma conexão e depois a outra, sem estabelecer uma ordem global.

Esse comportamento pode fazer com que diferentes threads mantenham uma conexão bloqueada enquanto aguardam outra, produzindo um deadlock.

Características:

- permite deadlock;
- utiliza contador compartilhado não atômico;
- possui limite de espera de 30 segundos;
- quando o tempo é excedido, o experimento é marcado com `Deadlock = true`;
- utiliza locks interrompíveis para permitir o encerramento das threads após a detecção.

---

### 2. `SafeFlightStrategy`

A estratégia segura ordena as conexões pelo identificador antes de adquiri-las.

Todas as threads passam a solicitar os locks na mesma ordem, eliminando a espera circular e, consequentemente, o deadlock.

Entretanto, essa estratégia ainda utiliza incremento comum com `++` em um contador compartilhado.

Características:

- elimina deadlock;
- mantém uma condição de corrida no contador;
- pode finalizar com um valor inferior à quantidade real de operações;
- demonstra que ausência de deadlock não significa ausência de problemas de concorrência.

---

### 3. `SafeAtomicFlightStrategy`

A estratégia atômica utiliza a mesma ordenação de conexões da estratégia segura e substitui o incremento comum por um `AtomicInteger`.

Características:

- elimina deadlock;
- elimina a condição de corrida no contador;
- utiliza `incrementAndGet()`;
- deve finalizar com o contador igual ao número total de operações.

---

## Estrutura do projeto

```text
aeroporto/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── org/
│   │           └── air/
│   │               ├── Main.java
│   │               ├── concurrency/
│   │               │   ├── FlightExecutionStrategy.java
│   │               │   ├── FlightTask.java
│   │               │   ├── SafeAtomicFlightStrategy.java
│   │               │   ├── SafeFlightStrategy.java
│   │               │   └── UnsafeFlightStrategy.java
│   │               ├── database/
│   │               │   ├── DatabaseConnection.java
│   │               │   └── DatabasePool.java
│   │               ├── experiment/
│   │               │   └── ExperimentRunner.java
│   │               ├── export/
│   │               │   └── CsvExporter.java
│   │               ├── model/
│   │               │   └── Flight.java
│   │               ├── monitor/
│   │               │   ├── ExperimentResult.java
│   │               │   └── SystemMonitor.java
│   │               └── util/
│   │                   ├── Config.java
│   │                   ├── SharedStatistics.java
│   │                   ├── SharedStatisticsSafe.java
│   │                   └── Timer.java
│   └── test/
├── results/
│   └── benchmark.csv
├── target/
├── .dockerignore
├── .gitignore
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## Configurações dos experimentos

As principais configurações estão centralizadas na classe:

```text
src/main/java/org/air/util/Config.java
```

Exemplo:

```java
public class Config {

    public static final boolean DEBUG = false;
    public static final int DATABASE_CONNECTIONS = 5;
    public static final long LOCK_DELAY_MS = 0;
    public static final long WORK_DELAY_MS = 0;
}
```

### Descrição

| Configuração | Função |
|---|---|
| `DEBUG` | Ativa ou desativa mensagens detalhadas das threads |
| `DATABASE_CONNECTIONS` | Quantidade de conexões compartilhadas |
| `LOCK_DELAY_MS` | Atraso entre a aquisição do primeiro e do segundo lock |
| `WORK_DELAY_MS` | Tempo simulado para registrar uma decolagem |

Ativar atrasos pode tornar o comportamento concorrente mais visível, mas também aumenta o tempo total dos experimentos.

---

## Pré-requisitos

Para executar localmente, é necessário ter instalado:

- Java 17 ou superior;
- Maven 3.8 ou superior.

Para executar em container, também é necessário:

- Docker;
- Docker Compose v2.

### Conferir as versões

```bash
java -version
mvn -version
docker --version
docker compose version
```

---

# Como executar o projeto

## 1. Clonar o repositório

```bash
git clone https://github.com/CarlosjuniorBr1/aero-concurrency
cd aero-concurrency/aeroporto
```

Substitua `<URL_DO_REPOSITORIO>` pelo endereço real do projeto.

É importante executar os comandos dentro da pasta que contém o arquivo `pom.xml`.

---

## 2. Compilar com Maven

Na pasta `aeroporto`, execute:

```bash
mvn clean package
```

Quando a compilação terminar corretamente, será exibido:

```text
BUILD SUCCESS
```

O arquivo JAR será criado em:

```text
target/aeroporto-1.0-SNAPSHOT.jar
```

---

## 3. Executar localmente

```bash
java -jar target/aeroporto-1.0-SNAPSHOT.jar
```

A aplicação executará as três estratégias para diferentes quantidades de operações.

Por padrão, os experimentos utilizam:

```java
int[] operationsList = {
    10_000,
    100_000,
    1_000_000
};
```

Como a estratégia insegura aguarda até 30 segundos para detectar deadlock, a execução completa pode levar aproximadamente 90 segundos ou mais.

Para um teste rápido, altere temporariamente o vetor no `Main.java`:

```java
int[] operationsList = {
    10_000
};
```

---

## 4. Executar com Docker

Primeiro, gere novamente o JAR:

```bash
mvn clean package
```

Depois, construa e execute o container:

```bash
docker compose up --build
```

O Compose exibirá os logs da aplicação no terminal.

Ao final da execução, deve aparecer algo semelhante a:

```text
aeroporto exited with code 0
```

O código de saída `0` indica que a aplicação terminou normalmente.

---

## 5. Executar diretamente com Docker

Também é possível construir a imagem manualmente:

```bash
docker build -t aeroporto .
```

Depois, execute:

```bash
docker run --rm aeroporto
```

A opção `--rm` remove automaticamente o container depois que o programa termina.

---

## 6. Encerrar o Docker Compose

Caso o processo ainda esteja conectado ao terminal, pressione:

```text
Ctrl + C
```

Depois remova o container e a rede criada pelo Compose:

```bash
docker compose down
```

---

## Dockerfile

O projeto utiliza um Dockerfile semelhante a:

```dockerfile
FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
```

Como o Dockerfile copia o JAR da pasta `target`, é necessário executar antes:

```bash
mvn clean package
```

---

## Docker Compose

Exemplo de configuração:

```yaml
services:
  aeroporto:
    build: .
    container_name: aeroporto
    cpus: 2
    mem_limit: 512m
    restart: "no"
    volumes:
      - ./results:/app/results
```

Essa configuração:

- limita o container a 2 CPUs;
- limita a memória a 512 MB;
- impede reinicializações automáticas;
- compartilha a pasta `results` entre o container e a máquina local.

O atributo `version` não é necessário nas versões atuais do Docker Compose.

---

## Exportação dos resultados

Ao finalizar os experimentos, a aplicação gera o arquivo:

```text
results/benchmark.csv
```

Exemplo de conteúdo:

```csv
Strategy,Operations,ExecutionTime,OpsPerSecond,CPU,Memory,Deadlock,UnsafeCounter,SafeCounter
UnsafeFlightStrategy,10000,30.013,333.19,0.00,28.44,true,130,0
SafeFlightStrategy,10000,0.020,494916.59,48.15,32.39,false,9977,0
SafeAtomicFlightStrategy,10000,0.011,930558.73,42.86,35.70,false,0,10000
```

### Descrição das colunas

| Coluna | Descrição |
|---|---|
| `Strategy` | Estratégia utilizada no experimento |
| `Operations` | Quantidade de tarefas submetidas |
| `ExecutionTime` | Tempo total de execução em segundos |
| `OpsPerSecond` | Quantidade teórica de operações por segundo |
| `CPU` | Uso aproximado de CPU no momento da coleta |
| `Memory` | Memória utilizada pela JVM em megabytes |
| `Deadlock` | Indica se houve bloqueio durante a execução |
| `UnsafeCounter` | Valor do contador comum compartilhado |
| `SafeCounter` | Valor do contador atômico |

---

## Interpretação dos resultados

### Estratégia insegura

A estratégia insegura normalmente apresenta:

```text
Deadlock   : true
```

Seu tempo tende a ficar próximo de 30 segundos porque esse é o limite configurado para detectar que o `ExecutorService` não conseguiu encerrar.

O valor de `OpsPerSecond` dessa estratégia deve ser interpretado com cuidado. Como ocorreu deadlock, nem todas as operações foram realmente concluídas. A fórmula utiliza o número solicitado de operações dividido pelo tempo total, portanto não representa a vazão real de tarefas finalizadas nesse cenário.

---

### Estratégia segura

A estratégia segura deve apresentar:

```text
Deadlock   : false
```

Entretanto, o contador comum pode terminar com valor inferior ao número de operações.

Exemplo:

```text
Operações  : 1000000
Unsafe Cnt : 999950
```

Isso acontece porque o operador `++` não é atômico. Duas ou mais threads podem ler e atualizar o mesmo valor simultaneamente, causando perda de incrementos.

---

### Estratégia atômica

A estratégia atômica deve apresentar:

```text
Deadlock   : false
Safe Cnt   : número total de operações
```

Exemplo:

```text
Operações : 1000000
Safe Cnt  : 1000000
```

Esse comportamento confirma que o `AtomicInteger` preserva a consistência do contador compartilhado.

---

## Observações sobre as métricas

### CPU

A medição de CPU representa uma amostra obtida durante o experimento. Em testes muito rápidos, o valor pode aparecer como `0.00%` porque a execução termina antes de o sistema operacional atualizar a métrica de utilização.

### Memória

A coluna de memória representa o consumo total aproximado da JVM no momento da coleta.

Os valores podem variar devido a:

- alocação de objetos;
- fila interna do `ExecutorService`;
- comportamento do Garbage Collector;
- aquecimento da JVM;
- ordem de execução dos experimentos.

Por isso, a memória deve ser considerada uma métrica complementar, e não uma medida absoluta de cada estratégia.

### Tempo e aquecimento da JVM

A JVM utiliza compilação JIT. As primeiras execuções podem ser mais lentas do que as seguintes, porque partes do código ainda estão sendo compiladas e otimizadas durante a execução.

Para resultados científicos mais rigorosos, seria recomendado:

- executar várias repetições;
- descartar execuções de aquecimento;
- calcular média, mediana e desvio-padrão;
- utilizar uma ferramenta especializada, como JMH.

---

## Resultado esperado

Um resultado típico apresenta o seguinte comportamento:

| Estratégia | Deadlock | Contador consistente | Comportamento esperado |
|---|---:|---:|---|
| `UnsafeFlightStrategy` | Sim | Não | Threads ficam bloqueadas aguardando conexões |
| `SafeFlightStrategy` | Não | Não | Locks ordenados, mas incremento sofre race condition |
| `SafeAtomicFlightStrategy` | Não | Sim | Locks ordenados e contador atômico |

---

## Possíveis problemas

### Maven informa que não encontrou o `pom.xml`

Erro:

```text
The goal you specified requires a project to execute but there is no POM in this directory
```

Solução:

```bash
cd aero-concurrency/aeroporto
mvn clean package
```

Também é possível executar o Maven informando o caminho:

```bash
mvn -f aeroporto/pom.xml clean package
```

---

### Docker não encontra a pasta `target`

Erro:

```text
COPY target/*.jar app.jar
failed to solve: lstat /target: no such file or directory
```

Verifique se o JAR foi gerado:

```bash
mvn clean package
ls target
```

Também confira se o `.dockerignore` não contém:

```text
target/
```

Como o Dockerfile copia o JAR dessa pasta, ela precisa fazer parte do contexto de build.

---

### O terminal não volta após a execução

Quando uma estratégia entra em deadlock, threads bloqueadas em locks comuns podem impedir o encerramento da JVM.

O projeto utiliza locks interrompíveis na estratégia insegura, permitindo que `shutdownNow()` interrompa as threads depois que o deadlock for detectado.

Ao final, deve aparecer:

```text
aeroporto exited with code 0
```

---

### O CSV não aparece na máquina após rodar no Docker

Confirme se o `docker-compose.yml` contém:

```yaml
volumes:
  - ./results:/app/results
```

Depois execute:

```bash
docker compose up --build
ls results
```

---

## Comandos principais

### Compilar

```bash
mvn clean package
```

### Executar localmente

```bash
java -jar target/aeroporto-1.0-SNAPSHOT.jar
```

### Construir a imagem Docker

```bash
docker build -t aeroporto .
```

### Executar a imagem Docker

```bash
docker run --rm aeroporto
```

### Executar com Docker Compose

```bash
docker compose up --build
```

### Encerrar e remover recursos do Compose

```bash
docker compose down
```

### Visualizar o CSV

```bash
cat results/benchmark.csv
```

---

## Conclusão

O projeto demonstra que problemas de concorrência exigem soluções diferentes dependendo da causa.

A ordenação dos locks elimina o deadlock porque remove a espera circular. Porém, essa alteração não protege automaticamente outros estados compartilhados. O contador comum continua sujeito a condição de corrida, já que o operador `++` envolve leitura, incremento e escrita em etapas separadas.

A utilização de `AtomicInteger` garante que cada atualização do contador seja realizada de forma atômica. Dessa forma, a estratégia final combina prevenção de deadlock com consistência dos dados compartilhados.

A containerização permite executar os experimentos em um ambiente reproduzível e com limites explícitos de CPU e memória, enquanto a exportação em CSV facilita a análise dos resultados e a criação de gráficos.

---

## Autor

**Carlos Jr.**

Projeto desenvolvido para fins acadêmicos, com foco em processos, threads, concorrência e containerização.

---

## Licença

Este projeto foi desenvolvido para fins educacionais.