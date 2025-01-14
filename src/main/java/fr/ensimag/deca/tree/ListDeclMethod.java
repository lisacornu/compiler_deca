package fr.ensimag.deca.tree;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.tools.IndentPrintStream;
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
    public void verifyListMethodMembers(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listMethodMembers: start");
        // throw new UnsupportedOperationException("not yet implemented");
        for (AbstractDeclMethod m : getList()){
            m.verifyMethodMembers(compiler);
        }
        LOG.debug("verify listMethodMembers: end");
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListMethodBody(DecacCompiler compiler) throws ContextualError {
        LOG.debug("verify listMethodBody: start");
        // throw new UnsupportedOperationException("not yet implemented");
        for (AbstractDeclMethod m : getList()){
            m.verifyMethodBody(compiler);
        }
        LOG.debug("verify listMethodBody: end");
    }


}
