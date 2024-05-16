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

        // pre-allocate everything for next steps.
        // nothing should be allocated post string instantiation, for easy analysis
        // of the dump.

        jvmHeapDiagnosticHelper.takePostAllocHistDump();
        jvmHeapDiagnosticHelper.takePostAllocHeapDump();
    }

}
