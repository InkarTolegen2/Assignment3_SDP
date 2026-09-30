package lab.label;

import lab.printing.LabelPrinter;
import lab.printing.PrintReceipt;
import lab.printing.RenderedLabel;

import java.util.Objects;

/**
 * ABSTRACTION of the Bridge: "what kind of label is this and what does it say".
 * Depends only on the LabelPrinter interface - never on a concrete or adapted printer.
 */
public abstract class Label {
    private final LabelPrinter printer;

    protected Label(LabelPrinter printer) {
        this.printer = Objects.requireNonNull(printer, "printer");
    }

    /** Refined abstractions decide the content. */
    protected abstract RenderedLabel render();

    public final PrintReceipt print(int copies) {
        if (copies < 1) {
            throw new IllegalArgumentException("copies must be >= 1");
        }
        return printer.print(render(), copies);
    }

    public final PrintReceipt printOne() {
        return print(1);
    }
}
