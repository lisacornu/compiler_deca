package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;

/**
 * Class declaration.
 *
 * @author gl31
 * @date 01/01/2025
 */
public abstract class AbstractDeclClass extends Tree {
    public abstract void setParentClassAdress (int i);
    /**
     * Pass 1 of [SyntaxeContextuelle]. Verify that the class declaration is OK
     * without looking at its content.
     */
    protected abstract void verifyClass(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the class members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyClassMembers(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the class are OK.
     */
    protected abstract void verifyClassBody(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Code Gen : Generate the code of the virtual method table
     */
    protected abstract int codeGenVTable(DecacCompiler compiler);

    protected abstract void codeGenDeclClass(DecacCompiler compiler);
}
