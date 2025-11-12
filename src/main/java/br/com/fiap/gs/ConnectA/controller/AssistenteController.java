package br.com.fiap.gs.ConnectA.controller;

import br.com.fiap.gs.ConnectA.dto.PerfilAssistenteDTO;
import br.com.fiap.gs.ConnectA.dto.SkillsSugeridasDTO;
import br.com.fiap.gs.ConnectA.service.AssistentePerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assistente")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Assistente de Perfil", description = "Endpoints para análise de currículo com IA")
public class AssistenteController {

    private final AssistentePerfilService assistenteService;
    private final MessageSource messageSource;

    /**
     * Analisa currículo e sugere skills usando Spring AI
     */
    @PostMapping("/analisar-curriculo")
    @Operation(
            summary = "Analisar currículo com IA",
            description = "Recebe um currículo em texto e retorna sugestões de skills usando inteligência artificial"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Análise concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "500", description = "Erro ao processar com IA")
    })
    public ResponseEntity<SkillsSugeridasDTO> analisarCurriculo(
            @Valid @RequestBody PerfilAssistenteDTO dto
    ) {
        log.info("Requisição para análise de currículo - Idioma: {}", dto.getIdioma());

        try {
            // Service retorna List<String> de skills
            List<String> skills = assistenteService.analisarCurriculo(
                    dto.getCurriculo(),
                    dto.getIdioma()
            );

            // Cria mensagem de sucesso baseada no idioma
            String mensagem = dto.getIdioma().equals("es-ES")
                    ? messageSource.getMessage(
                    "assistente.analise.sucesso",
                    null,
                    java.util.Locale.forLanguageTag("es-ES")
            )
                    : messageSource.getMessage(
                    "assistente.analise.sucesso",
                    null,
                    java.util.Locale.forLanguageTag("pt-BR")
            );

            // Converte para DTO
            SkillsSugeridasDTO response = new SkillsSugeridasDTO(skills, mensagem);

            log.info("Análise de currículo concluída - {} skills identificadas", skills.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erro ao analisar currículo", e);

            String mensagemErro = dto.getIdioma().equals("es-ES")
                    ? messageSource.getMessage(
                    "assistente.analise.erro",
                    null,
                    java.util.Locale.forLanguageTag("es-ES")
            )
                    : messageSource.getMessage(
                    "assistente.analise.erro",
                    null,
                    java.util.Locale.forLanguageTag("pt-BR")
            );

            SkillsSugeridasDTO errorResponse = new SkillsSugeridasDTO(List.of(), mensagemErro);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}