package com.backend.gns.Shared.ai;

import com.backend.gns.Shared.domain.enums.TypeDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class GeminiExtractionService {

  @Value("${gemini.api-key}")
  private String apiKey;

  @Value("${gemini.model}")
  private String model;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public GeminiExtractionService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public record ExtractionResultat(
      String niveau, 
      Integer creditsTotalValides, 
      BigDecimal moyenneBac, 
      String anneeObtention,
      BigDecimal montantScolarite,
      String nomComplet,
      String dateNaissance,
      Double scoreFiabilite) {}

  public ExtractionResultat extraire(String urlImage, TypeDocument typeDoc) {
    try {
      String prompt = buildPrompt(typeDoc);

      Map<String, Object> body =
          Map.of(
              "contents",
              List.of(Map.of("parts", List.of(
                          Map.of("inline_data", Map.of("mime_type", "image/jpeg", "data", telechargerEtEncoder(urlImage))),
                          Map.of("text", prompt)))));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), new ParameterizedTypeReference<Map<String, Object>>() {});

      String texte = extraireTexte(response.getBody());
      return parseResultat(texte);
    } catch (Exception e) {
      log.error("Erreur Gemini: {}", e.getMessage());
      return new ExtractionResultat(null, null, null, null, null, null, null, 0.0);
    }
  }

  private String buildPrompt(TypeDocument typeDoc) {
    return switch (typeDoc) {
      case RELEVE_BAC -> "Analyse ce relevé du BAC. Retourne JSON: {\"moyenneBac\": 13.5, \"anneeObtention\": \"2023\", \"nomComplet\": \"...\"}";
      case FICHE_UE -> "Analyse cette fiche UE. Retourne JSON: {\"niveau\": \"L1\", \"montantScolarite\": 25000, \"creditsTotalValides\": 30}";
      case RELEVE_NOTES -> "Analyse ce relevé notes. Retourne JSON: {\"niveau\": \"L2\", \"creditsTotalValides\": 60}";
      case SOUCHE_TAMPONNEE -> "Analyse ce mandat. Retourne JSON: {\"documentStatus\": \"valide\", \"nomBanque\": \"...\"}";
      case PIECE_IDENTITE -> "Analyse cette ID. Retourne JSON: {\"nomComplet\": \"...\", \"dateNaissance\": \"YYYY-MM-DD\"}";
    };
  }

  private String telechargerEtEncoder(String url) throws Exception {
    ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
    return Base64.getEncoder().encodeToString(response.getBody());
  }

  private String extraireTexte(Map<String, Object> response) {
    JsonNode root = objectMapper.valueToTree(response);
    return root.path("candidates").path(0).path("content").path("parts").path(0).path("text").asText("");
  }

  private ExtractionResultat parseResultat(String json) {
    try {
      String clean = json.trim().replace("```json", "").replace("```", "").trim();
      JsonNode node = objectMapper.readTree(clean);
      return new ExtractionResultat(
          node.has("niveau") ? node.get("niveau").asText(null) : null,
          node.has("creditsTotalValides") ? (node.get("creditsTotalValides").isNull() ? null : node.get("creditsTotalValides").asInt()) : null,
          node.has("moyenneBac") ? (node.get("moyenneBac").isNull() ? null : new BigDecimal(node.get("moyenneBac").asText())) : null,
          node.has("anneeObtention") ? node.get("anneeObtention").asText(null) : null,
          node.has("montantScolarite") ? (node.get("montantScolarite").isNull() ? null : new BigDecimal(node.get("montantScolarite").asText())) : null,
          node.has("nomComplet") ? node.get("nomComplet").asText(null) : null,
          node.has("dateNaissance") ? node.get("dateNaissance").asText(null) : null,
          node.has("scoreFiabilite") ? node.get("scoreFiabilite").asDouble() : 95.0);
    } catch (Exception e) {
      return new ExtractionResultat(null, null, null, null, null, null, null, 0.0);
    }
  }
}
