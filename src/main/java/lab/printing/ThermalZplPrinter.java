package lab.printing;

import java.util.function.Consumer;

public class ThermalZplPrinter implements LabelPrinter {
    private final Consumer<String> sink;

    public ThermalZplPrinter(Consumer<String> sink) {
        this.sink = sink;
    }

    @Override
    public PrintReceipt print(RenderedLabel label, int copies) {
        StringBuilder zpl = new StringBuilder("^XA\n");
        zpl.append("^FO20,20^A0N,30,30^FD").append(clean(label.title())).append("^FS\n");
        int y = 60;
        for (String line : label.lines()) {
            zpl.append("^FO20,").append(y).append("^A0N,22,22^FD").append(clean(line)).append("^FS\n");
            y += 28;
        }
        zpl.append("^FO20,").append(y + 10).append("^BCN,60,Y,N,N^FD").append(clean(label.barcode())).append("^FS\n");
        zpl.append("^PQ").append(copies).append("\n^XZ");
        try {
            sink.accept(zpl.toString());
        } catch (RuntimeException e) {
            throw new PrintFailedException(PrintFailedException.Reason.DEVICE_UNAVAILABLE,
                    "Thermal printer is not reachable: " + e.getMessage(), e);
        }
        return new PrintReceipt(copies, "thermal-zpl");
    }

    private static String clean(String s) {
        return s.replace('^', ' ').replace('~', ' ');
    }
}
