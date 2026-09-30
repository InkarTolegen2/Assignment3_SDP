package vendor.dotmatrix;

import java.nio.charset.StandardCharsets;

public class DotMatrixDriver {
    public static final int OK = 0;
    public static final int ERR_NOT_OPEN = -1;
    public static final int ERR_PORT_BUSY = -11;
    public static final int ERR_NO_PAPER = -23;
    public static final int ERR_BAD_PAYLOAD = -40;
    public static final int MAX_PAYLOAD_BYTES = 512;

    private final String portName;
    private boolean open;

    public DotMatrixDriver(String portName) {
        this.portName = portName;
    }

    public void open() throws DotMatrixLinkException {
        if (portName == null || portName.isBlank() || portName.endsWith("-DEAD")) {
            throw new DotMatrixLinkException("cannot open port '" + portName + "'");
        }
        open = true;
    }

    public int emit(short copyCount, byte[] payload) {
        if (!open) return ERR_NOT_OPEN;
        if (portName.endsWith("-BUSY")) return ERR_PORT_BUSY;
        if (portName.endsWith("-NOPAPER")) return ERR_NO_PAPER;
        if (copyCount <= 0 || payload == null || payload.length > MAX_PAYLOAD_BYTES) return ERR_BAD_PAYLOAD;
        String text = new String(payload, StandardCharsets.US_ASCII).trim().replace("\r\n", " | ");
        System.out.println("[dot-matrix@" + portName + "] x" + copyCount + " -> " + text);
        return OK;
    }

    public void close() {
        open = false;
    }
}
