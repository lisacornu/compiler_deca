package fr.ensimag.deca.tree;
import fr.ensimag.deca.context.Type;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.context.Signature;

import java.lang.instrument.ClassDefinition;

import org.apache.log4j.Logger;

/**
 *
 * @author Fabien Galzi
 * @date 14/01/2025
 */
public class ListDeclParam extends TreeList<AbstractDeclParam> {
    private static final Logger LOG = Logger.getLogger(ListDeclParam.class);
    
    @Override
    public void decompile(IndentPrintStream s) {
        for (AbstractDeclParam p : getList()) {
            p.decompile(s);
            s.println();
        }
    }


    /**
     * Pass 2 of [SyntaxeContextuelle]
     */
    public fr.ensimag.deca.context.Signature verifyListParamMembers(DecacCompiler compiler, fr.ensimag.deca.context.ClassDefinition nameClass) throws ContextualError {
        LOG.debug("verify listParamMembers: start");
        // throw new UnsupportedOperationException("not yet implemented");
        Signature sign = new fr.ensimag.deca.context.Signature();
        for (AbstractDeclParam p : getList()){
            Type typeSign = p.verifyParamMembers(compiler);
            sign.add(typeSign);
        }
        LOG.debug("verify listParamMembers: end");
        return sign;
    }
    
    /**
     * Pass 3 of [SyntaxeContextuelle]
     */
    public void verifyListParamBody(DecacCompiler compiler, EnvironmentExp localEnv) throws ContextualError {
        LOG.debug("verify listFieldBody: start");
        // throw new UnsupportedOperationException("not yet implemented");
        for (AbstractDeclParam p : getList()){
            p.verifyParamBody(compiler, localEnv);
        }
        LOG.debug("verify listFieldBody: end");
    }


}
