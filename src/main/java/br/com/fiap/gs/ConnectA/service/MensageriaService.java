package br.com.fiap.gs.ConnectA.service;

import br.com.fiap.gs.ConnectA.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensageriaService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String EXCHANGE = "connecta.exchange";
    private static final String ROUTING_KEY_NOVO_USUARIO = "usuario.novo";

    /**
     * Publica mensagem quando um novo usuário é criado
     * Outros microserviços podem consumir esta mensagem
     */
    public void publicarNovoUsuario(Usuario usuario) {
        try {
            Map<String, Object> mensagem = new HashMap<>();
            mensagem.put("id", usuario.getId());
            mensagem.put("nome", usuario.getNome());
            mensagem.put("email", usuario.getEmail());
            mensagem.put("tipoPerfil", usuario.getTipoPerfil().name());
            mensagem.put("timestamp", System.currentTimeMillis());

            String mensagemJson = objectMapper.writeValueAsString(mensagem);

            rabbitTemplate.convertAndSend(
                    EXCHANGE,
                    ROUTING_KEY_NOVO_USUARIO,
                    mensagemJson
            );

            log.info("Mensagem publicada no RabbitMQ - Novo usuário: {}", usuario.getEmail());

        } catch (Exception e) {
            log.error("Erro ao publicar mensagem no RabbitMQ", e);
            // Não propaga a exceção para não interromper o fluxo principal
        }
    }
}