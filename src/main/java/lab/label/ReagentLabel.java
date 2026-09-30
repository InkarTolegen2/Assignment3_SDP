package lab.label;

import lab.printing.LabelPrinter;
import lab.printing.RenderedLabel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Refined abstraction #2: label for a reagent bottle; adds a hazard line when needed. */
public class ReagentLabel extends Label {
    private final String name;
    private final String lotNumber;
    private final LocalDate expiresOn;
    private final String hazardNote; // may be null

    public ReagentLabel(LabelPrinter printer, String name, String lotNumber, LocalDate expiresOn, String hazardNote) {
        super(printer);
        this.name = name;
        this.lotNumber = lotNumber;
        this.expiresOn = expiresOn;
        this.hazardNote = hazardNote;
    }

    @Override
    protected RenderedLabel render() {
        List<String> lines = new ArrayList<>();
        lines.add("Lot: " + lotNumber);
        lines.add("Expires: " + expiresOn);
        if (hazardNote != null && !hazardNote.isBlank()) {
            lines.add("HAZARD: " + hazardNote);
        }
        return new RenderedLabel(name, lines, lotNumber);
    }
}
