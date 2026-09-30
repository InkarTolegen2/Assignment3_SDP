package lab.app;

import lab.label.Label;
import lab.label.ReagentLabel;
import lab.label.SpecimenLabel;
import lab.printing.PrintFailedException;
import lab.printing.PrintReceipt;
import lab.printing.PrinterResolver;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        PrinterResolver resolver = DefaultPrinters.create();
        String tmpFile = Path.of(System.getProperty("java.io.tmpdir"), "labels-demo.txt").toUri().toString();

        List<String> destinations = args.length > 0
                ? List.of(args)
                : List.of("thermal://lab-1", tmpFile, "dotmatrix://LPT1",
                          "dotmatrix://LPT2-NOPAPER", "dotmatrix://LPT3-DEAD", "fax://office");

        for (String destination : destinations) {
            System.out.println("\n=== " + destination + " ===");
            try {
                Label specimen = new SpecimenLabel(resolver.resolve(destination),
                        "S-2026-0042", LocalDate.of(2026, 9, 29), "-80C");
                Label reagent = new ReagentLabel(resolver.resolve(destination),
                        "Ethanol 96%", "LOT-7781", LocalDate.of(2027, 3, 1), "Flammable");
                PrintReceipt r1 = specimen.print(2);
                PrintReceipt r2 = reagent.printOne();
                System.out.println("OK: " + r1 + " / " + r2);
            } catch (PrintFailedException e) {
                System.out.println("FAILED [" + e.getReason() + "]: " + e.getMessage());
            }
        }
    }
}
