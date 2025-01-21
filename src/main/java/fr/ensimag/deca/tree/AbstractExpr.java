package fr.ensimag.deca.tree;
import java.util.HashMap;
import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ClassType;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.DecacInternalError;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;

import java.io.PrintStream;

import fr.ensimag.ima.pseudocode.IMAProgram;
import org.apache.commons.lang.Validate;

/**
 * Expression, i.e. anything that has a value.
 *
 * @author gl31
 * @date 01/01/2025
 */
public abstract class AbstractExpr extends AbstractInst {
    /**
     * @return true if the expression does not correspond to any concrete token
     * in the source code (and should be decompiled to the empty string).
     */
    boolean isImplicit() {
        return false;
    }

    /**
     * Get the type decoration associated to this expression (i.e. the type computed by contextual verification).
     */
    public Type getType() {
        return type;
    }

    protected void setType(Type type) {
        Validate.notNull(type);
        this.type = type;
    }
    private Type type;

    @Override
    protected void checkDecoration() {
        if (getType() == null) {
            throw new DecacInternalError("Expression " + decompile() + " has no Type decoration");
        }
    }

    /**
     * Verify the expression for contextual error.
     * 
     * implements non-terminals "expr" and "lvalue" 
     *    of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  (contains the "env_types" attribute)
     * @param localEnv
     *            Environment in which the expression should be checked
     *            (corresponds to the "env_exp" attribute)
     * @param currentClass
     *            Definition of the class containing the expression
     *            (corresponds to the "class" attribute)
     *             is null in the main bloc.
     * @return the Type of the expression
     *            (corresponds to the "type" attribute)
     */
    public abstract Type verifyExpr(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError;

    /**
     * Verify the expression in right hand-side of (implicit) assignments 
     * 
     * implements non-terminal "rvalue" of [SyntaxeContextuelle] in pass 3
     *
     * @param compiler  contains the "env_types" attribute
     * @param localEnv corresponds to the "env_exp" attribute
     * @param currentClass corresponds to the "class" attribute
     * @param expectedType corresponds to the "type1" attribute            
     * @return this with an additional ConvFloat if needed...
     */
    public AbstractExpr verifyRValue(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        Type type = verifyExpr(compiler, localEnv, currentClass);
        if(type.sameType(expectedType)){
            setType(type);
            return this;
        }else if ((type.isInt() && expectedType.isFloat()) ){ 
            return this;
        }else if (type.isClass()){
            ClassType classType = type.asClassType(null, getLocation());
            ClassType expectedClassType= expectedType.asClassType(null, getLocation());
            if (classType.isSubClassOf(expectedClassType)){
                return this;
            }else{
                throw new ContextualError("The classType "+ classType +" is not a subclassType of " + expectedClassType, getLocation());
            }
        }else{
            throw new ContextualError("They are not compatible (not same type or float->int) " + type + " is not " + expectedType, getLocation());
        }
    }
        public AbstractExpr verifyRValue_opti(DecacCompiler compiler,
            EnvironmentExp localEnv, ClassDefinition currentClass, 
            Type expectedType)
            throws ContextualError {
        Type type = verifyExpr_opti(compiler, localEnv, currentClass);
        if(type.sameType(expectedType)){
            setType(type);
            return this;
        }else if ((type.isInt() && expectedType.isFloat()) ){ 
            return this;
        }else if (type.isClass()){
            ClassType classType = type.asClassType(null, getLocation());
            ClassType expectedClassType= expectedType.asClassType(null, getLocation());
            if (classType.isSubClassOf(expectedClassType)){
                return this;
            }else{
                throw new ContextualError("The classType "+ classType +" is not a subclassType of " + expectedClassType, getLocation());
            }
        }else{
            throw new ContextualError("They are not compatible (not same type or float->int) " + type + " is not " + expectedType, getLocation());
        }
    }
    
    
    @Override
    protected void verifyInst(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
            Type type = verifyExpr(compiler, localEnv, currentClass);
        if (!(type.isBoolean() || type.isFloat() || type.isInt() || type.isString()|| type.isVoid()|| type.isClass())){
            throw new UnsupportedOperationException("This is not inst type");
        }
    }

    protected Type verifyExpr_opti(DecacCompiler compiler, EnvironmentExp localEnv,
        ClassDefinition currentClass) throws ContextualError {
             return verifyExpr(compiler, localEnv, currentClass);
        }

    @Override
    protected void verifyInst_opti(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass, Type returnType)
            throws ContextualError {
            Type type = verifyExpr_opti(compiler, localEnv, currentClass);
        if (!(type.isBoolean() || type.isFloat() || type.isInt() || type.isString()|| type.isVoid()|| type.isClass())){
            throw new UnsupportedOperationException("This is not inst type");
        }
    }


    /**
     * Verify the expression as a condition, i.e. check that the type is
     * boolean.
     *
     * @param localEnv
     *            Environment in which the condition should be checked.
     * @param currentClass
     *            Definition of the class containing the expression, or null in
     *            the main program.
     */
    void verifyCondition(DecacCompiler compiler, EnvironmentExp localEnv,
            ClassDefinition currentClass) throws ContextualError {
            Type type = verifyExpr(compiler, localEnv, currentClass);
            if (!type.isBoolean()){
                throw new ContextualError("This is not boolean",getLocation());
            }
        
    }


    protected boolean printHex = false;
    /**
     * Generate code to print the expression
     *
     * @param compiler
     */
    protected void codeGenPrint(DecacCompiler compiler, boolean printHex) {
        this.printHex = printHex;
        this.printExprValue(compiler);
    }

    public abstract void printExprValue(DecacCompiler compiler);


    @Override
    protected void codeGenInst(DecacCompiler compiler, String methodName) {
        DVal reg = codeGenExpr(compiler);
        compiler.registerHandler.SetFree(reg);
    }


    protected abstract DVal codeGenExpr(DecacCompiler compiler);
    protected DVal codeGenExpr_opti(DecacCompiler compiler){
        return codeGenExpr(compiler);
    }


    @Override
    protected void decompileInst(IndentPrintStream s) {
        decompile(s);
        s.print(";");
    }

    @Override
    protected void prettyPrintType(PrintStream s, String prefix) {
        Type t = getType();
        if (t != null) {
            s.print(prefix);
            s.print("type: ");
            s.print(t);
            s.println();
        }
    }
}
