package fr.ensimag.deca.tree;
import java.util.ArrayList;


import fr.ensimag.deca.codegen.RegisterHandler;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.Definition;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.context.FieldDefinition;
import fr.ensimag.deca.context.MethodDefinition;
import fr.ensimag.deca.context.ExpDefinition;
import fr.ensimag.deca.context.VariableDefinition;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.deca.tools.SymbolTable.Symbol;
import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.*;
import fr.ensimag.ima.pseudocode.instructions.*;
import org.apache.commons.lang.Validate;

/**
 * Deca Identifier
 *
 * @author gl31
 * @date 01/01/2025
 */
public class Identifier extends AbstractIdentifier {
    public int indice = 0;
    public IntLiteral literal = null;

    @Override
    protected void checkDecoration() {
        if (getDefinition() == null) {
            throw new DecacInternalError("Identifier " + this.getName() + " has no attached Definition");
        }
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * ClassDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a class definition.
     */
    @Override
    public ClassDefinition getClassDefinition() {
        try {
            return (ClassDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a class identifier, you can't call getClassDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * MethodDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a method definition.
     */
    @Override
    public MethodDefinition getMethodDefinition() {
        try {
            return (MethodDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a method identifier, you can't call getMethodDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * FieldDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public FieldDefinition getFieldDefinition() {
        try {
            return (FieldDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a field identifier, you can't call getFieldDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a
     * VariableDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public VariableDefinition getVariableDefinition() {
        try {
            return (VariableDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a variable identifier, you can't call getVariableDefinition on it");
        }
    }

    /**
     * Like {@link #getDefinition()}, but works only if the definition is a ExpDefinition.
     * 
     * This method essentially performs a cast, but throws an explicit exception
     * when the cast fails.
     * 
     * @throws DecacInternalError
     *             if the definition is not a field definition.
     */
    @Override
    public ExpDefinition getExpDefinition() {
        try {
            return (ExpDefinition) definition;
        } catch (ClassCastException e) {
            throw new DecacInternalError(
                    "Identifier "
                            + getName()
                            + " is not a Exp identifier, you can't call getExpDefinition on it");
        }
    }

    @Override
    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    @Override
    public Symbol getName() {
        return name;
    }

    private Symbol name;

    public Identifier(Symbol name) {
        Validate.notNull(name);
        this.name = name;
    }

    @Override
    public Type verifyExpr(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        if (localEnv.get(getName()) != null){                
            Definition expr = localEnv.get(getName());
            if (expr.isExpression()){
                setDefinition(expr);
                setType(expr.getType());
                return getType();
            }else{
                throw new ContextualError("This is not an expression.", getLocation());
            }
        }else{
            throw new ContextualError("There is no definition for this name : "+getName(), getLocation());
        }
        
    }

    /**
     * Implements non-terminal "type" of [SyntaxeContextuelle] in the 3 passes
     * @param compiler contains "env_types" attribute
     */
    @Override
    public Type verifyType(DecacCompiler compiler) throws ContextualError {
        Definition type = compiler.environmentType.defOfType(getName());
        if (type == null){
            throw new ContextualError("Type is not defined", getLocation());
        }
        if (type.getType().isVoid()){
            throw new ContextualError("It's void type", getLocation());
        }
        setDefinition(type);
        setType(type.getType());
        return getType();
    }

    public Type verifyTypeMethod(DecacCompiler compiler) throws ContextualError{
        Definition type = compiler.environmentType.defOfType(getName());
        if (type == null){
            throw new ContextualError("Type of method is not defined", getLocation());
        }
        setDefinition(type);
        setType(type.getType());
        return getType();
    }

    @Override
    public void printExprValue(DecacCompiler compiler) {
        if (this.getExpDefinition().getNature().equals("variable") || this.getExpDefinition().isParam()) {
            compiler.addInstruction(new LOAD(this.getExpDefinition().getOperand(), GPRegister.R1));
            if (this.getExpDefinition().getType().isFloat())
                compiler.addInstruction(new WFLOAT());
            else if (this.getExpDefinition().getType().isInt()) {
                compiler.addInstruction(new WINT());
            }
        }

        if (getExpDefinition().getNature().equals("field")) {
            DVal exprAddr = codeGenExpr(compiler);
            GPRegister exprReg = RegisterHandler.popIntoRegister(compiler, exprAddr, GPRegister.R1);
            compiler.addInstruction(new LOAD(exprReg, GPRegister.R1));
            if (this.getExpDefinition().getType().isFloat())
                compiler.addInstruction(new WFLOAT());
            else if (this.getExpDefinition().getType().isInt())
                compiler.addInstruction(new WINT());
        }
    }


    private Definition definition;


    @Override
    protected void iterChildren(TreeFunction f) {
        // leaf node => nothing to do
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        // leaf node => nothing to do
    }

    @Override
    public void decompile(IndentPrintStream s) {
        s.print(name.toString());
    }

    @Override
    String prettyPrintNode() {
        return "Identifier (" + getName() + ")";
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Definition d = getDefinition();
        if (d != null) {
            s.print(prefix);
            s.print("definition: ");
            s.print(d);
            s.println();
        }
    }

    //Renvoi un register offset en cas d'assign (si l'identifier est un champ)
    public DAddr codeGenExprAddr(DecacCompiler compiler, GPRegister tempReg) {
        RegisterOffset objectAddress = new RegisterOffset(-2, Register.LB);
        compiler.addInstruction(new LOAD(objectAddress, tempReg));
        RegisterOffset fielHeapAddress = new RegisterOffset(getFieldDefinition().getIndex(), tempReg);
        return fielHeapAddress;
    }

    protected DVal codeGenExpr(DecacCompiler compiler) {
        if (getDefinition().isField()) {
            RegisterOffset objectAddress = new RegisterOffset(-2, Register.LB);
            compiler.addInstruction(new LOAD(objectAddress, GPRegister.R0));
            RegisterOffset fielHeapAddress = new RegisterOffset(getFieldDefinition().getIndex(), GPRegister.R0);
            compiler.addInstruction(new LOAD(fielHeapAddress, GPRegister.R0));
            return RegisterHandler.pushFromRegister(compiler, GPRegister.R0);
        }
        return getExpDefinition().getOperand();
    }
      public void usage(DecacCompiler compiler){
        // Incrémenter l'usage de la variable dans la table de hachage
        String varNameStr = getName().toString();
        compiler.variableUsageCount.put(varNameStr, compiler.variableUsageCount.get(varNameStr) + 1);  // Incrémenter l'usage
        ArrayList<Integer> dynamicInfo = compiler.variableUsageCountdyna.get(varNameStr);
        int i = compiler.variableLast.getOrDefault(varNameStr,0);
        dynamicInfo.set(i, 1);
    }

    
    @Override
    public Type verifyExpr_opti(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
        if (localEnv.get(getName()) != null){                
            Definition expr = localEnv.get(getName());
            if (expr.isExpression()){
                setDefinition(expr);
                setType(expr.getType());
                usage(compiler);
                return getType();
            }else{
                throw new ContextualError("This is not an expression.", getLocation());
            }
        }else{
            throw new ContextualError("There is no definition for this name : "+getName(), getLocation());
        }
        
    }
}
