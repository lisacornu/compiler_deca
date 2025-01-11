package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Or extends AbstractOpBool {

    public Or(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "||";
    }

    // permet de discerner les labels entre les différents usages de OR au cours du programme
    static int or_cpt = 0;

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        // CMP ne travaille qu'avec des registres
        compiler.addInstruction(new LOAD(op1, GPRegister.R0));

        // si op1 est vrai
        compiler.addInstruction(new CMP(0, GPRegister.R0));
        compiler.addInstruction(new BNE(new Label("or_true_case" + or_cpt)));

        // sinon si op2 est vrai
        compiler.addInstruction(new CMP(0, op2));
        compiler.addInstruction(new BNE(new Label("or_true_case" + or_cpt)));

        // sinon le resultat est faux, pas besoin de LOAD faux dans op2 car op2 est déjà faux, on va a la fin
        compiler.addInstruction(new BRA(new Label("or_end_case" + or_cpt)));

        // si on arrive ici, alors OR vaut vrai
        compiler.addLabel(new Label("or_true_case" + or_cpt));
        compiler.addInstruction(new LOAD(1, op2));

        compiler.addLabel(new Label("or_end_case" + or_cpt));

        or_cpt++;
    }
}
