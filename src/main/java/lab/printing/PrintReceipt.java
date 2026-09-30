package lab.printing;

/** Result of a successful print job. */
public record PrintReceipt(int copiesPrinted, String deviceName) {
}
