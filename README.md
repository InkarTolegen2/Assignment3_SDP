# Lab Label Printing - Bridge + Adapter (Java 17, Maven)

Assignment 3: Adapter and Bridge patterns.

## Build and test (one command)
```
mvn test
```
Requires JDK 17+ and Maven. Run the demo after `mvn compile`:
```
java -cp target/classes lab.app.Main
java -cp target/classes lab.app.Main thermal://lab-1 dotmatrix://LPT1 dotmatrix://LPT2-NOPAPER
```
Destination suffixes for the simulated dot-matrix port: `-BUSY`, `-NOPAPER`, `-DEAD` trigger different vendor failures.

## Structure
- `lab.label` - Abstraction (`Label`) + Refined Abstractions (`SpecimenLabel`, `ReagentLabel`)
- `lab.printing` - Implementor (`LabelPrinter`), concrete implementors, `PrinterResolver`, `PrintFailedException`
- `lab.printing.dotmatrix` - Adapter (`DotMatrixPrinterAdapter`)
- `vendor.dotmatrix` - simulated third-party driver (adaptee, not modified)
- `lab.app` - composition root and demo
- `docs/uml-class-diagram.(svg|png)`, `docs/rationale.md`
