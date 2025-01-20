package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;

import fr.ensimag.deca.context.ClassDefinition;

import org.apache.log4j.Logger;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class ListDeclMethod extends TreeList<AbstractDeclMethod> {
    private static final Logger LOG = Logger.getLogger(ListDeclMethod.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclMethod m : getList()) {
            m.decompile(s);
            s.println();
        }
    }


    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public void verifyListMethodMembers(DecacCompiler compiler, ClassDefinition nameClass) throws ContextualError { 
        LOG.debug("verify listMethodMembers: start");
        for (AbstractDeclMethod m : getList()){
            m.verifyMethodMembers(compiler, nameClass);
        }
        LOG.debug("verify listMethodMembers: end");
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListMethodBody(DecacCompiler compiler, ClassDefinition nameClass) throws ContextualError {
        LOG.debug("verify listMethodBody: start");
        for (AbstractDeclMethod m : getList()){
            m.verifyMethodBody(compiler, nameClass);
        }
        LOG.debug("verify listMethodBody: end");
    }


}
