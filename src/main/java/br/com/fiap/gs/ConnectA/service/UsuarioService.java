package br.com.fiap.gs.ConnectA.service;

import br.com.fiap.gs.ConnectA.exception.ResourceNotFoundException;
import br.com.fiap.gs.ConnectA.model.Usuario;
import br.com.fiap.gs.ConnectA.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;

    /**
     * Implementação do UserDetailsService para Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageSource.getMessage(
                                "usuario.not.found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));
    }

    /**
     * Busca usuário por ID com cache
     */
    @Cacheable(value = "usuarios", key = "#id")
    public Usuario buscarPorId(Long id) {
        log.debug("Buscando usuário com ID: {}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "usuario.not.found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));
    }

    /**
     * Busca usuário por email
     */
    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage(
                                "usuario.not.found",
                                null,
                                LocaleContextHolder.getLocale()
                        )
                ));
    }

    /**
     * Lista todos os usuários ativos com paginação
     */
    public Page<Usuario> listarTodos(Pageable pageable) {
        log.debug("Listando todos os usuários ativos - Página: {}", pageable.getPageNumber());
        return usuarioRepository.findByAtivoTrue(pageable);
    }

    /**
     * Busca usuários por tipo de perfil
     */
    public Page<Usuario> buscarPorTipoPerfil(
            Usuario.TipoPerfil tipoPerfil,
            Pageable pageable
    ) {
        log.debug("Buscando usuários por tipo de perfil: {}", tipoPerfil);
        return usuarioRepository.findByAtivoTrueAndTipoPerfil(tipoPerfil, pageable);
    }

    /**
     * Busca usuários por nome
     */
    public Page<Usuario> buscarPorNome(String nome, Pageable pageable) {
        log.debug("Buscando usuários por nome: {}", nome);
        return usuarioRepository.buscarPorNome(nome, pageable);
    }

    /**
     * Cria novo usuário
     */
    @Transactional
    public Usuario criar(Usuario usuario) {
        log.debug("Criando novo usuário: {}", usuario.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        log.info("Usuário criado com sucesso: {}", usuarioSalvo.getEmail());
        return usuarioSalvo;
    }

    /**
     * Atualiza dados do usuário
     */
    @Transactional
    @CacheEvict(value = "usuarios", key = "#id")
    public Usuario atualizar(Long id, String nome, String telefone, String senha) {
        log.debug("Atualizando usuário com ID: {}", id);

        Usuario usuario = buscarPorId(id);

        // Atualiza apenas os campos não nulos
        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome);
        }
        if (telefone != null && !telefone.isBlank()) {
            usuario.setTelefone(telefone);
        }
        if (senha != null && !senha.isBlank()) {
            usuario.setSenha(passwordEncoder.encode(senha));
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        log.info("Usuário atualizado com sucesso: {}", usuarioAtualizado.getEmail());

        return usuarioAtualizado;
    }

    /**
     * Desativa usuário (soft delete)
     */
    @Transactional
    @CacheEvict(value = "usuarios", key = "#id")
    public void desativar(Long id) {
        log.debug("Desativando usuário com ID: {}", id);

        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuário desativado com sucesso: {}", usuario.getEmail());
    }

    /**
     * Verifica se email já existe
     */
    public boolean emailJaExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}