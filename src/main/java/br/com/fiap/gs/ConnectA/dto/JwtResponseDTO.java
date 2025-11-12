package br.com.fiap.gs.ConnectA.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private UsuarioResponseDTO usuario;

    public JwtResponseDTO(String token, UsuarioResponseDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }
}