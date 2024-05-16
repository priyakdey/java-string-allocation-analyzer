package com.priyakdey;

/**
 * @author Priyak Dey
 */
public class SingleStringLiteralAllocation {

    private static final JVMHeapDiagnosticHelper jvmHeapDiagnosticHelper =
        new JVMHeapDiagnosticHelper(SingleStringLiteralAllocation.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        jvmHeapDiagnosticHelper.takePreAllocHistDump();
        jvmHeapDiagnosticHelper.takePreAllocHeapDump();

        String s1 = "Hello, World";

        jvmHeapDiagnosticHelper.takePostAllocHistDump();
        jvmHeapDiagnosticHelper.takePostAllocHeapDump();

    }

}
