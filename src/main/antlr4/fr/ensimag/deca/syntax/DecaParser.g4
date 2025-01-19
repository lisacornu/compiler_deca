parser grammar DecaParser;

options {
    // Default language but name it anyway
    //
    language  = Java;

    // Use a superclass to implement all helper
    // methods, instance variables and overrides
    // of ANTLR default methods, such as error
    // handling.
    //
    superClass = AbstractDecaParser;

    // Use the vocabulary generated by the accompanying
    // lexer. Maven knows how to work out the relationship
    // between the lexer and parser and will build the
    // lexer before the parser. It will also rebuild the
    // parser if the lexer changes.
    //
    tokenVocab = DecaLexer;

}

// which packages should be imported?
@header {
    import fr.ensimag.deca.tree.*;
    import java.io.PrintStream;

    import fr.ensimag.deca.tools.SymbolTable;
    import fr.ensimag.deca.tools.SymbolTable.Symbol;
}

@members {
    @Override
    protected AbstractProgram parseProgram() {
        return prog().tree;
    }
}

prog returns[AbstractProgram tree]
    : list_classes main EOF {
            assert($list_classes.tree != null);
            assert($main.tree != null);
            $tree = new Program($list_classes.tree, $main.tree);
            setLocation($tree, $list_classes.start);
        }
    ;

main returns[AbstractMain tree]
    : /* epsilon */ {
            $tree = new EmptyMain();
        }
    | block {
            assert($block.decls != null);
            assert($block.insts != null);
            $tree = new Main($block.decls, $block.insts);
            setLocation($tree, $block.start);
        }
    ;

block returns[ListDeclVar decls, ListInst insts]
    : OBRACE list_decl list_inst CBRACE {
            assert($list_decl.tree != null);
            assert($list_inst.tree != null);
            $decls = $list_decl.tree;
            $insts = $list_inst.tree;
        }
    ;

list_decl returns[ListDeclVar tree]
@init   {
            $tree = new ListDeclVar();
        }
    : decl_var_set[$tree]*
    ;

decl_var_set[ListDeclVar l]
    : type list_decl_var[$l,$type.tree] SEMI
    ;

list_decl_var[ListDeclVar l, AbstractIdentifier t]
    : dv1=decl_var[$t] {
        $l.add($dv1.tree);
        } (COMMA dv2=decl_var[$t] {
            $l.add($dv2.tree);
        }
      )*
    ;

decl_var[AbstractIdentifier t] returns[AbstractDeclVar tree]
@init   {
        AbstractInitialization absInit = new NoInitialization();    //au cas ou il n'y a pas d'initialisation
        }
    : i=ident {
        assert($i.tree != null);
        AbstractIdentifier name = $i.tree;  //défini ici pas dans @init car il y a forcement un ident
        }
      (EQUALS e=expr {
        absInit = new Initialization($e.tree);  //si il y a une initialisation
        setLocation(absInit, $EQUALS);
        }
      )? {
        $tree = new DeclVar($t, name, absInit);
        setLocation($tree, $i.start);
        }
    ;

list_inst returns[ListInst tree]
@init {
    $tree = new ListInst();
}
    : (inst {
        $tree.add($inst.tree);
        setLocation($tree, $inst.start);
        }
      )*
    ;

inst returns[AbstractInst tree]
    : e1=expr SEMI {
            assert($e1.tree != null);
            $tree = $e1.tree;
        }
    | SEMI {
            $tree = new NoOperation(); //On ne fais rien
            setLocation($tree, $SEMI);
        }
    | PRINT OPARENT list_expr CPARENT SEMI {
            assert($list_expr.tree != null);
            $tree = new Print(false, $list_expr.tree);
            setLocation($tree, $PRINT);
        }
    | PRINTLN OPARENT list_expr CPARENT SEMI {
            assert($list_expr.tree != null);
            $tree = new Println(false, $list_expr.tree);
            setLocation($tree, $PRINTLN);

        }
    | PRINTX OPARENT list_expr CPARENT SEMI {
            assert($list_expr.tree != null);
            $tree = new Print(true, $list_expr.tree);
            setLocation($tree, $PRINTX);
        }
    | PRINTLNX OPARENT list_expr CPARENT SEMI {
            assert($list_expr.tree != null);
            $tree = new Println(true, $list_expr.tree);
            setLocation($tree, $PRINTLNX);
        }
    | if_then_else {
            assert($if_then_else.tree != null);
            $tree = $if_then_else.tree;
        }
    | WHILE OPARENT condition=expr CPARENT OBRACE body=list_inst CBRACE {
            assert($condition.tree != null);
            assert($body.tree != null);
            $tree = new While($condition.tree, $body.tree);
            setLocation($tree, $WHILE);
        }
    | RETURN expr SEMI {
            assert($expr.tree != null);
            $tree = new Return($expr.tree);
            setLocation($tree, $RETURN);
        }
    ;

