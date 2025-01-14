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
public abstract class AbstractDeclParam extends Tree {
    

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the method members are OK.
     */
    protected abstract Type verifyParamMembers(DecacCompiler compiler)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the method are OK.
     */
    protected abstract void verifyParamBody(DecacCompiler compiler, EnvironmentExp localEnv)
            throws ContextualError;
}