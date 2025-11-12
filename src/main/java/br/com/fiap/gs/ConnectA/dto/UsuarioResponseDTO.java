package br.com.fiap.gs.ConnectA.dto;

import br.com.fiap.gs.ConnectA.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String tipoPerfil;
    private Boolean ativo;

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getTipoPerfil().name(),
                usuario.getAtivo()
        );
    }
}