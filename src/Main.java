import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Main {

    public static Cell[][] sheet;
    public static void main(String[] args) {
        String inFile;
        String outFile;
        if(args.length == 2){
            inFile = args[0];
            outFile = args[1];
        }else {
            inFile = "src\\HW5-Input.txt";
            outFile = "src\\HW5-Output.txt";
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outFile, "UTF-8");

        Scanner scanner = new Scanner();
        sheet = new Cell[10][6];
        Cell.InitializeCells(sheet);
        sheet = scanner.CreateTable(inFile, sheet, writer);
        Cell.DisplayAll(sheet, writer);

        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }

    }
}
