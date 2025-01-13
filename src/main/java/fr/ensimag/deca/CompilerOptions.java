package fr.ensimag.deca;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;




/**
 * User-specified options influencing the compilation.
 *
 * @author gl31
 * @date 01/01/2025
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;

    public int getDebug() { return debug; }

    public boolean getParallel() { return parallel; }

    public boolean getPrintBanner() { return printBanner; }
    
    public List<File> getSourceFiles() { return Collections.unmodifiableList(sourceFiles); }

    public boolean getParse() { return parse; }

    public boolean getSetRegister() { return setRegister; }

    public int getNbRegister() { return nbRegister; }



    public boolean getVerify() {
        return verify;
    }

    private int debug = 0;
    private int nbRegister = 16;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parse = false, verify = false, noCheck = false, setRegister = false;

    private List<File> sourceFiles = new ArrayList<File>();

    
    public void parseArgs(String[] args) throws CLIException {

        //TODO : verifier le format des erreurs

        //Lecture des options

        Iterator<String> argIterator = Arrays.asList(args).iterator();
        while (argIterator.hasNext()) {

            String arg = argIterator.next();
            switch(arg) {

                case "-b": //Affiche la bannière
                    printBanner = true;
                    break;

                case "-p": //Decompile l'arbre du programme et affiche le résultat en sortie
                    if (verify) throw new CLIException("Options -p et -v sont incompatibles.");
                    parse = true;
                    break;

                case "-v":
                    if (parse) throw new CLIException("Options -p et -v sont incompatibles.");
                    verify = true;
                    break;

                case "-n":
                    noCheck = true;
                    break;

                case "-r":
                    if (!argIterator.hasNext()) throw new CLIException("Option -r nécessite un argument (nombre de registres).");
                    try {
                        int registers = Integer.parseInt(argIterator.next());
                        if (registers < 2 || registers > 16)
                            throw new CLIException("Le nombre de registres doit être entre 4 et 16.");
                        this.nbRegister = registers;
                        this.setRegister = true;
                    } catch (NumberFormatException e) {
                        throw new CLIException("Argument de -r invalide : " + arg);
                    }
                    break;

                case "-d":
                    debug++;
                    break;

                case "-P":
                    parallel = true;
                    break;

                default:
                    if (arg.startsWith("-")) throw new CLIException("Option inconnue : " + arg);
                    sourceFiles.add(new File(arg));
                    break;
            }
        }

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET:
            break;
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!

        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }
    }

    protected void displayBanner() {
        System.out.println(
            "     /\\\\\\\\\\\\\\\\\\\\\\\\  /\\\\\\                 /\\\\\\\\\\\\\\\\\\\\       /\\\\\\         \n" +
            "    /\\\\\\//////////  \\/\\\\\\               /\\\\\\///////\\\\\\  /\\\\\\\\\\\\\\        \n" +
            "    /\\\\\\             \\/\\\\\\              \\///      /\\\\\\  \\/////\\\\\\       \n" +
            "    \\/\\\\\\    /\\\\\\\\\\\\\\ \\/\\\\\\                     /\\\\\\//       \\/\\\\\\      \n" +
            "     \\/\\\\\\   \\/////\\\\\\ \\/\\\\\\                    \\////\\\\\\      \\/\\\\\\     \n" +
            "      \\/\\\\\\       \\/\\\\\\ \\/\\\\\\                       \\//\\\\\\     \\/\\\\\\    \n" +
            "       \\/\\\\\\       \\/\\\\\\ \\/\\\\\\              /\\\\\\      /\\\\\\      \\/\\\\\\   \n" +
            "        \\//\\\\\\\\\\\\\\\\\\\\\\\\/  \\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ \\///\\\\\\\\\\\\\\\\\\/       \\/\\\\\\  \n" +
            "          \\////////////    \\///////////////    \\/////////         \\///  "
        );
    }

    protected void displayUsage() {
        System.out.println("Usage : decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]");
        System.out.println(
            "-b (banner) : affiche une bannière indiquant le nom de l'équipe.\n" +
            "-p (parse) : arrête decac après l'étape de construction de l'arbre, et affiche la décompilation de ce dernier (i.e. s'il n'y a qu'un fichier source à compiler, la sortie doit être un programme deca syntaxiquement correct)\n" +
            "-v (verification) : arrête decac après l'étape de vérifications (ne produit aucune sortie en l'absence d'erreur)\n" +
            "-n (no check) : supprime les tests à l'exécution spécifiés dans les points 11.1 et 11.3 de la sémantique de Deca.\n" +
            "-r X (registers) : limite les registres banalisés disponibles à R0 ... R{X-1}, avec 4 <= X <= 16\n" +
            "-d (debug) : active les traces de debug. Répéter l'option plusieurs fois pour avoir plus de traces.\n" +
            "-P (parallel) : s'il y a plusieurs fichiers sources, lance la compilation des fichiers en parallèle (pour accélérer la compilation)\n"
        );
    }
}
