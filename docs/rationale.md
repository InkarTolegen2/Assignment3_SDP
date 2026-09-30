# Design Rationale - Lab Label Printing (Bridge + Adapter)

## 1. Domain and problem
A research lab prints labels for sample tubes and reagent bottles. Two things change independently:
- **What** is printed: specimen labels, reagent labels (later: freezer-box labels, ...).
- **Where/how** it is printed: a thermal ZPL printer, a text file (preview/archive), and an old dot-matrix printer that only ships with a vendor driver.

With plain inheritance this gives `ThermalSpecimenLabel`, `FileSpecimenLabel`, `DotMatrixReagentLabel`, ... - 2 x 3 = 6 classes today, and every new label type or device multiplies the count. This is the "multiple orthogonal dimensions of variation" case from Lecture 4.

## 2. Why Bridge (and not something else)
- `Label` (Abstraction) holds a `LabelPrinter` (Implementor) and delegates. `SpecimenLabel` and `ReagentLabel` (Refined Abstractions) only decide the *content* via `render()`. Printers only know how to put a `RenderedLabel` on some medium.
- Both sides grow independently: `NewLabelVariantTest` adds a `FreezerBoxLabel` without touching any existing class; `PrinterResolverTest` registers a new printer without touching the resolver. Class count grows 2 + 3, not 2 x 3.
- I did not use Strategy: the printer is not just interchangeable behaviour of one class - it is a whole second hierarchy that the abstraction is *designed* to be separated from.
- `Label` never imports a concrete printer; it depends on the `LabelPrinter` interface only, so no implementor details leak.

## 3. Why Adapter is also needed
The dot-matrix vendor driver (`vendor.dotmatrix.DotMatrixDriver`) is third-party code that cannot be changed and does **not** fit `LabelPrinter`:

| Aspect | Our contract (`LabelPrinter`) | Vendor (`DotMatrixDriver`) |
|---|---|---|
| Method | `print(RenderedLabel, int copies)` | `emit(short copyCount, byte[] payload)` |
| Parameters | structured label, `int`, label first | raw ASCII bytes, `short`, copies first |
| Lifecycle | none | `open()` ... `close()` |
| Errors | `PrintFailedException(Reason)` | negative `int` codes + checked `DotMatrixLinkException` |

`DotMatrixPrinterAdapter implements LabelPrinter` and wraps the driver. It:
1. converts `RenderedLabel` to ASCII bytes and `int` to `short`;
2. manages `open()`/`close()` (close is always called, in `finally`);
3. translates every failure: `-11`/`-1` -> `DEVICE_UNAVAILABLE`, `-23` -> `OUT_OF_MEDIA`, `-40` -> `INVALID_LABEL`, other codes and unexpected runtime errors -> `UNKNOWN`, `DotMatrixLinkException` -> `DEVICE_UNAVAILABLE`.

Following Clean Code (error handling / wrapping third-party APIs, Lecture 5), callers handle **one** exception type. The adapter is the only class that knows the vendor's constants and exception; the vendor exception is deliberately not chained as a cause, so it cannot leak to callers.

**Why both patterns together:** Bridge gives the two independent hierarchies; Adapter is what lets one *Concrete Implementor* come from a foreign API. Without Bridge, the label types would be duplicated per printer. Without Adapter, the vendor driver could not be plugged into the Implementor interface without editing vendor code. Adapter is applied first (normalize the interface), Bridge composes the result (Lecture 5 guidance: "normalize once, then compose").

## 4. Module: dynamic selection of the implementor at runtime
`PrinterResolver` picks the `LabelPrinter` from the destination string's URI scheme (`thermal://lab-1`, `file:///...`, `dotmatrix://LPT1`). Concrete printers are registered from the composition root (`DefaultPrinters`), so the resolver depends only on the interface. The client (`Main`) chooses the label type and passes the destination it received as input; it never names a concrete printer or the adapter. Unknown or malformed destinations fail with `UNSUPPORTED_DESTINATION`, i.e. through the same contract. This satisfies Lecture 4's "runtime flexibility: choosing implementations at runtime" and Open/Closed on the implementor side.

## 5. Testing (JUnit 5 + Mockito)
- `SpecimenLabelTest`, `ReagentLabelTest`: each Refined Abstraction delegates the right rendered content and copy count to a mocked `LabelPrinter`; invalid copies never reach the printer; failures propagate unchanged.
- `DotMatrixPrinterAdapterTest`: mocked `DotMatrixDriver`; verifies the exact payload/argument translation, every error-code mapping, that the vendor exception does not leak, and that `close()` is always called.
- `PrinterResolverTest`: scheme-based choice, adding a printer without changing the resolver, consistent failure for bad destinations.

## 6. Limitation
The vendor printer understands plain ASCII only. Labels with Cyrillic/Kazakh letters are rejected for that device with `INVALID_LABEL` instead of being transliterated. The adapter could transliterate, but that would silently change lab data on a printed label, so I chose to fail loudly. Other printers are unaffected.
