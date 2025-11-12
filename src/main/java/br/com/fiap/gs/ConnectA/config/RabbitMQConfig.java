package br.com.fiap.gs.ConnectA.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "connecta.exchange";
    public static final String QUEUE_NOVO_USUARIO = "connecta.usuario.novo";
    public static final String ROUTING_KEY_NOVO_USUARIO = "usuario.novo";

    /**
     * Exchange do tipo Topic para roteamento flexível
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    /**
     * Fila para novos usuários
     */
    @Bean
    public Queue queueNovoUsuario() {
        return QueueBuilder
                .durable(QUEUE_NOVO_USUARIO)
                .build();
    }

    /**
     * Binding entre exchange e fila
     */
    @Bean
    public Binding bindingNovoUsuario(Queue queueNovoUsuario, TopicExchange exchange) {
        return BindingBuilder
                .bind(queueNovoUsuario)
                .to(exchange)
                .with(ROUTING_KEY_NOVO_USUARIO);
    }

    /**
     * Conversor de mensagens para JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * Template do RabbitMQ configurado com conversor JSON
     */
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }

    /**
     * ObjectMapper para serialização JSON
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}