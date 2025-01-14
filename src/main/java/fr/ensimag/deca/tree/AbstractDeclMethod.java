package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;


/**
 * Method declaration
 *
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public abstract class AbstractDeclMethod extends Tree {
    
    /**
     * Pass 1 of [SyntaxeContextuelle]. Verify that the method declaration is OK
     * without looking at its content.
     */
    protected abstract void verifyMethod(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the method members are OK.
     */
    protected abstract void verifyMethodMembers(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the method are OK.
     */
    protected abstract void verifyMethodBody(DecacCompiler compiler)
            throws ContextualError;
}