package lab.printing.dotmatrix;

import lab.printing.LabelPrinter;
import lab.printing.PrintFailedException;
import lab.printing.PrintFailedException.Reason;
import lab.printing.PrintReceipt;
import lab.printing.RenderedLabel;
import vendor.dotmatrix.DotMatrixDriver;
import vendor.dotmatrix.DotMatrixLinkException;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;


public class DotMatrixPrinterAdapter implements LabelPrinter {
    private static final String DEVICE_NAME = "dot-matrix";
    private final DotMatrixDriver driver;

    public DotMatrixPrinterAdapter(DotMatrixDriver driver) {
        this.driver = driver;
    }

    @Override
    public PrintReceipt print(RenderedLabel label, int copies) {
        if (copies > Short.MAX_VALUE) {
            throw new PrintFailedException(Reason.INVALID_LABEL, "Too many copies for this printer: " + copies);
        }
        byte[] payload = toAscii(label);

        int code;
        try {
            driver.open();
            code = driver.emit((short) copies, payload);
        } catch (DotMatrixLinkException e) {
            throw new PrintFailedException(Reason.DEVICE_UNAVAILABLE,
                    "Dot-matrix printer link failed: " + e.getMessage());
        } catch (RuntimeException e) {
            throw new PrintFailedException(Reason.UNKNOWN,
                    "Dot-matrix printer failed unexpectedly: " + e.getMessage());
        } finally {
            driver.close();
        }

        if (code != DotMatrixDriver.OK) {
            throw translate(code);
        }
        return new PrintReceipt(copies, DEVICE_NAME);
    }

    private static PrintFailedException translate(int code) {
        return switch (code) {
            case DotMatrixDriver.ERR_PORT_BUSY, DotMatrixDriver.ERR_NOT_OPEN ->
                    new PrintFailedException(Reason.DEVICE_UNAVAILABLE, "Dot-matrix printer is busy or not ready");
            case DotMatrixDriver.ERR_NO_PAPER ->
                    new PrintFailedException(Reason.OUT_OF_MEDIA, "Dot-matrix printer is out of paper");
            case DotMatrixDriver.ERR_BAD_PAYLOAD ->
                    new PrintFailedException(Reason.INVALID_LABEL, "Dot-matrix printer rejected the label (too long?)");
            default ->
                    new PrintFailedException(Reason.UNKNOWN, "Dot-matrix printer reported an unknown error");
        };
    }

    private static byte[] toAscii(RenderedLabel label) {
        StringBuilder text = new StringBuilder(label.title()).append("\r\n");
        for (String line : label.lines()) {
            text.append(line).append("\r\n");
        }
        text.append('*').append(label.barcode()).append("*\r\n");

        CharsetEncoder encoder = StandardCharsets.US_ASCII.newEncoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            ByteBuffer buf = encoder.encode(java.nio.CharBuffer.wrap(text));
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            return bytes;
        } catch (CharacterCodingException e) {
            throw new PrintFailedException(Reason.INVALID_LABEL,
                    "Label contains characters this printer cannot print (ASCII only)");
        }
    }
}
