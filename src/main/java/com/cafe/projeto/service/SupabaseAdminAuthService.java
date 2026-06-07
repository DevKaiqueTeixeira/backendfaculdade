package com.cafe.projeto.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
public class SupabaseAdminAuthService {

    private final String supabaseUrl;
    private final String serviceRoleKey;
    private final String publishableKey;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public SupabaseAdminAuthService(
            @Value("${app.supabase.url:}") String supabaseUrl,
            @Value("${app.supabase.service-role-key:}") String serviceRoleKey,
            @Value("${app.supabase.publishable-key:}") String publishableKey,
            ObjectMapper objectMapper
    ) {
        this.supabaseUrl = supabaseUrl;
        this.serviceRoleKey = serviceRoleKey;
        this.publishableKey = publishableKey;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    public String criarUsuario(String email, String senha, Map<String, Object> metadata) {
        validarConfiguracao();

        if (temServiceRole()) {
            return criarUsuarioViaAdmin(email, senha, metadata);
        }

        return criarUsuarioViaSignup(email, senha, metadata);
    }

    private String criarUsuarioViaAdmin(String email, String senha, Map<String, Object> metadata) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "email", email,
                    "password", senha,
                    "email_confirm", true,
                    "user_metadata", metadata
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(normalizarBaseUrl() + "/auth/v1/admin/users"))
                    .header("Content-Type", "application/json")
                    .header("apikey", serviceRoleKey)
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ValidacaoException(extrairMensagemErro(response.body(), "Erro ao criar usuário no Supabase Auth."));
            }

            return extrairUserId(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ValidacaoException("Erro ao integrar com Supabase Auth.");
        } catch (IOException ex) {
            throw new ValidacaoException("Erro ao integrar com Supabase Auth.");
        }
    }

    private String criarUsuarioViaSignup(String email, String senha, Map<String, Object> metadata) {
        try {
            String body = objectMapper.writeValueAsString(Map.of(
                    "email", email,
                    "password", senha,
                    "data", metadata
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(normalizarBaseUrl() + "/auth/v1/signup"))
                    .header("Content-Type", "application/json")
                    .header("apikey", publishableKey)
                    .header("Authorization", "Bearer " + publishableKey)
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ValidacaoException(extrairMensagemErro(response.body(), "Erro ao criar usuário no Supabase Auth."));
            }

            return extrairUserId(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new ValidacaoException("Erro ao integrar com Supabase Auth.");
        } catch (IOException ex) {
            throw new ValidacaoException("Erro ao integrar com Supabase Auth.");
        }
    }

    public void deletarUsuario(String authUserId) {
        if (!temServiceRole()) {
            return;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(normalizarBaseUrl() + "/auth/v1/admin/users/" + authUserId))
                    .header("apikey", serviceRoleKey)
                    .header("Authorization", "Bearer " + serviceRoleKey)
                    .DELETE()
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (IOException ex) {
        }
    }

    public String buscarUsuarioIdPorAccessToken(String accessToken) {
        validarConfiguracao();

        if (accessToken == null || accessToken.isBlank()) {
            throw new AutorizacaoException("Token de acesso obrigatorio.");
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(normalizarBaseUrl() + "/auth/v1/user"))
                    .header("apikey", obterChaveApi())
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new AutorizacaoException("Sessao invalida ou expirada.");
            }

            return extrairUserId(response.body());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new AutorizacaoException("Erro ao validar sessao do usuario.");
        } catch (IOException ex) {
            throw new AutorizacaoException("Erro ao validar sessao do usuario.");
        }
    }

    private void validarConfiguracao() {
        if (supabaseUrl == null || supabaseUrl.isBlank()) {
            throw new ValidacaoException("Configuração Supabase URL ausente no backend.");
        }

        if (!temServiceRole() && (publishableKey == null || publishableKey.isBlank())) {
            throw new ValidacaoException("Configuração Supabase key ausente no backend.");
        }
    }

    private boolean temServiceRole() {
        return serviceRoleKey != null && !serviceRoleKey.isBlank();
    }

    private String obterChaveApi() {
        return temServiceRole() ? serviceRoleKey : publishableKey;
    }

    private String normalizarBaseUrl() {
        return supabaseUrl.endsWith("/") ? supabaseUrl.substring(0, supabaseUrl.length() - 1) : supabaseUrl;
    }

    private String extrairMensagemErro(String responseBody, String fallback) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            if (root.hasNonNull("msg")) {
                return root.get("msg").asText();
            }
            if (root.hasNonNull("error_description")) {
                return root.get("error_description").asText();
            }
            if (root.hasNonNull("message")) {
                return root.get("message").asText();
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }

    private String extrairUserId(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);

        JsonNode idNode = root.get("id");
        if (idNode != null && !idNode.asText().isBlank()) {
            return idNode.asText();
        }

        JsonNode userIdNode = root.path("user").get("id");
        if (userIdNode != null && !userIdNode.asText().isBlank()) {
            return userIdNode.asText();
        }

        throw new ValidacaoException("Supabase não retornou id do usuário criado.");
    }
}
