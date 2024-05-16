package com.priyakdey;

/**
 * @author Priyak Dey
 */
public class SingleStringObjectAllocation {

    private static final JVMHeapDiagnosticHelper jvmHeapDiagnosticHelper =
        new JVMHeapDiagnosticHelper(SingleStringObjectAllocation.class.getSimpleName());

    public static void main(String[] args) throws Exception {
        jvmHeapDiagnosticHelper.takePreAllocHistDump();
        jvmHeapDiagnosticHelper.takePreAllocHeapDump();

        String s1 = new String("Hello, World");

        jvmHeapDiagnosticHelper.takePostAllocHistDump();
        jvmHeapDiagnosticHelper.takePostAllocHeapDump();
    }

}
