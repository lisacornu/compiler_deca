package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.ima.pseudocode.DVal;
/**
 * Left-hand side value of an assignment.
 * 
 * @author gl31
 * @date 01/01/2025
 */
public abstract class AbstractLValue extends AbstractExpr {
    protected DVal registre_ssa;
    public void setRegistre_ssa(DVal r){
        registre_ssa=r;
    }
}
