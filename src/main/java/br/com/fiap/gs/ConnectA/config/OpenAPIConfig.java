package br.com.fiap.gs.ConnectA.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ConnectA API - Global Solution FIAP",
                version = "1.0",
                description = """
            API de Usuários e Autenticação para o projeto ConnectA.
            
            Sistema de conexão entre mentores e mentorados focado no futuro do trabalho.
            
            **Recursos principais:**
            - Autenticação JWT
            - CRUD de Usuários
            - Análise de Currículo com IA (Spring AI + OpenAI)
            - Mensageria com RabbitMQ
            - Internacionalização (PT e ES)
            """,
                contact = @Contact(
                        name = "Equipe ConnectA",
                        email = "contato@connecta.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Servidor Local"),
                @Server(url = "https://connecta-api.azurewebsites.net", description = "Servidor Azure (Produção)")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "Autenticação JWT - Insira o token gerado no login",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig {
    // Configuração feita via anotações
}