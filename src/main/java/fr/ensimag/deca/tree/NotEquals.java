package fr.ensimag.deca.tree;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.POP;
import fr.ensimag.ima.pseudocode.instructions.PUSH;
import fr.ensimag.ima.pseudocode.instructions.LOAD;
import fr.ensimag.ima.pseudocode.instructions.CMP;
import fr.ensimag.ima.pseudocode.instructions.BRA;
import fr.ensimag.ima.pseudocode.instructions.BNE;
import fr.ensimag.deca.DecacCompiler;

/**
 *
 * @author gl31
 * @date 01/01/2025
 */
public class NotEquals extends AbstractOpExactCmp {
    static int i = 0;//gerer les label pr ne pas le declarer 2fois
    public NotEquals(AbstractExpr leftOperand, AbstractExpr rightOperand) {
        super(leftOperand, rightOperand);
    }


    @Override
    protected String getOperatorName() {
        return "!=";
    }

    @Override
    protected void codeGenBinaryExpr(DecacCompiler compiler, DVal op1, GPRegister op2) {
        //au final c pas ici qu il faut appeler les operons gauche et droite
       /* this.getLeftOperand().codeGenInst(compiler);
            //pop  dans R0 par exemple
        compiler.addInstruction(new POP(Register.getR(0)));
        this.getRightOperand().codeGenInst(compiler);
            //pop dans R2
        compiler.addInstruction(new POP(Register.getR(2)));
        compiler.addInstruction(new CMP(Register.getR(0),Register.getR(2)));
       // compiler.addInstruction(new BNE(ImmediateInteger(0),))*/
       compiler.addInstruction(new CMP(op1, op2));
        Label case1=new Label ("notequal_"+i);
        Label case2=new Label("equal"+i);
        i++;
        compiler.addInstruction(new BNE(case2));//si la comparaison est fausse sa saute 
        compiler.addInstruction(new LOAD(new ImmediateInteger(0),op2));
        compiler.addInstruction(new BRA(case1));
        compiler.addLabel(case2);
        compiler.addInstruction(new LOAD(new ImmediateInteger(1),op2));
        compiler.addLabel(case1);
       
    }
}
