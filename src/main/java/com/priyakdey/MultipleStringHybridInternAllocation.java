package com.priyakdey;

/**
 * @author Priyak Dey
 */
public class MultipleStringHybridInternAllocation {

    private static final JVMHeapDiagnosticHelper jvmHeapDiagnosticHelper =
        new JVMHeapDiagnosticHelper(MultipleStringHybridInternAllocation.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        jvmHeapDiagnosticHelper.takePreAllocHistDump();
        jvmHeapDiagnosticHelper.takePreAllocHeapDump();

        String s1 = "Hello, World";
        String s2 = new String("Hello, World").intern();

        jvmHeapDiagnosticHelper.takePostAllocHistDump();
        jvmHeapDiagnosticHelper.takePostAllocHeapDump();
    }

}
