package br.com.fiap.gs.ConnectA.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "TB_USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName = "SEQ_USUARIO", allocationSize = 1)
    @Column(name = "ID_USUARIO")
    private Long id;

    @NotBlank(message = "{usuario.nome.notblank}")
    @Size(min = 3, max = 100, message = "{usuario.nome.size}")
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "{usuario.email.notblank}")
    @Email(message = "{usuario.email.invalid}")
    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "{usuario.telefone.notblank}")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "{usuario.telefone.invalid}")
    @Column(name = "TELEFONE", nullable = false, length = 20)
    private String telefone;

    @NotBlank(message = "{usuario.senha.notblank}")
    @Size(min = 6, message = "{usuario.senha.size}")
    @Column(name = "SENHA", nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_PERFIL", nullable = false)
    private TipoPerfil tipoPerfil;

    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "DATA_ATUALIZACAO")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (ativo == null) {
            ativo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    // Métodos do UserDetails para Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + tipoPerfil.name()));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return ativo;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }

    public enum TipoPerfil {
        MENTOR,
        MENTORADO
    }
}