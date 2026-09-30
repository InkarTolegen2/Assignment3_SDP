package lab.printing;

/**
 * IMPLEMENTOR of the Bridge.
 *
 * Contract: prints {@code copies} (>= 1) copies of the label and returns a receipt.
 * On any failure it throws {@link PrintFailedException} and nothing else.
 */
public interface LabelPrinter {
    PrintReceipt print(RenderedLabel label, int copies);
}
