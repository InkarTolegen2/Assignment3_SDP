package vendor.dotmatrix;

/** Third-party checked exception: thrown when the serial/parallel link cannot be opened. */
public class DotMatrixLinkException extends Exception {
    public DotMatrixLinkException(String message) {
        super(message);
    }
}
