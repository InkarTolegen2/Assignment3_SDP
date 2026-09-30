package lab.label;

import lab.printing.LabelPrinter;
import lab.printing.RenderedLabel;

import java.time.LocalDate;
import java.util.List;

/** Refined abstraction #1: label for a biological sample tube. */
public class SpecimenLabel extends Label {
    private final String sampleId;
    private final LocalDate collectedOn;
    private final String storage;

    public SpecimenLabel(LabelPrinter printer, String sampleId, LocalDate collectedOn, String storage) {
        super(printer);
        this.sampleId = sampleId;
        this.collectedOn = collectedOn;
        this.storage = storage;
    }

    @Override
    protected RenderedLabel render() {
        return new RenderedLabel(
                "SPECIMEN " + sampleId,
                List.of("Collected: " + collectedOn, "Storage: " + storage),
                sampleId);
    }
}
