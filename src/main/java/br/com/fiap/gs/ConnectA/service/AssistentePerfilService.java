package br.com.fiap.gs.ConnectA.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistentePerfilService {

    private final ChatModel chatModel;

    private static final String PROMPT_PT = """
        Você é um assistente especializado em análise de currículos e identificação de competências profissionais.
        
        Analise o seguinte currículo e extraia as principais habilidades técnicas (hard skills) e competências 
        comportamentais (soft skills) relevantes para o mercado de trabalho atual.
        
        Currículo:
        %s
        
        Retorne APENAS uma lista de skills separadas por vírgula, sem numeração, explicações ou formatação adicional.
        Foque em skills modernas e relevantes para o futuro do trabalho.
        Limite a resposta a no máximo 15 skills mais importantes.
        """;

    private static final String PROMPT_ES = """
        Eres un asistente especializado en análisis de currículums e identificación de competencias profesionales.
        
        Analiza el siguiente currículum y extrae las principales habilidades técnicas (hard skills) y competencias 
        comportamentales (soft skills) relevantes para el mercado laboral actual.
        
        Currículum:
        %s
        
        Devuelve SOLO una lista de skills separadas por coma, sin numeración, explicaciones o formato adicional.
        Enfócate en skills modernas y relevantes para el futuro del trabajo.
        Limita la respuesta a un máximo de 15 skills más importantes.
        """;

    /**
     * Analisa currículo e sugere skills usando Spring AI
     */
    public List<String> analisarCurriculo(String curriculo, String idioma) {
        log.debug("Iniciando análise de currículo com IA - Idioma: {}", idioma);

        try {
            // Seleciona prompt baseado no idioma
            String promptTemplate = idioma.equals("es-ES") ? PROMPT_ES : PROMPT_PT;
            String promptText = String.format(promptTemplate, curriculo);

            // Cria o prompt
            Prompt prompt = new Prompt(promptText);

            // Chama a API OpenAI via Spring AI
            String resposta = chatModel.call(prompt).getResult().getOutput().getContent();

            // Processa a resposta (separa as skills por vírgula)
            List<String> skills = Arrays.stream(resposta.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .limit(15)
                    .collect(Collectors.toList());

            log.info("Análise de currículo concluída - {} skills identificadas", skills.size());

            return skills;

        } catch (Exception e) {
            log.error("Erro ao analisar currículo com IA", e);
            return List.of();
        }
    }
}