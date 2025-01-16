package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;

import java.lang.instrument.ClassDefinition;

import org.apache.log4j.Logger;

/**
 *
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public class ListDeclField extends TreeList<AbstractDeclField> {
    private static final Logger LOG = Logger.getLogger(ListDeclField.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclField f : getList()) {
            f.decompile(s);
            s.println();
        }
    }


    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListFieldMembers(DecacCompiler compiler, fr.ensimag.deca.context.ClassDefinition nameClass) throws ContextualError {
        LOG.debug("verify listFieldMembers: start");
        // throw new UnsupportedOperationException("not yet implemented");
        int i = 0;
        for (AbstractDeclField f : getList()){
            f.verifyFieldMembers(compiler, nameClass, nameClass.getMembers(), i++);
        }
        LOG.debug("verify listFieldMembers: end");
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListFieldBody(DecacCompiler compiler, fr.ensimag.deca.context.ClassDefinition nameClass) throws ContextualError {
        LOG.debug("verify listFieldBody: start");
        // throw new UnsupportedOperationException("not yet implemented");
        for (AbstractDeclField f : getList()){
            f.verifyFieldBody(compiler, nameClass);
        }
        LOG.debug("verify listFieldBody: end");
    }

    protected void codeGenDeclField(DecacCompiler compiler, int superOffset) {
        for (AbstractDeclField abstractDeclField : getList()) {
            abstractDeclField.codeGenDeclField(compiler, superOffset);
        }
    }


}
