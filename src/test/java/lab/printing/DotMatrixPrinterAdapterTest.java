package lab.printing;

import lab.printing.PrintFailedException.Reason;
import lab.printing.dotmatrix.DotMatrixPrinterAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import vendor.dotmatrix.DotMatrixDriver;
import vendor.dotmatrix.DotMatrixLinkException;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DotMatrixPrinterAdapterTest {
    private final DotMatrixDriver driver = mock(DotMatrixDriver.class);
    private DotMatrixPrinterAdapter adapter;
    private final RenderedLabel label = new RenderedLabel("Title", List.of("Line 1"), "ABC");

    @BeforeEach
    void setUp() {
        adapter = new DotMatrixPrinterAdapter(driver);
    }

    @Test
    void translatesCallAndReturnsReceiptOnSuccess() throws Exception {
        when(driver.emit(anyShort(), any())).thenReturn(DotMatrixDriver.OK);

        PrintReceipt receipt = adapter.print(label, 3);

        ArgumentCaptor<byte[]> payload = ArgumentCaptor.forClass(byte[].class);
        verify(driver).open();
        verify(driver).emit(eq((short) 3), payload.capture());
        verify(driver).close();
        assertEquals("Title\r\nLine 1\r\n*ABC*\r\n", new String(payload.getValue(), StandardCharsets.US_ASCII));
        assertEquals(3, receipt.copiesPrinted());
    }

    @Test
    void noPaperCodeBecomesOutOfMedia() {
        when(driver.emit(anyShort(), any())).thenReturn(DotMatrixDriver.ERR_NO_PAPER);
        assertReason(Reason.OUT_OF_MEDIA);
    }

    @Test
    void portBusyCodeBecomesDeviceUnavailable() {
        when(driver.emit(anyShort(), any())).thenReturn(DotMatrixDriver.ERR_PORT_BUSY);
        assertReason(Reason.DEVICE_UNAVAILABLE);
    }

    @Test
    void badPayloadCodeBecomesInvalidLabel() {
        when(driver.emit(anyShort(), any())).thenReturn(DotMatrixDriver.ERR_BAD_PAYLOAD);
        assertReason(Reason.INVALID_LABEL);
    }

    @Test
    void unknownCodeBecomesUnknown() {
        when(driver.emit(anyShort(), any())).thenReturn(-99);
        assertReason(Reason.UNKNOWN);
    }

    @Test
    void vendorLinkExceptionDoesNotLeak() throws Exception {
        doThrow(new DotMatrixLinkException("port dead")).when(driver).open();

        PrintFailedException e = assertThrows(PrintFailedException.class, () -> adapter.print(label, 1));

        assertEquals(Reason.DEVICE_UNAVAILABLE, e.getReason());
        assertNull(e.getCause(), "vendor exception must not be chained");
        verify(driver, never()).emit(anyShort(), any());
        verify(driver).close();
    }

    @Test
    void unexpectedRuntimeErrorFromDriverBecomesUnknown() {
        when(driver.emit(anyShort(), any())).thenThrow(new IllegalStateException("boom"));
        assertReason(Reason.UNKNOWN);
        verify(driver).close();
    }

    @Test
    void nonAsciiLabelIsRejectedBeforeReachingDriver() {
        RenderedLabel kazakh = new RenderedLabel("Үлгі", List.of("Қатты"), "K1");

        PrintFailedException e = assertThrows(PrintFailedException.class, () -> adapter.print(kazakh, 1));

        assertEquals(Reason.INVALID_LABEL, e.getReason());
        verifyNoInteractions(driver);
    }

    @Test
    void tooManyCopiesIsInvalidLabel() {
        PrintFailedException e = assertThrows(PrintFailedException.class, () -> adapter.print(label, 40_000));
        assertEquals(Reason.INVALID_LABEL, e.getReason());
        verifyNoInteractions(driver);
    }

    private void assertReason(Reason expected) {
        PrintFailedException e = assertThrows(PrintFailedException.class, () -> adapter.print(label, 1));
        assertEquals(expected, e.getReason());
    }
}