if_then_else returns[IfThenElse tree]
@init {
    IfThenElse lastBranch = null;   //On retient quel est la dernière branche if/elsif pour lui ajouter une branche else
}
    : if1=IF OPARENT condition=expr CPARENT OBRACE li_if=list_inst CBRACE {
        $tree = new IfThenElse($condition.tree, $li_if.tree, new ListInst()); //if sans else
        setLocation($tree, $if1);
        lastBranch = $tree;
        }
      (ELSE elsif=IF OPARENT elsif_cond=expr CPARENT OBRACE elsif_li=list_inst CBRACE {
        IfThenElse elif = new IfThenElse($elsif_cond.tree, $elsif_li.tree, new ListInst());
        lastBranch.setElseBranch(elif);
        setLocation(elif, $elsif);
        lastBranch = elif;  //deviens la dernière branche
        }
      )*
      (ELSE OBRACE li_else=list_inst CBRACE {
        lastBranch.setElseBranch($li_else.tree);    //Devient la branche else du dernier if/elsif
        }
      )?;

list_expr returns[ListExpr tree]
@init   {
    $tree = new ListExpr();
        }
    : (e1=expr {
        $tree.add($e1.tree);
        }
       (COMMA e2=expr {
       $tree.add($e2.tree);
        }
       )* )?
    ;

expr returns[AbstractExpr tree]
    : assign_expr {
            assert($assign_expr.tree != null);
            $tree = $assign_expr.tree;
        }
    ;

assign_expr returns[AbstractExpr tree]
    : e=or_expr (

        /* condition: expression e must be a "LVALUE" */ {
            if (! ($e.tree instanceof AbstractLValue)) {
                throw new InvalidLValue(this, $ctx);
            }

        }
        EQUALS e2=assign_expr {
            assert($e.tree != null);
            assert($e2.tree != null);
            $tree = new Assign((AbstractLValue)$e.tree, $e2.tree);  //on cast car AbstractLValue est une sous classe de AbstractExpr
            setLocation($tree, $EQUALS);
        }
      | /* epsilon */ {
            assert($e.tree != null);
            $tree = $e.tree;
        }
      )
    ;

or_expr returns[AbstractExpr tree]
    : e=and_expr {
            assert($e.tree != null);
            $tree = $e.tree;
        }
    | e1=or_expr OR e2=and_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Or($e1.tree, $e2.tree);
            setLocation($tree, $OR);
       }
    ;

and_expr returns[AbstractExpr tree]
    : e=eq_neq_expr {
            assert($e.tree != null);
            $tree = $e.tree;
        }
    |  e1=and_expr AND e2=eq_neq_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new And($e1.tree, $e2.tree);
            setLocation($tree, $AND);
        }
    ;

eq_neq_expr returns[AbstractExpr tree]
    : e=inequality_expr {
            assert($e.tree != null);
            $tree = $e.tree;
        }
    | e1=eq_neq_expr EQEQ e2=inequality_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Equals($e1.tree, $e2.tree);
            setLocation($tree, $EQEQ);
        }
    | e1=eq_neq_expr NEQ e2=inequality_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new NotEquals($e1.tree, $e2.tree);
            setLocation($tree, $NEQ);
        }
    ;

inequality_expr returns[AbstractExpr tree]
    : e=sum_expr {
            assert($e.tree != null);
            $tree = $e.tree;
        }
    | e1=inequality_expr LEQ e2=sum_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new LowerOrEqual($e1.tree, $e2.tree);
            setLocation($tree, $LEQ);
        }
    | e1=inequality_expr GEQ e2=sum_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new GreaterOrEqual($e1.tree, $e2.tree);
            setLocation($tree, $GEQ);
        }
    | e1=inequality_expr GT e2=sum_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Greater($e1.tree, $e2.tree);
            setLocation($tree, $GT);
        }
    | e1=inequality_expr LT e2=sum_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Lower($e1.tree, $e2.tree);
            setLocation($tree, $LT);
        }
    | e1=inequality_expr INSTANCEOF type {
            assert($e1.tree != null);
            assert($type.tree != null);
            $tree = new InstanceOf($e1.tree, $type.tree);
            setLocation($tree, $e1.start);
        }
    ;


sum_expr returns[AbstractExpr tree]
    : e=mult_expr {
            assert($e.tree != null);
            $tree = $e.tree;
        }
    | e1=sum_expr PLUS e2=mult_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Plus($e1.tree, $e2.tree);
            setLocation($tree, $PLUS);
        }
    | e1=sum_expr MINUS e2=mult_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Minus($e1.tree, $e2.tree);
            setLocation($tree, $MINUS);
        }
    ;

