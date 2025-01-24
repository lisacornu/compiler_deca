package fr.ensimag.deca.codegen;

public class StackUsageWatcher {
    public int nbSavedRegisters;
    public int nbVariables;
    public int nbTempRegisters;
    public int nbParam;

    public StackUsageWatcher () {
        this.nbSavedRegisters = 0;
        this.nbVariables = 0;
        this.nbTempRegisters = 0;
        this.nbParam = 0;
    }

    public int getTotal() {
        return nbSavedRegisters + nbVariables + nbTempRegisters + nbParam;
    }
}
