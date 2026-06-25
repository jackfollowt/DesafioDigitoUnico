# Desafio Técnico Java — Banco Inter (Time Alice)

API REST para cálculo de **Dígito Único** e **CRUD de Usuário** com criptografia **RSA 2048 bits**,
cache em memória personalizado e documentação OpenAPI/Swagger.

## 🧰 Stack

- **Java 17**
- **Spring Boot 3.2** (Web, Data JPA, Validation)
- **H2** (banco de dados em memória)
- **springdoc-openapi** (Swagger UI)
- **Maven** (build, testes e execução)
- **JUnit 5** + Spring MockMvc (testes unitários e de integração)

## 📦 Estrutura de pacotes (`com.inter.desafio`)

| Pacote        | Responsabilidade |
|---------------|------------------|
| `model`       | Entidades JPA (`User`, `Calculo`) |
| `repository`  | Persistência (`UserRepository`) |
| `service`     | Regras de negócio (`DigitoUnicoService`, `UserService`, `CryptoService`) |
| `dto`         | Objetos de transferência de dados |
| `controller`  | Endpoints REST |
| `config`      | Swagger, gestão de chaves RSA (`KeyManager`) e cache (`DigitoUnicoCache`) |
| `exception`   | Tratamento centralizado de erros |

## 🎯 O cálculo do Dígito Único

O dígito único é a *raiz digital* de `P`, onde `P` é a string `n` concatenada `k` vezes.

Para suportar `n` com até ~10^6 dígitos e `k` até 10^5 **sem materializar `P`** nem estourar a pilha,
usamos a propriedade matemática:

```
soma_digitos(P) = k * soma_digitos(n)
raiz_digital(v) = 0            , se v == 0
                = 1 + (v-1) % 9 , caso contrário
```

Assim o cálculo é **O(tamanho de n)** — uma única varredura linear.

**Exemplo (especificação):** `n = 9875`, `k = 4` → resultado `8`.

## 🚀 Como compilar e executar

Pré-requisitos: **JDK 17** e **Maven 3.9+** no `PATH`.

```bash
# Compilar
mvn clean package

# Executar a aplicação
mvn spring-boot:run
# ou
java -jar target/deafio-inter-1.0-SNAPSHOT.jar
```

A aplicação sobe em `http://localhost:8080`.

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **Console H2:** http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:desafiodb`, user `sa`, sem senha)

## 🧪 Como executar os testes

```bash
mvn test
```

Cobertura de testes:
- `DigitoUnicoServiceTest` — regra de negócio, casos extremos (1.000.000 de dígitos) e validações.
- `DigitoUnicoCacheTest` — cache LRU mantém os últimos 10, descarte FIFO/LRU e thread-safety.
- `CryptoServiceTest` — round-trip RSA 2048, chaves distintas por usuário, Base64.
- `ApiIntegrationTest` — fluxo ponta a ponta da API (CRUD, cálculo, cache, criptografia).

## 🌐 Endpoints

### Dígito Único
| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/api/digito-unico` | Calcula o dígito único. Associa a um usuário se `usuarioId` for informado. |

```json
POST /api/digito-unico
{ "n": "9875", "k": 4, "usuarioId": 1 }
```

### Usuários (CRUD)
| Método | Rota | Descrição |
|--------|------|-----------|
| `POST`   | `/api/usuarios`            | Cria usuário (nome/email criptografados) |
| `GET`    | `/api/usuarios`            | Lista usuários |
| `GET`    | `/api/usuarios/{id}`       | Busca usuário |
| `PUT`    | `/api/usuarios/{id}`       | Atualiza usuário |
| `DELETE` | `/api/usuarios/{id}`       | Remove usuário |
| `GET`    | `/api/usuarios/{id}/calculos` | Recupera todos os cálculos do usuário |

### Chave pública
| Método | Rota | Descrição |
|--------|------|-----------|
| `POST` | `/api/chave-publica`          | Recebe a chave pública (Base64 X.509/SPKI) do cliente |
| `GET`  | `/api/chave-publica/servidor` | Retorna a chave pública do servidor (útil para testes) |

## 💾 Cache

Implementação **própria** (sem frameworks de cache de mercado), em `config/DigitoUnicoCache`:

- Mantém os **últimos 10 cálculos**, independente de usuário.
- LRU sobre `LinkedHashMap` (`accessOrder = true`) com `removeEldestEntry`.
- **Thread-safe** via `Collections.synchronizedMap` + blocos sincronizados nas operações compostas.
- Se o cálculo `(n, k)` já existe, retorna o valor sem reexecutar a função (`origemCache = true` na resposta).

## 🔐 Criptografia

- Algoritmo **RSA assimétrico 2048 bits** (`RSA/ECB/OAEPWithSHA-256AndMGF1Padding`).
- **Nome** e **email** do usuário são armazenados criptografados.
- Cada usuário pode usar uma **chave distinta**: o endpoint `POST /api/chave-publica` registra a chave
  pública do cliente, e novos dados passam a ser cifrados com ela.
- **Fluxo:** criptografia com a **chave pública** do cliente; descriptografia com a **chave privada** do cliente.
- Para permitir uma demonstração ponta a ponta, o servidor gera um par próprio na inicialização; ao ler
  um usuário ele tenta descriptografar com sua chave privada. Se os dados foram cifrados com uma chave
  externa, a resposta retorna o ciphertext e `criptografado = true` (apenas o cliente, dono da chave
  privada, consegue descriptografar).

## 📁 Arquivos de entrega na raiz

- `README.md` — este arquivo.
- `postman_collection.json` — coleção com os testes integrados (importar no Postman).
- `openapi.yaml` — especificação OpenAPI da API.