mult_expr returns[AbstractExpr tree]
    : e=unary_expr {
            assert($e.tree != null);
            $tree = $unary_expr.tree;
        }
    | e1=mult_expr TIMES e2=unary_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Multiply($e1.tree, $e2.tree);
            setLocation($tree, $TIMES);
        }
    | e1=mult_expr SLASH e2=unary_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Divide($e1.tree, $e2.tree);
            setLocation($tree, $SLASH);
        }
    | e1=mult_expr PERCENT e2=unary_expr {
            assert($e1.tree != null);
            assert($e2.tree != null);
            $tree = new Modulo($e1.tree, $e2.tree);
            setLocation($tree, $PERCENT);
        }
    ;

unary_expr returns[AbstractExpr tree]
    : op=MINUS e=unary_expr {
            assert($e.tree != null);
            $tree = new UnaryMinus($e.tree);
            setLocation($tree, $op);
        }
    | op=EXCLAM e=unary_expr {
            assert($e.tree != null);
            $tree = new Not($e.tree);
            setLocation($tree, $op);
        }
    | select_expr {
            assert($select_expr.tree != null);
            $tree = $select_expr.tree;
        }
    ;

select_expr returns[AbstractExpr tree]
    : e=primary_expr {
            assert($e.tree != null);
            $tree = $e.tree;
            setLocation($tree, $e.start);
        }
    | e1=select_expr DOT i=ident { //selection de champ
            assert($e1.tree != null);
            assert($i.tree != null);
            $tree = new Selection($e1.tree, $i.tree);
            setLocation($tree, $e1.start);
        }
        (o=OPARENT args=list_expr CPARENT {
            // we matched "e1.i(args)"
            assert($args.tree != null);
            $tree = new MethodCall($e1.tree, $i.tree, $args.tree);
            setLocation($tree, $e1.start);
        }
        | /* epsilon */ {
            // we matched "e.i"
        }
        )
    ;

primary_expr returns[AbstractExpr tree]
    //TODO gérer les identificateurs
    : ident {
            assert($ident.tree != null);
            $tree = $ident.tree;
        }
    | m=ident OPARENT args=list_expr CPARENT { //appel de méthode
            assert($args.tree != null);
            assert($m.tree != null);
            $tree = new MethodCall($tree, $m.tree, $args.tree);
            setLocation($tree, $m.start);
        }
    | OPARENT expr CPARENT {
            assert($expr.tree != null);
            $tree = $expr.tree;
        }
    | READINT OPARENT CPARENT {
            $tree = new ReadInt();
            setLocation($tree, $READINT);
        }
    | READFLOAT OPARENT CPARENT {
            $tree = new ReadFloat();
            setLocation($tree, $READFLOAT);
        }
    | NEW ident OPARENT CPARENT {
            assert($ident.tree != null);
            $tree = new New($ident.tree);
            setLocation($tree, $NEW);
        }
    | cast=OPARENT type CPARENT OPARENT expr CPARENT {
            assert($type.tree != null);
            assert($expr.tree != null);
            $tree = new Cast($expr.tree, $type.tree);
            setLocation($tree, $cast);
        }
    | literal {
            assert($literal.tree != null);
            $tree = $literal.tree;
        }
    ;

type returns[AbstractIdentifier tree]
    : ident {
            assert($ident.tree != null);
            $tree = $ident.tree;
        }
    ;

literal returns[AbstractExpr tree]
    : INT {
        try {
            $tree = new IntLiteral(Integer.parseInt($INT.text));
            setLocation($tree, $INT);
        }
        catch (NumberFormatException nfe) {
            throw new NumberOverflow(this, $ctx);
        }
        }
    | FLOAT {
        try {
            $tree = new FloatLiteral(Float.parseFloat($FLOAT.text));
            setLocation($tree, $FLOAT);
        }
        catch (NumberFormatException nfe) {
            throw new NumberOverflow(this, $ctx);
        }
        }
    | STRING {
        $tree = new StringLiteral($STRING.text);
        setLocation($tree, $STRING);
        }
    | TRUE {
        $tree = new BooleanLiteral(Boolean.parseBoolean($TRUE.text));
        setLocation($tree, $TRUE);
        }
    | FALSE {
        $tree = new BooleanLiteral(Boolean.parseBoolean($FALSE.text));
        setLocation($tree, $FALSE);
        }
    | THIS {
        $tree = new This();
        setLocation($tree, $THIS);
        }
    | NULL {
        $tree = new Null();
        setLocation($tree, $NULL);
        }
    ;

ident returns[AbstractIdentifier tree]
    : IDENT {
        SymbolTable symbTable = this.getDecacCompiler().symbolTable;
        Symbol name = symbTable.create($IDENT.getText());
        $tree = new Identifier(name);
        setLocation($tree, $IDENT);
        }
    ;

