package lab.label;

import lab.printing.LabelPrinter;
import lab.printing.PrintReceipt;
import lab.printing.RenderedLabel;

import java.util.Objects;

public abstract class Label {
    private final LabelPrinter printer;

    protected Label(LabelPrinter printer) {
        this.printer = Objects.requireNonNull(printer, "printer");
    }

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
