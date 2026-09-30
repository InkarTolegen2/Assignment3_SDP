package lab.label;

import lab.printing.LabelPrinter;
import lab.printing.PrintReceipt;
import lab.printing.RenderedLabel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ReagentLabelTest {
    private final LabelPrinter printer = mock(LabelPrinter.class);

    @Test
    void delegatesWithHazardLine() {
        PrintReceipt receipt = new PrintReceipt(1, "mock");
        when(printer.print(any(), anyInt())).thenReturn(receipt);

        Label label = new ReagentLabel(printer, "Ethanol", "LOT-1", LocalDate.of(2027, 3, 1), "Flammable");
        PrintReceipt result = label.printOne();

        verify(printer).print(new RenderedLabel("Ethanol",
                List.of("Lot: LOT-1", "Expires: 2027-03-01", "HAZARD: Flammable"), "LOT-1"), 1);
        assertSame(receipt, result);
    }

    @Test
    void omitsHazardLineWhenNoHazard() {
        Label label = new ReagentLabel(printer, "Water", "LOT-2", LocalDate.of(2027, 3, 1), null);
        label.printOne();

        verify(printer).print(new RenderedLabel("Water",
                List.of("Lot: LOT-2", "Expires: 2027-03-01"), "LOT-2"), 1);
    }
}
