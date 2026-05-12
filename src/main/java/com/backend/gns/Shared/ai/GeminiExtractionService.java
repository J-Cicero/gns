package com.backend.gns.Shared.ai;

import com.backend.gns.Shared.domain.enums.TypeDocument;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
      String niveau, Integer creditsTotalValides, String mentionBac, String anneeObtention) {}

  public ExtractionResultat extraire(String urlImage, TypeDocument typeDoc) {
    try {
      String prompt = buildPrompt(typeDoc);

      // Corps de la requête Gemini
      Map<String, Object> body =
          Map.of(
              "contents",
              List.of(
                  Map.of(
                      "parts",
                      List.of(
                          Map.of(
                              "inline_data",
                              Map.of(
                                  "mime_type", "image/jpeg",
                                  "data", telechargerEtEncoder(urlImage))),
                          Map.of("text", prompt)))));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      String url =
          "https://generativelanguage.googleapis.com/v1beta/models/"
              + model
              + ":generateContent?key="
              + apiKey;

      ResponseEntity<Map<String, Object>> response =
          restTemplate.exchange(
              url,
              HttpMethod.POST,
              new HttpEntity<>(body, headers),
              new ParameterizedTypeReference<Map<String, Object>>() {});

      String texte = extraireTexte(response.getBody());
      return parseResultat(texte);

    } catch (Exception e) {
      // Si Gemini échoue → l'admin saisit manuellement
      return new ExtractionResultat(null, null, null, null);
    }
  }

  private String buildPrompt(TypeDocument typeDoc) {
    return switch (typeDoc) {
      case RELEVE_BAC ->
          """
                Analyse ce relevé de notes du BAC togolais.
                Retourne UNIQUEMENT ce JSON sans aucun autre texte :
                {"mentionBac": "moyenne ex: 13.50", "anneeObtention": "ex: 2023"}
                Si tu ne trouves pas une valeur mets null.
                """;
      case FICHE_UE ->
          """
                Analyse cette fiche UE universitaire togolaise.
                Retourne UNIQUEMENT ce JSON sans aucun autre texte :
                {"niveau": "L1 ou L2 ou L3", "creditsTotalValides": nombre entier}
                Si tu ne trouves pas une valeur mets null.
                """;
      case RELEVE_NOTES ->
          """
                Analyse ce relevé de notes universitaire togolais.
                Retourne UNIQUEMENT ce JSON sans aucun autre texte :
                {"niveau": "L1 ou L2 ou L3", "creditsTotalValides": nombre entier total validés}
                Si tu ne trouves pas une valeur mets null.
                """;
      case SOUCHE_TAMPONNEE ->
          """
                Analyse cette souche tamponnée.
                Retourne UNIQUEMENT ce JSON sans aucun autre texte :
                {"documentStatus": "valide ou invalide", "dateVerification": "ex: 2023-01-01"}
                Si tu ne trouves pas une valeur mets null.
                """;
    };
  }

  // Télécharge l'image depuis Cloudinary et l'encode en Base64
  private String telechargerEtEncoder(String url) throws Exception {
    ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);
    return java.util.Base64.getEncoder().encodeToString(response.getBody());
  }

  private String extraireTexte(Map<String, Object> response) {
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
    @SuppressWarnings("unchecked")
    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
    return (String) parts.get(0).get("text");
  }

  private ExtractionResultat parseResultat(String json) {
    try {
      // Nettoyer le JSON si Gemini ajoute des backticks
      String clean = json.trim().replace("```json", "").replace("```", "").trim();

      JsonNode node = objectMapper.readTree(clean);
      return new ExtractionResultat(
          node.has("niveau") ? node.get("niveau").asText(null) : null,
          node.has("creditsTotalValides") ? node.get("creditsTotalValides").asInt() : null,
          node.has("mentionBac") ? node.get("mentionBac").asText(null) : null,
          node.has("anneeObtention") ? node.get("anneeObtention").asText(null) : null);
    } catch (Exception e) {
      return new ExtractionResultat(null, null, null, null);
    }
  }
}
