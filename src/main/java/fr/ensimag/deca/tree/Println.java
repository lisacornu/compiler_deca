package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.IMAProgram;
import fr.ensimag.ima.pseudocode.instructions.WNL;

/**
 * @author gl31
 * @date 01/01/2025
 */
public class Println extends AbstractPrint {

    /**
     * @param arguments arguments passed to the print(...) statement.
     * @param printHex if true, then float should be displayed as hexadecimal (printlnx)
     */
    public Println(boolean printHex, ListExpr arguments) {
        super(printHex, arguments);
    }

    @Override
    protected void codeGenInst(IMAProgram methodBodyProgram) {
        super.codeGenInst(methodBodyProgram);
        methodBodyProgram.addInstruction(new WNL());
    }

    @Override
    String getSuffix() {
        return "ln";
    }
}