/****     Class related rules     ****/

list_classes returns[ListDeclClass tree]
@init{
    $tree = new ListDeclClass();
}
    :
      (c1=class_decl {
        assert($c1.tree != null);   //On ajoute les déclarations de classes une par une
        $tree.add($c1.tree);
        setLocation($tree, $c1.start);
        }
      )*
    ;

class_decl returns [DeclClass tree]
    : CLASS name=ident superclass=class_extension OBRACE class_body CBRACE {
            assert($name.tree != null);
            assert($superclass.tree != null);
            assert($class_body.fields != null);
            assert($class_body.methods != null);
            $tree = new DeclClass($superclass.tree, $name.tree, $class_body.fields, $class_body.methods);
            setLocation($tree, $CLASS);
        }
    ;

class_extension returns[AbstractIdentifier tree]
    : EXTENDS ident {
        //On ajoute l'identifier à la symbolTable
        SymbolTable symbTable = this.getDecacCompiler().symbolTable;
        Symbol name = symbTable.create($ident.text);
        $tree = new Identifier(name);
        setLocation($tree, $ident.start);
        }
    | /* epsilon */ {
        //cas ou la classe n'a pas de classe mère
        SymbolTable symbTable = this.getDecacCompiler().symbolTable;
        Symbol name = symbTable.create("Object");
        $tree = new Identifier(name);
        $tree.setLocation(Location.BUILTIN);
        }
    ;

class_body returns [ListDeclMethod methods, ListDeclField fields]
    @init {
    //méthodes et champs de la classe
    $methods = new ListDeclMethod();
    $fields = new ListDeclField();
    }
    : (m=decl_method {
        assert($m.tree != null);
        $methods.add($m.tree);
        }
      | decl_field_set[$fields]
      )*
    ;

decl_field_set[ListDeclField tree]
    : v=visibility t=type list_decl_field[$v.tree, $t.tree, $tree]
      SEMI
    ;

visibility returns [Visibility tree]
    : /* epsilon */ {
        $tree = Visibility.PUBLIC; //par défaut les champs / class sont public
        }
    | PROTECTED {
        $tree = Visibility.PROTECTED;
        }
    ;

list_decl_field[Visibility v, AbstractIdentifier t, ListDeclField tree]
    : dv1=decl_field[$v, $t]{   //On ajoute les déclarations de champs à l'arbre
    $tree.add($dv1.tree);
    }
        (COMMA dv2=decl_field[$v, $t]
        {
            $tree.add($dv2.tree);
        }
      )*

    ;

decl_field [Visibility v, AbstractIdentifier t] returns [AbstractDeclField tree]
    @init{
        AbstractInitialization init = new NoInitialization(); // si le champ n'est pas initialisé
    }
    : i=ident {
        assert($i.tree != null);
        }
      (EQUALS e=expr {
      init = new Initialization($e.tree);
      setLocation(init, $EQUALS);
        }
      )? {
      $tree = new DeclField($v, $t, $i.tree, init);
      setLocation($tree, $i.start);
        }
    ;

decl_method returns [DeclMethod tree]
@init {
    AbstractMethodBody methodBody = null;   //au début la méthode est vide
}
    : type ident OPARENT params=list_params CPARENT (block {
        methodBody = new MethodBody($block.decls, $block.insts);
        setLocation(methodBody, $block.start);
        }
      | ASM OPARENT code=multi_line_string CPARENT SEMI {
        StringLiteral methodContent = new StringLiteral($code.text.substring(1, $code.text.length() - 1)); //On retire les guillemets
        methodBody = new MethodAsmBody(methodContent);
        setLocation(methodBody, $ASM);
        }
      ) {
      $tree = new DeclMethod($type.tree, $ident.tree, $params.tree, methodBody);
      setLocation($tree, $type.start);
        }
    ;

list_params returns [ListDeclParam tree]
    @init{
    $tree = new ListDeclParam();
    }
    : (p1=param {// On ajoute les paramètres à la liste des paramètre
        assert($p1.tree != null);
        $tree.add($p1.tree);
        } (COMMA p2=param {
        assert($p2.tree != null);
        $tree.add($p2.tree);
        }
      )*)?
    ;

multi_line_string returns[String text, Location location]
    : s=STRING {
            $text = $s.text;
            $location = tokenLocation($s);
        }
    | s=MULTI_LINE_STRING {
            $text = $s.text;
            $location = tokenLocation($s);
        }
    ;

param returns [DeclParam tree]
    : type ident {
        $tree = new DeclParam($type.tree, $ident.tree);
        setLocation($tree, $type.start);
        }
    ;
