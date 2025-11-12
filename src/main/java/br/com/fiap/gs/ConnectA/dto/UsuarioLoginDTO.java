package br.com.fiap.gs.ConnectA.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLoginDTO {

    @NotBlank(message = "{usuario.email.notblank}")
    @Email(message = "{usuario.email.invalid}")
    private String email;

    @NotBlank(message = "{usuario.senha.notblank}")
    private String senha;
}