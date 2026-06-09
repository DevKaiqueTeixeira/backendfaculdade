package com.cafe.projeto.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ProdutoImagemStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");
    private static final String PUBLIC_PREFIX = "/uploads/produtos/";

    private final Path uploadDir;

    public ProdutoImagemStorageService(@Value("${app.upload.produtos-dir}") String uploadProdutosDir) {
        this.uploadDir = Paths.get(uploadProdutosDir).toAbsolutePath().normalize();
    }

    public String salvar(MultipartFile imagem) {
        validarImagem(imagem);

        try {
            Files.createDirectories(uploadDir);

            String extension = extrairExtensao(imagem.getOriginalFilename());
            String fileName = UUID.randomUUID() + "." + extension;
            Path destino = uploadDir.resolve(fileName);

            try (InputStream inputStream = imagem.getInputStream()) {
                Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            }

            return PUBLIC_PREFIX + fileName;
        } catch (IOException ex) {
            throw new ValidacaoException("Nao foi possivel salvar a imagem do produto.");
        }
    }

    public void excluir(String imagemUrl) {
        if (imagemUrl == null || imagemUrl.isBlank() || !imagemUrl.startsWith(PUBLIC_PREFIX)) {
            return;
        }

        String fileName = imagemUrl.substring(PUBLIC_PREFIX.length());

        if (fileName.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(uploadDir.resolve(fileName));
        } catch (IOException ignored) {
        }
    }

    private void validarImagem(MultipartFile imagem) {
        if (imagem == null || imagem.isEmpty()) {
            throw new ValidacaoException("Imagem do produto e obrigatoria.");
        }

        String contentType = imagem.getContentType();

        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new ValidacaoException("Arquivo da imagem deve ser uma imagem valida.");
        }

        String extension = extrairExtensao(imagem.getOriginalFilename());

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ValidacaoException("Formato de imagem nao suportado. Use jpg, jpeg, png ou webp.");
        }
    }

    private String extrairExtensao(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new ValidacaoException("Arquivo de imagem invalido.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);

        if (extension.isBlank()) {
            throw new ValidacaoException("Arquivo de imagem invalido.");
        }

        return extension;
    }
}
