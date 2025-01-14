package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;

import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;

/**
 * Method declaration
 *
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public abstract class AbstractDeclField extends Tree {

    /**
     * Pass 2 of [SyntaxeContextuelle]. Verify that the field members (fields and
     * methods) are OK, without looking at method body and field initialization.
     */
    protected abstract void verifyFieldMembers(DecacCompiler compiler, 
    ClassDefinition nameClass, 
    EnvironmentExp envExp)
            throws ContextualError;

    /**
     * Pass 3 of [SyntaxeContextuelle]. Verify that instructions and expressions
     * contained in the field are OK.
     */
    protected abstract void verifyFieldBody(DecacCompiler compiler, ClassDefinition nameClass)
            throws ContextualError;
}