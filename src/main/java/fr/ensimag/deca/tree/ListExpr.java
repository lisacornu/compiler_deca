package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import java.util.Iterator;

/**
 * List of expressions (eg list of parameters).
 *
 * @author gl31
 * @date 01/01/2025
 */
public class ListExpr extends TreeList<AbstractExpr> {


    @Override
    public void decompile(IndentPrintStream s) {

        for (Iterator<AbstractExpr> iterator = getList().iterator(); iterator.hasNext();) {

            AbstractExpr expr = iterator.next();
            expr.decompile(s);
            if (iterator.hasNext()) s.print(",");
        }
    }
}
