package br.com.fiap.gs.ConnectA.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioAtualizacaoDTO {

    @Size(min = 3, max = 100, message = "{usuario.nome.size}")
    private String nome;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "{usuario.telefone.invalid}")
    private String telefone;

    @Size(min = 6, message = "{usuario.senha.size}")
    private String senha;
}