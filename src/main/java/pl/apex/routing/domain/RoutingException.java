package pl.apex.routing.domain;

/**
 * Baza wyjatkow domenowych kontekstu Route. Grupuje bledy domeny, dzieki czemu
 * warstwa web mapuje je swiadomie, nie lapiac przypadkiem wyjatkow frameworka.
 */
public abstract class RoutingException extends RuntimeException {

    protected RoutingException(String message) {
        super(message);
    }
}
