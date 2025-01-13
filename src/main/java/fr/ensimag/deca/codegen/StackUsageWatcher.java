package fr.ensimag.deca.codegen;

public class StackUsageWatcher {
    private int nbSavedRegisters;
    private int nbVariables;
    private int nbTempRegisters;
    private int nbParam;

    public StackUsageWatcher () {
        this.nbSavedRegisters = 0;
        this.nbVariables = 0;
        this.nbTempRegisters = 0;
        this.nbParam = 0;
    }

    public int getNbSavedRegisters() {return this.nbSavedRegisters;}
    public int getNbVariables() {return this.nbVariables;}
    public int getNbTempRegisters() {return this.nbTempRegisters;}
    public int getNbParam() {return this.nbParam;}

    public void addToNbSavedRegisters() {this.nbSavedRegisters++;}
    public void addToNbVariables() {this.nbVariables++;}
    public void addToNbTempRegisters() {this.nbTempRegisters++;}
    public void addToNbParam() {this.nbParam++;}
}
