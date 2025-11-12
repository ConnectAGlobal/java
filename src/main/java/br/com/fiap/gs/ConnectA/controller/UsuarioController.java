package br.com.fiap.gs.ConnectA.controller;

import br.com.fiap.gs.ConnectA.dto.UsuarioAtualizacaoDTO;
import br.com.fiap.gs.ConnectA.dto.UsuarioResponseDTO;
import br.com.fiap.gs.ConnectA.model.Usuario;
import br.com.fiap.gs.ConnectA.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    /**
     * Lista todos os usuários com paginação
     */
    @GetMapping
    @Operation(summary = "Listar todos os usuários", description = "Retorna lista paginada de usuários ativos")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<UsuarioResponseDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        log.info("Requisição para listar todos os usuários - Página: {}", pageable.getPageNumber());

        Page<Usuario> usuarios = usuarioService.listarTodos(pageable);

        // Converte Page<Usuario> para Page<UsuarioResponseDTO>
        Page<UsuarioResponseDTO> response = usuarios.map(UsuarioResponseDTO::fromEntity);

        return ResponseEntity.ok(response);
    }

    /**
     * Busca usuário por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna dados de um usuário específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @Parameter(description = "ID do usuário") @PathVariable Long id
    ) {
        log.info("Requisição para buscar usuário com ID: {}", id);

        Usuario usuario = usuarioService.buscarPorId(id);
        UsuarioResponseDTO response = UsuarioResponseDTO.fromEntity(usuario);

        return ResponseEntity.ok(response);
    }

    /**
     * Busca usuários por tipo de perfil
     */
    @GetMapping("/perfil/{tipoPerfil}")
    @Operation(summary = "Buscar por tipo de perfil", description = "Lista usuários por tipo (MENTOR ou MENTORADO)")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<UsuarioResponseDTO>> buscarPorTipoPerfil(
            @Parameter(description = "Tipo de perfil (MENTOR ou MENTORADO)") @PathVariable String tipoPerfil,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable
    ) {
        log.info("Requisição para buscar usuários do tipo: {}", tipoPerfil);

        Usuario.TipoPerfil tipo = Usuario.TipoPerfil.valueOf(tipoPerfil.toUpperCase());
        Page<Usuario> usuarios = usuarioService.buscarPorTipoPerfil(tipo, pageable);

        Page<UsuarioResponseDTO> response = usuarios.map(UsuarioResponseDTO::fromEntity);

        return ResponseEntity.ok(response);
    }

    /**
     * Busca usuários por nome
     */
    @GetMapping("/buscar")
    @Operation(summary = "Buscar por nome", description = "Busca usuários por nome (busca parcial)")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<UsuarioResponseDTO>> buscarPorNome(
            @Parameter(description = "Nome ou parte do nome") @RequestParam String nome,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable
    ) {
        log.info("Requisição para buscar usuários por nome: {}", nome);

        Page<Usuario> usuarios = usuarioService.buscarPorNome(nome, pageable);
        Page<UsuarioResponseDTO> response = usuarios.map(UsuarioResponseDTO::fromEntity);

        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza dados do usuário
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário", description = "Atualiza dados de um usuário existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Valid @RequestBody UsuarioAtualizacaoDTO dto
    ) {
        log.info("Requisição para atualizar usuário com ID: {}", id);

        Usuario usuario = usuarioService.atualizar(
                id,
                dto.getNome(),
                dto.getTelefone(),
                dto.getSenha()
        );

        UsuarioResponseDTO response = UsuarioResponseDTO.fromEntity(usuario);

        return ResponseEntity.ok(response);
    }

    /**
     * Desativa usuário (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário", description = "Desativa um usuário (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário desativado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Map<String, String>> desativar(
            @Parameter(description = "ID do usuário") @PathVariable Long id
    ) {
        log.info("Requisição para desativar usuário com ID: {}", id);

        usuarioService.desativar(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", messageSource.getMessage(
                "usuario.deleted",
                null,
                LocaleContextHolder.getLocale()
        ));

        return ResponseEntity.ok(response);
    }
}