package fr.ensimag.deca.tree;


import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.Label;
import fr.ensimag.ima.pseudocode.instructions.BEQ;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.LOAD;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class And extends AbstractOpBool {

    public And(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }

    @Override
    protected String getOperatorName() {
        return "&&";
    }

    // permet de discerner les labels entre les différents usages de AND au cours du programme
    static int and_cpt = 0;

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {

        // si op2 est nulle, AND est faux
        compiler.addInstruction(new CMP(0, op2));
        compiler.addInstruction(new BEQ(new Label("and_false_case" + and_cpt)));

        // si op1 est nulle (=false), AND est faux
        compiler.addInstruction(new LOAD(op1, GPRegister.R0));
        compiler.addInstruction(new CMP(0, GPRegister.R0));
        compiler.addInstruction(new BEQ(new Label("and_false_case" + and_cpt)));


        // les deux opérandes sont non nulles => AND est vrai
        // étant donné que le résultat est stocké dans op2, il n'y a rien à faire dans ce cas
        compiler.addInstruction(new BRA(new Label("and_end_case" + and_cpt)));

        // sinon, on met op2 à faux, l'opération donne le résultat "faux"
        compiler.addLabel(new Label("and_false_case" + and_cpt));
        compiler.addInstruction(new LOAD(0, op2));

        compiler.addLabel(new Label("and_end_case" + and_cpt));
        and_cpt++;
    }
}
