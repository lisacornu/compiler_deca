package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Equals extends AbstractOpExactCmp {

    public Equals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "==";
    }

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }
}
