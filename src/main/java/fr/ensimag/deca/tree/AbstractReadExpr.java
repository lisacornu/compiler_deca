package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;

/**
 * read...() statement.
 *
 * @author gl31
 * @date 01/01/2025
 */
public abstract class AbstractReadExpr extends AbstractExpr {

    public AbstractReadExpr() {
        super();
    }

    @Override
    public String getExprValue(DecacCompiler compiler) {
        return "temp";
    }

}
