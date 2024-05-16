package com.priyakdey;

/**
 * @author Priyak Dey
 */
public class DoubleStringLiteralAllocation {

    private static final JVMHeapDiagnosticHelper jvmHeapDiagnosticHelper =
        new JVMHeapDiagnosticHelper(DoubleStringLiteralAllocation.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        jvmHeapDiagnosticHelper.takePreAllocHistDump();
        jvmHeapDiagnosticHelper.takePreAllocHeapDump();

        String s1 = "Hello, World";
        String s2 = "Hello, World";

        jvmHeapDiagnosticHelper.takePostAllocHistDump();
        jvmHeapDiagnosticHelper.takePostAllocHeapDump();
    }

}
