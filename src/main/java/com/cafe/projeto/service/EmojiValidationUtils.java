package com.cafe.projeto.service;

import java.util.regex.Pattern;

final class EmojiValidationUtils {

    private static final Pattern EMOJI_PATTERN = Pattern.compile("[\\x{1F1E6}-\\x{1F1FF}\\x{1F300}-\\x{1FAFF}\\x{2600}-\\x{27BF}\\uFE0F\\u200D]");

    private EmojiValidationUtils() {
    }

    static void validarSemEmoji(String valor, String campo) {
        if (valor != null && EMOJI_PATTERN.matcher(valor).find()) {
            throw new ValidacaoException("Nao e permitido usar emoji no campo " + campo + ".");
        }
    }
}
