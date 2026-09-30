package lab.printing;

public interface LabelPrinter {
    PrintReceipt print(RenderedLabel label, int copies);
}
