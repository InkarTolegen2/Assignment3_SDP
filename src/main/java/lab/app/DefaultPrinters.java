package lab.app;

import lab.printing.FileLabelPrinter;
import lab.printing.PrinterResolver;
import lab.printing.ThermalZplPrinter;
import lab.printing.dotmatrix.DotMatrixPrinterAdapter;
import vendor.dotmatrix.DotMatrixDriver;

import java.nio.file.Path;

/** Composition root: the only place that knows which concrete printers exist. */
public final class DefaultPrinters {
    private DefaultPrinters() {
    }

    public static PrinterResolver create() {
        return new PrinterResolver()
                .register("thermal", uri -> new ThermalZplPrinter(
                        zpl -> System.out.println("[thermal@" + uri.getAuthority() + "]\n" + zpl)))
                .register("file", uri -> new FileLabelPrinter(Path.of(uri)))
                .register("dotmatrix", uri -> new DotMatrixPrinterAdapter(new DotMatrixDriver(uri.getAuthority())));
    }
}
