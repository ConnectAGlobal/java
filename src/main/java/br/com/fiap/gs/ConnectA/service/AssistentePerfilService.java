package br.com.fiap.gs.ConnectA.service;

import br.com.fiap.gs.ConnectA.config.GroqConfig;
import br.com.fiap.gs.ConnectA.dto.groq.GroqRequest;
import br.com.fiap.gs.ConnectA.dto.groq.GroqResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistentePerfilService {

    private final RestTemplate restTemplate;
    private final GroqConfig groqConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PROMPT_PT = """
        Você é um especialista em análise de currículos e extração de competências profissionais.
        
        Analise o currículo abaixo e extraia TODAS as habilidades técnicas (hard skills) e competências 
        comportamentais (soft skills) mencionadas.
        
        CURRÍCULO:
        %s
        
        INSTRUÇÕES IMPORTANTES:
        1. Identifique linguagens de programação (Java, Python, JavaScript, C#, etc)
        2. Identifique frameworks e bibliotecas (Spring Boot, React, Angular, Django, etc)
        3. Identifique bancos de dados (MySQL, PostgreSQL, MongoDB, Oracle, SQL Server, etc)
        4. Identifique ferramentas e tecnologias (Docker, Kubernetes, Git, Jenkins, etc)
        5. Identifique clouds e plataformas (AWS, Azure, GCP, Heroku, etc)
        6. Identifique metodologias (Scrum, Agile, Kanban, CI/CD, TDD, etc)
        7. Identifique soft skills (liderança, comunicação, trabalho em equipe, etc)
        
        FORMATO DA RESPOSTA:
        Retorne APENAS um JSON válido no seguinte formato (sem explicações, sem markdown, sem texto adicional):
        {
          "skills": ["skill1", "skill2", "skill3"]
        }
        
        IMPORTANTE: 
        - Retorne APENAS o JSON, nada mais
        - Não inclua ```json ou ``` 
        - Liste no máximo 20 skills mais relevantes
        - Use os nomes exatos das tecnologias conforme aparecem no currículo
        """;

    private static final String PROMPT_ES = """
        Eres un experto en análisis de currículums y extracción de competencias profesionales.
        
        Analiza el currículum a continuación y extrae TODAS las habilidades técnicas (hard skills) y competencias 
        comportamentales (soft skills) mencionadas.
        
        CURRÍCULUM:
        %s
        
        INSTRUCCIONES IMPORTANTES:
        1. Identifica lenguajes de programación (Java, Python, JavaScript, C#, etc)
        2. Identifica frameworks y bibliotecas (Spring Boot, React, Angular, Django, etc)
        3. Identifica bases de datos (MySQL, PostgreSQL, MongoDB, Oracle, SQL Server, etc)
        4. Identifica herramientas y tecnologías (Docker, Kubernetes, Git, Jenkins, etc)
        5. Identifica clouds y plataformas (AWS, Azure, GCP, Heroku, etc)
        6. Identifica metodologías (Scrum, Agile, Kanban, CI/CD, TDD, etc)
        7. Identifica soft skills (liderazgo, comunicación, trabajo en equipo, etc)
        
        FORMATO DE RESPUESTA:
        Devuelve SOLO un JSON válido en el siguiente formato (sin explicaciones, sin markdown, sin texto adicional):
        {
          "skills": ["skill1", "skill2", "skill3"]
        }
        
        IMPORTANTE: 
        - Devuelve SOLO el JSON, nada más
        - No incluyas ```json o ``` 
        - Lista máximo 20 skills más relevantes
        - Usa los nombres exactos de las tecnologías como aparecen en el currículum
        """;

    public List<String> analisarCurriculo(String curriculo, String idioma) {
        log.info("Iniciando análise de currículo com Groq - Idioma: {}", idioma);
        log.debug("Currículo recebido: {}", curriculo.substring(0, Math.min(100, curriculo.length())) + "...");

        try {
            String promptTemplate = idioma.equals("es-ES") ? PROMPT_ES : PROMPT_PT;
            String promptText = String.format(promptTemplate, curriculo);

            GroqRequest request = new GroqRequest();
            request.setModel(groqConfig.getModel());
            request.setTemperature(0.3);
            request.setMaxTokens(500);
            request.setMessages(List.of(
                    new GroqRequest.Message("user", promptText)
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqConfig.getApiKey());

            HttpEntity<GroqRequest> entity = new HttpEntity<>(request, headers);

            log.debug("Chamando Groq API: {}", groqConfig.getApiUrl());
            GroqResponse response = restTemplate.postForObject(
                    groqConfig.getApiUrl(),
                    entity,
                    GroqResponse.class
            );

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                log.error("Resposta vazia da API Groq");
                return List.of();
            }

            String resposta = response.getChoices().get(0).getMessage().getContent();
            log.debug("Resposta bruta do Groq: {}", resposta);

            List<String> skills = parseRespostaJSON(resposta);

            if (skills.isEmpty()) {
                log.warn("Parsing JSON falhou, tentando parsing por vírgula");
                skills = parseRespostaCsv(resposta);
            }

            log.info("Análise concluída - {} skills identificadas: {}", skills.size(), skills);
            return skills;

        } catch (Exception e) {
            log.error("Erro ao analisar currículo com Groq", e);
            throw new RuntimeException("Erro ao processar análise: " + e.getMessage(), e);
        }
    }

    private List<String> parseRespostaJSON(String resposta) {
        try {
            String jsonLimpo = resposta.trim()
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            log.debug("JSON limpo: {}", jsonLimpo);

            JsonNode root = objectMapper.readTree(jsonLimpo);
            JsonNode skillsNode = root.get("skills");

            if (skillsNode != null && skillsNode.isArray()) {
                List<String> skills = new ArrayList<>();
                for (JsonNode skillNode : skillsNode) {
                    String skill = skillNode.asText().trim();
                    if (!skill.isEmpty()) {
                        skills.add(skill);
                    }
                }
                return skills;
            }

            return List.of();

        } catch (Exception e) {
            log.debug("Não foi possível parsear como JSON: {}", e.getMessage());
            return List.of();
        }
    }

    private List<String> parseRespostaCsv(String resposta) {
        return Arrays.stream(resposta.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .filter(s -> !s.toLowerCase().startsWith("aqui"))
                .filter(s -> !s.toLowerCase().startsWith("segue"))
                .filter(s -> s.length() > 2)
                .limit(20)
                .collect(Collectors.toList());
    }
}