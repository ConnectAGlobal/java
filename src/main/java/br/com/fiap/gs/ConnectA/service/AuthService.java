package br.com.fiap.gs.ConnectA.service;

import br.com.fiap.gs.ConnectA.config.security.JwtService;
import br.com.fiap.gs.ConnectA.exception.BusinessException;
import br.com.fiap.gs.ConnectA.model.Usuario;
import br.com.fiap.gs.ConnectA.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final MessageSource messageSource;
    private final MensageriaService mensageriaService;

    /**
     * Registra um novo usuário no sistema
     * Retorna o usuário criado e o token JWT
     */
    @Transactional
    public Usuario registrar(
            String nome,
            String email,
            String telefone,
            String senha,
            String tipoPerfil
    ) {
        log.debug("Iniciando registro de novo usuário: {}", email);

        // Verifica se email já existe
        if (usuarioRepository.existsByEmail(email)) {
            throw new BusinessException(
                    messageSource.getMessage(
                            "auth.register.email.exists",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        // Cria novo usuário
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setTelefone(telefone);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setTipoPerfil(Usuario.TipoPerfil.valueOf(tipoPerfil.toUpperCase()));
        usuario.setAtivo(true);

        // Salva no banco
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário registrado com sucesso: {}", usuarioSalvo.getEmail());

        // Publica mensagem no RabbitMQ para notificar outros microserviços
        mensageriaService.publicarNovoUsuario(usuarioSalvo);

        return usuarioSalvo;
    }

    /**
     * Gera token JWT para o usuário
     */
    public String gerarToken(Usuario usuario) {
        return jwtService.generateToken(usuario);
    }

    /**
     * Autentica usuário e retorna o usuário autenticado
     */
    public Usuario autenticar(String email, String senha) {
        log.debug("Autenticando usuário: {}", email);

        try {
            // Autentica com Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, senha)
            );
        } catch (Exception e) {
            log.error("Erro na autenticação do usuário: {}", email, e);
            throw new BusinessException(
                    messageSource.getMessage(
                            "auth.login.error",
                            null,
                            LocaleContextHolder.getLocale()
                    )
            );
        }

        // Busca usuário
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        messageSource.getMessage(
                                "auth.login.error",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));

        log.info("Usuário autenticado com sucesso: {}", usuario.getEmail());
        return usuario;
    }
}