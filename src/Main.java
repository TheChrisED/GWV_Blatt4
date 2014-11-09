import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main
{

    /**
     * Starts up the program on three environments. 
     * @param args
     */
    public static void main(String[] args)
    {
        // Loading the environments
        Start startBlatt3 = new Start("3");
        Start startBlatt4a = new Start("a");
        Start startBlatt4b = new Start("b");

        System.out.println("Zum beenden \"q\" eingeben und Enter drücken \n");
        while (true)
        {
            String ausgabe = "Auf welcher Umgebung soll die Suche gestartet werden? \n"
                    + "Blatt 3: 3, Blatt 4a: a, Blatt 4b: b";
            String modus = "";
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(ausgabe);
            try {
                modus = console.readLine();
            } catch (IOException e) {
                // Sollte eigentlich nie passieren
                e.printStackTrace();
            }
            
            switch(modus)
            {
            case "3":
                startBlatt3.startAStarSearch();
                break;
            case "a":
                startBlatt4a.startAStarSearch();
                break;
            case "b":
                startBlatt4b.startAStarSearch();
                break;
            case "q":
                System.exit(0);
                break;
            default:
                System.out.println("Unbekannte Eingabe!");
                break;
            }
            System.out.println("Zum Fortfahren Enter drücken!");
            try {
                modus = console.readLine();
            } catch (IOException e) {
                // Sollte eigentlich nie passieren
                e.printStackTrace();
            }
        }
    }

}
