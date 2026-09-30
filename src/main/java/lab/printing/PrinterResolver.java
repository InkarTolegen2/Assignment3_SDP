package lab.printing;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Chooses the LabelPrinter at runtime from the destination string itself (its URI scheme).
 * Knows only the LabelPrinter interface; concrete printers are registered from outside,
 * so adding a new implementor never requires editing this class (Open/Closed).
 */
public class PrinterResolver {
    private final Map<String, Function<URI, LabelPrinter>> factories = new HashMap<>();

    public PrinterResolver register(String scheme, Function<URI, LabelPrinter> factory) {
        factories.put(scheme.toLowerCase(), factory);
        return this;
    }

    public LabelPrinter resolve(String destination) {
        URI uri;
        try {
            uri = new URI(destination);
        } catch (URISyntaxException | NullPointerException e) {
            throw new PrintFailedException(PrintFailedException.Reason.UNSUPPORTED_DESTINATION,
                    "Malformed printer destination: " + destination);
        }
        Function<URI, LabelPrinter> factory =
                uri.getScheme() == null ? null : factories.get(uri.getScheme().toLowerCase());
        if (factory == null) {
            throw new PrintFailedException(PrintFailedException.Reason.UNSUPPORTED_DESTINATION,
                    "No printer registered for destination: " + destination);
        }
        return factory.apply(uri);
    }
}
