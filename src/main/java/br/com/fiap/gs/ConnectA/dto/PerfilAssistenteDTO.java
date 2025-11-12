package br.com.fiap.gs.ConnectA.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PerfilAssistenteDTO {

    @NotBlank(message = "{assistente.curriculo.notblank}")
    private String curriculo; // Texto do currículo colado pelo usuário

    private String idioma = "pt-BR"; // pt-BR ou es-ES
}