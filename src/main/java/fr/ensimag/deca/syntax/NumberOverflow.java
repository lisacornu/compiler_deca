package fr.ensimag.deca.syntax;

import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Exception gérant les débordements d'entier et de flottants
 */
public class NumberOverflow extends DecaRecognitionException {

    public NumberOverflow(DecaParser parser, ParserRuleContext ctx){
        super(parser, ctx);
    }

    @Override
    public String getMessage(){
        return "Number overflow error";
    }

}
