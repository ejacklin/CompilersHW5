public class Main {

    public static Cell[][] sheet;
    public static void main(String[] args) {
        String inFile;
        String outFile;
        if(args.length == 2){
            inFile = args[0];
            outFile = args[1];
        }else {
            inFile = "src//HW3-Input.txt";
            outFile = "src//HW3-Output.txt";
        }

        Scanner scanner = new Scanner();
        sheet = new Cell[10][6];
        Cell.InitializeCells(sheet);
        sheet = scanner.CreateTable(inFile, sheet);
        Cell.DisplayAll(sheet, outFile);

    }
}
