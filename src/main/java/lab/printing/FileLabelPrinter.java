package lab.printing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/** Concrete implementor #2: appends a plain-text label sheet to a file (handy for previews and archives). */
public class FileLabelPrinter implements LabelPrinter {
    private final Path file;

    public FileLabelPrinter(Path file) {
        this.file = file;
    }

    @Override
    public PrintReceipt print(RenderedLabel label, int copies) {
        StringBuilder block = new StringBuilder();
        for (int i = 0; i < copies; i++) {
            block.append("+----------------------------+\n");
            block.append("| ").append(label.title()).append('\n');
            for (String line : label.lines()) {
                block.append("| ").append(line).append('\n');
            }
            block.append("| [barcode] ").append(label.barcode()).append('\n');
            block.append("+----------------------------+\n");
        }
        try {
            Files.writeString(file, block.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new PrintFailedException(PrintFailedException.Reason.DEVICE_UNAVAILABLE,
                    "Cannot write label file " + file + ": " + e.getMessage(), e);
        }
        return new PrintReceipt(copies, "file:" + file.getFileName());
    }
}
