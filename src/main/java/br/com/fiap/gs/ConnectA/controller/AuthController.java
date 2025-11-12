package br.com.fiap.gs.ConnectA.controller;

import br.com.fiap.gs.ConnectA.dto.JwtResponseDTO;
import br.com.fiap.gs.ConnectA.dto.UsuarioLoginDTO;
import br.com.fiap.gs.ConnectA.dto.UsuarioRegistroDTO;
import br.com.fiap.gs.ConnectA.dto.UsuarioResponseDTO;
import br.com.fiap.gs.ConnectA.model.Usuario;
import br.com.fiap.gs.ConnectA.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Autenticação", description = "Endpoints para registro e autenticação de usuários")
public class AuthController {

    private final AuthService authService;
    private final MessageSource messageSource;

    /**
     * Registro de novo usuário
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria uma nova conta de usuário no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário registrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou email já cadastrado")
    })
    public ResponseEntity<JwtResponseDTO> registrar(@Valid @RequestBody UsuarioRegistroDTO dto) {
        log.info("Requisição de registro recebida para email: {}", dto.getEmail());

        // Service retorna a entidade Usuario
        Usuario usuario = authService.registrar(
                dto.getNome(),
                dto.getEmail(),
                dto.getTelefone(),
                dto.getSenha(),
                dto.getTipoPerfil()
        );

        // Gera o token JWT
        String token = authService.gerarToken(usuario);

        // Converte entidade para DTO (camada controller)
        UsuarioResponseDTO usuarioResponse = UsuarioResponseDTO.fromEntity(usuario);

        // Cria resposta com token
        JwtResponseDTO response = new JwtResponseDTO(token, usuarioResponse);

        log.info("Usuário registrado com sucesso: {}", usuario.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login de usuário
     */
    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Autentica usuário e retorna token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody UsuarioLoginDTO dto) {
        log.info("Requisição de login recebida para email: {}", dto.getEmail());

        // Autentica e retorna a entidade Usuario
        Usuario usuario = authService.autenticar(dto.getEmail(), dto.getSenha());

        // Gera o token JWT
        String token = authService.gerarToken(usuario);

        // Converte entidade para DTO
        UsuarioResponseDTO usuarioResponse = UsuarioResponseDTO.fromEntity(usuario);

        // Cria resposta com token
        JwtResponseDTO response = new JwtResponseDTO(token, usuarioResponse);

        log.info("Login realizado com sucesso: {}", usuario.getEmail());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de teste de autenticação
     */
    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário autenticado", description = "Retorna informações do usuário atualmente autenticado")
    @ApiResponse(responseCode = "200", description = "Dados do usuário retornados com sucesso")
    public ResponseEntity<Map<String, String>> me() {
        Map<String, String> response = new HashMap<>();
        response.put("message", messageSource.getMessage(
                "auth.login.success",
                null,
                LocaleContextHolder.getLocale()
        ));
        return ResponseEntity.ok(response);
    }
}