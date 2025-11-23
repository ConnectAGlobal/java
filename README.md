# ConnectA

Projeto backend em Java (Spring Boot) para o ConnectA.

**Desenvolvedores**
- Pedro Henrique dos Santos — RM 559064
- Vinícius de Oliveira Coutinho — RM 556182
- Thiago Thomaz Sales Conceição — RM 557992

**Descrição**
Este repositório contém a API do ConnectA, construída com Spring Boot. Inclui autenticação (JWT), configurações de segurança, integração com filas (RabbitMQ), suporte a internacionalização e endpoints para gerenciamento de usuários e assistente.

**Deploy público**
O projeto está disponível em: https://conecta-myfq.onrender.com

Obs.: o deploy está ativo e pode ser testado (veja seção "Usando Insomnia" para instruções).

**Pré-requisitos**
- Java 17+ (ou compatível com o projeto)
- Maven (ou usar o wrapper incluído)
- Docker (opcional)
- RabbitMQ (se usar filas em ambiente local)

**Build (Windows)**
Execute na raiz do projeto:

```
.\mvnw.cmd clean package -DskipTests
```

**Build (Linux / macOS)**

```
./mvnw clean package -DskipTests
```
./mvnw clean package -DskipTests
```

**Executar (usando Maven Wrapper)**

```
.\mvnw.cmd spring-boot:run    # Windows
./mvnw spring-boot:run      # Linux/macOS
```

Ou execute o JAR gerado:

```
java -jar target/*.jar
```

**Testes**

```
.\mvnw.cmd test
```

**Endpoints principais**
- `POST /api/v1/auth/register` : Registrar usuário
- `POST /api/v1/auth/login` : Autenticação (retorna JWT)
- `GET  /api/v1/auth/me` : Atual usuário (requer autenticação)
- `GET  /api/v1/usuarios` : Listar usuários
- `GET  /api/v1/usuarios/{id}` : Obter usuário por id
- `GET  /api/v1/usuarios/perfil/{tipoPerfil}` : Buscar por perfil
- `GET  /api/v1/usuarios/buscar` : Buscar usuários (query params)
- `PUT  /api/v1/usuarios/{id}` : Atualizar usuário
- `DELETE /api/v1/usuarios/{id}` : Deletar usuário
- `POST /api/v1/assistente/analisar-curriculo` : Analisar currículo (assistente)
````
Obs.: Consulte os controladores em `src/main/java/br/com/fiap/gs/ConnectA/controller` para detalhes das entradas e parâmetros.

**Usando Insomnia (ou outra ferramenta REST)**
Recomendamos usar o Insomnia (ou Postman) para testar os endpoints, especialmente o fluxo de autenticação JWT.

Passos básicos para testar o login (exemplo):
1. Abrir Insomnia e criar uma requisição `POST` para:
	 `https://conecta-myfq.onrender.com/api/v1/auth/login`
2. No body da requisição, usar JSON com campos `email` e `senha` (ex.: `{ "email": "joao@teste.com", "senha": "senha123" }`).
3. Enviar a requisição — a resposta inclui o token JWT e informações do usuário.

Exemplo de resposta (formato):
```
{
	"token": "<jwt-token-aqui>",
	"tipo": "Bearer",
	"usuario": {
		"id": 1,
		"nome": "Joao Cabral",
		"email": "joao@teste.com",
		"telefone": "11971659228",
		"tipoPerfil": "MENTOR",
		"ativo": true
	}
}
```

Você pode usar o valor retornado em `token` para autorizar outras requisições adicionando o header:
`Authorization: Bearer <jwt-token-aqui>`

Imagem de exemplo do Insomnia (login e resposta):


<img src="docs/insomnia-login-preview.png" alt="Insomnia - login" width="700" />
=======


**Configuração**
- Arquivo principal: `src/main/resources/application.properties`.
- Suporte a variáveis de ambiente e `.env` (ver `DotenvConfig`).
- Configurações de RabbitMQ em `RabbitMQConfig` (host, port, credentials).
- OpenAPI/Swagger configurado (ver `OpenAPIConfig`) — documentação automática disponível em `/v3/api-docs` e, se presente, interface Swagger UI.

**Variáveis de ambiente recomendadas**
- `SPRING_DATASOURCE_URL` - URL do banco de dados (opcional, se usar DB externo)
- `SPRING_DATASOURCE_USERNAME` - usuário DB
- `SPRING_DATASOURCE_PASSWORD` - senha DB
- `JWT_SECRET` - segredo para assinar os tokens JWT
- `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD` - configuração do RabbitMQ


**Configuração**
- Arquivo principal: `src/main/resources/application.properties`.
- Suporte a variáveis de ambiente e `.env` (ver `DotenvConfig`).
- Configurações de RabbitMQ em `RabbitMQConfig` (host, port, credentials).
- OpenAPI/Swagger configurado (ver `OpenAPIConfig`) — documentação automática disponível em `/v3/api-docs` e, se presente, interface Swagger UI.

**Docker**
Para gerar a imagem Docker:

```
docker build -t connecta .
docker run -p 8080:8080 --env-file .env connecta
```

**Observações**
- O projeto já inclui configurações de segurança JWT (`JwtService`, `JwtAuthenticationFilter`).
- Internacionalização (`i18n/messages*.properties`) está disponível em `src/main/resources/i18n`.

- .env enviado indiviualmente para o teams do professor.
