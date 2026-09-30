package lab.printing;

import java.util.List;
import java.util.Objects;

/** Printer-independent content of one label. */
public record RenderedLabel(String title, List<String> lines, String barcode) {
    public RenderedLabel {
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(barcode, "barcode");
        if (title.isBlank() || barcode.isBlank()) {
            throw new IllegalArgumentException("title and barcode must not be blank");
        }
        lines = List.copyOf(lines);
    }
}
