package pl.apex.routing.domain;

import java.util.UUID;

/**
 * Identyfikator autora trasy - referencja do uzytkownika.
 * Docelowo wypelniany przez 'sub' z tokenu Keycloaka (Faza 4). Na razie
 * domena zna tylko samo ID, nie caly profil - granica kontekstu jest cienka.
 */
public record AuthorId(UUID value) {

    public AuthorId {
        if (value == null) {
            throw new IllegalArgumentException("AuthorId nie moze byc null");
        }
    }

    public static AuthorId of(UUID value) {
        return new AuthorId(value);
    }
}
