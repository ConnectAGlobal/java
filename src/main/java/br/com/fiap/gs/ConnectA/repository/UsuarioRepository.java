package br.com.fiap.gs.ConnectA.repository;

import br.com.fiap.gs.ConnectA.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca usuário por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuários ativos por tipo de perfil com paginação
     */
    Page<Usuario> findByAtivoTrueAndTipoPerfil(
            Usuario.TipoPerfil tipoPerfil,
            Pageable pageable
    );

    /**
     * Busca usuários ativos com paginação
     */
    Page<Usuario> findByAtivoTrue(Pageable pageable);

    /**
     * Busca usuários por nome (busca parcial - LIKE)
     */
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND u.ativo = true")
    Page<Usuario> buscarPorNome(@Param("nome") String nome, Pageable pageable);
}