package lab.label;

import lab.printing.LabelPrinter;
import lab.printing.PrintFailedException;
import lab.printing.PrintReceipt;
import lab.printing.RenderedLabel;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class SpecimenLabelTest {
    private final LabelPrinter printer = mock(LabelPrinter.class);

    @Test
    void delegatesRenderedContentToPrinter() {
        PrintReceipt receipt = new PrintReceipt(2, "mock");
        when(printer.print(any(), anyInt())).thenReturn(receipt);

        Label label = new SpecimenLabel(printer, "S-1", LocalDate.of(2026, 9, 29), "-80C");
        PrintReceipt result = label.print(2);

        RenderedLabel expected = new RenderedLabel("SPECIMEN S-1",
                List.of("Collected: 2026-09-29", "Storage: -80C"), "S-1");
        verify(printer).print(expected, 2);
        assertSame(receipt, result);
    }

    @Test
    void rejectsNonPositiveCopiesWithoutTouchingPrinter() {
        Label label = new SpecimenLabel(printer, "S-1", LocalDate.of(2026, 9, 29), "-80C");
        assertThrows(IllegalArgumentException.class, () -> label.print(0));
        verifyNoInteractions(printer);
    }

    @Test
    void propagatesPrinterFailureUnchanged() {
        PrintFailedException failure =
                new PrintFailedException(PrintFailedException.Reason.OUT_OF_MEDIA, "no paper");
        when(printer.print(any(), anyInt())).thenThrow(failure);

        Label label = new SpecimenLabel(printer, "S-1", LocalDate.of(2026, 9, 29), "-80C");
        assertSame(failure, assertThrows(PrintFailedException.class, label::printOne));
    }
}
