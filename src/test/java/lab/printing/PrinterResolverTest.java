package lab.printing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PrinterResolverTest {

    @Test
    void choosesImplementorFromDestinationScheme() {
        LabelPrinter a = mock(LabelPrinter.class);
        LabelPrinter b = mock(LabelPrinter.class);
        PrinterResolver resolver = new PrinterResolver()
                .register("aaa", uri -> a)
                .register("bbb", uri -> b);

        assertSame(a, resolver.resolve("aaa://x"));
        assertSame(b, resolver.resolve("BBB://y"));
    }

    @Test
    void newImplementorCanBeAddedWithoutChangingResolver() {
        LabelPrinter custom = mock(LabelPrinter.class);
        PrinterResolver resolver = new PrinterResolver();
        assertThrows(PrintFailedException.class, () -> resolver.resolve("custom://z"));

        resolver.register("custom", uri -> custom);

        assertSame(custom, resolver.resolve("custom://z"));
    }

    @Test
    void unknownOrMalformedDestinationFailsConsistently() {
        PrinterResolver resolver = new PrinterResolver();
        for (String bad : new String[]{"fax://office", "no scheme", "::::"}) {
            PrintFailedException e = assertThrows(PrintFailedException.class, () -> resolver.resolve(bad));
            assertEquals(PrintFailedException.Reason.UNSUPPORTED_DESTINATION, e.getReason());
        }
    }
}
