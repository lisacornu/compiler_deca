package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.context.TypeDefinition;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.instructions.WSTR;
import org.apache.commons.lang.Validate;

/**
 * Selection
 *
 * @author Fabien Galzi
 * @date 15/01/2025
 */
public class Selection extends AbstractLValue {
    private AbstractExpr expr;
    private AbstractIdentifier fieldIdent;

    public Selection(AbstractExpr expr, AbstractIdentifier fieldIdent){
        this.expr = expr;
        this.fieldIdent = fieldIdent;
    }
    
    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass)
            throws ContextualError {
        Type type = expr.verifyExpr(compiler, localEnv, currentClass);
        FieldDefinition fieldDef = (FieldDefinition) currentClass.getMembers().get(fieldIdent.getName()); //cette ligne bizarre pck pas sur de bien avoir le bon type
        if(!type.isClass()){
            throw new ContextualError("The class is not defined", getLocation());
        }
        if (fieldDef.getVisibility()==Visibility.PROTECTED){
            if(!type.sameType(currentClass.getType())){
                throw new ContextualError("Subtype(expr type) is not a subtype of super class(currentClass)", getLocation());
            }
            if(!currentClass.getType().sameType(fieldDef.getContainingClass().getType())){
                throw new ContextualError("Subtype(currentClass) is not a subtype of super class(field Class)", getLocation());
            }
        }
        fieldIdent.setDefinition(fieldDef);
        setType(fieldDef.getType());
        return fieldDef.getType();
    }

    @Override
    public void decompile(IndentPrintStream s) {
        expr.decompile(s);
        s.print(".");
        fieldIdent.decompile(s);
        //TODO
    }

    @Override
    protected void iterChildren(TreeFunction f) {
        expr.iter(f);
        fieldIdent.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expr.prettyPrint(s, prefix, false);
        fieldIdent.prettyPrint(s, prefix, true);

    }

    @Override
    protected DVal codeGenExpr(DecacCompiler compiler) {
        throw new UnsupportedOperationException("not yet implemented");
    }

    @Override
    public void printExprValue(DecacCompiler compiler){
        throw new UnsupportedOperationException("not implemented yet");
    }
}
