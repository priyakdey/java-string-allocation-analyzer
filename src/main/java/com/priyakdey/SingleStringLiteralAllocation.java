package com.priyakdey;

/**
 * @author Priyak Dey
 */
public class SingleStringLiteralAllocation {

    public static void main(String[] args) {
        JVMHeapDiagnosticHelper jvmHeapDiagnosticHelper =
            new JVMHeapDiagnosticHelper(SingleStringLiteralAllocation.class.getSimpleName());

        jvmHeapDiagnosticHelper.takePreAllocHistDump();
        jvmHeapDiagnosticHelper.takePreAllocHeapDump();

        String s = "Hello, World";

        jvmHeapDiagnosticHelper.takePostAllocHistDump();
        jvmHeapDiagnosticHelper.takePostAllocHeapDump();

        // sync to make sure compiler does not optimize unused declaration
        System.out.println(s);
    }

}
