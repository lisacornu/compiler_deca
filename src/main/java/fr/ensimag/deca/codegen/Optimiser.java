package fr.ensimag.deca.codegen;

import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.tree.*;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.ImmediateInteger;

public class Optimiser {

    //Convertit un tableau en un entier de nombre binaire
    private static int[] toBinaryArray(int num) {
        int[] binary = new int[32];
        for (int i = 0; i < 32; i++) {
            binary[31 - i] = (num >> i) & 1;
        }
        return binary;
    }

    //Change une suite binaire 01...1 -> 10...0(-1) selon la representation de Booth
    static private void boothConsecutiveOnes(int[] booth, int a, int b) {
        for(int j = a; j < b; j++) booth[j] = 0;
        booth[a-1] = 1;
        booth[b] = -1;
    }

    //Convertit un entier en une representation binaire de Booth
    private static int[] boothRecoding(int num) {

        int[] bin = toBinaryArray(num);

        //Pas de recoding possible si num >= 2^30
        if (bin[1] == 1) return bin;
        int[] booth = new int[32];
        int start = -1; //-1 represente que start n'est pas set

        //On commence à 2 pour éviter d'écraser le bit de signe
        for (int i = 2; i < 32; i++) {
            if (bin[i] == 1) {
                booth[i] = 1;
                if (i!=2 && start == -1) start = i;
            } else {
                booth[i] = 0;
                if (start != -1 && i-start > 1)
                    boothConsecutiveOnes(booth, start, i-1);
                start = -1;
            }
        }
        //Traité le dernier bit
        if (start != -1 && start != 31) boothConsecutiveOnes(booth, start, 31);

        //Derniers bits
        booth[0] = bin[0]; //bit de signe;
        booth[1] = bin[1]; //premier bit skip
        return booth;
    }

    //Convertit une representation binaire de Booth en entier
    private static int boothToInt(int[] booth) {
        int result = 0;
        for(int i = 1; i <32; i++)
            result += booth[i] * (1 << (31-i));
        return result;
    }

    //Calcul les cycles de clock qu'effectuera une multiplication de Booth
    private static int countClockCycles(int[] booth) {
        int clockCycles = 0;
        boolean firstAdd = true; // Skip the first add/sub
        for(int i = 1; i < 32; i++) {
            if (booth[i] == 0) continue;
            clockCycles += (31-i)*2; // BitShift clock cycles
            if (firstAdd) {
                firstAdd = false;
                continue;
            }
            clockCycles += 2; // Add/Sub clock cycles
        }
        return clockCycles;
    }

    //Renvoi le dernier bit actif (1 ou -1) d'une representation de Booth
    private static int LastActiveBit(int[] booth) {
        for(int i = 0; i < 31; i++)
            if (booth[31-i] != 0) return 31-i;
        return -1;
    }

    //Renvoi le premier bit actif (1 ou -1) d'une representation de Booth
    private static int FirstActiveBit(int[] booth) {
        for(int i = 1; i < 32; i++)
            if (booth[i] != 0) return i;
        return -1;
    }

    //Renvoi le prochain bit actif  d'une representation de Booth par rapport à i
    private static int getNextActiveBit(int booth[], int i) {
        for (int j = i+1; j < 32; j++)
            if (booth[j] != 0) return j;
        return -1;
    }

    //Construit l'arbre correspond à un bitshift gauche de i
    private static AbstractExpr ShiftLefti(AbstractExpr operand, int i) {
        AbstractExpr operandShift = operand;
        for (int j = 0; j < i; j++)
            operandShift = new ShiftLeft(operandShift);
        return operandShift;
    }

    //Construit l'arbre d'une multiplication de Booth
    private static AbstractExpr buildBitshiftTree(AbstractExpr expr, int[] booth, int i, int lastActiveBit) {

        if (i == lastActiveBit) return ShiftLefti(expr, 31-i);

        int nextBitInd = getNextActiveBit(booth, i);
        AbstractExpr nextExpr = buildBitshiftTree(expr, booth, nextBitInd, lastActiveBit);

        if (booth[nextBitInd] == 1)
            return new Plus(ShiftLefti(expr,31-i),nextExpr);

        return new Minus(ShiftLefti(expr,31-i),nextExpr);
    }


    public static AbstractExpr FastMultiply(AbstractExpr expr1, AbstractExpr expr2) {

        //On test si on a une multiplication entre un int et une expression
        AbstractExpr expr;
        IntLiteral integer;
        if (expr1 instanceof IntLiteral) {
            integer = (IntLiteral)expr1;
            expr = expr2;
        } else if (expr2 instanceof IntLiteral) {
            integer = (IntLiteral)expr2;
            expr = expr1;
        } else {
            return new Multiply(expr1, expr2);
        }

        int value = integer.getValue();
        if (value == 0) return new IntLiteral(0); //Multiplication par 0

        int[] booth = boothRecoding(value); //Representation de Booth
        if (value < 0 || countClockCycles(booth) >= 20) //Si la multiplication de booth est plus lente (ou que value négative)
            return new Multiply(expr1, expr2); //Multiplication classique

        //Construction de l'arbre de la multiplication de Booth
        return buildBitshiftTree(expr, booth, FirstActiveBit(booth), LastActiveBit(booth));
    }


    public static AbstractExpr FastDiv(AbstractExpr expr, IntLiteral integer) {

        int value = integer.getValue();

        int[] booth = boothRecoding(value); //Representation de Booth
        if (value < 0 || countClockCycles(booth) >= 20) //Si la multiplication de booth est plus lente (ou que value négative)
            return new Multiply(expr, integer); //Multiplication classique

        //Construction de l'arbre de la multiplication de Booth
        return buildBitshiftTree(expr, booth, FirstActiveBit(booth), LastActiveBit(booth));
    }




}
