package lab.label;

import lab.printing.LabelPrinter;
import lab.printing.RenderedLabel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NewLabelVariantTest {

    static class FreezerBoxLabel extends Label {
        FreezerBoxLabel(LabelPrinter printer) {
            super(printer);
        }

        @Override
        protected RenderedLabel render() {
            return new RenderedLabel("FREEZER BOX", List.of("Shelf 3"), "BOX-3");
        }
    }

    @Test
    void newRefinedAbstractionWorksWithExistingImplementor() {
        LabelPrinter printer = mock(LabelPrinter.class);
        new FreezerBoxLabel(printer).printOne();
        verify(printer).print(new RenderedLabel("FREEZER BOX", List.of("Shelf 3"), "BOX-3"), 1);
    }
}
