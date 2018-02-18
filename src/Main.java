public class Main {

    public static void main(String[] args) {

        String inFile = "src\\HW2-Input.txt";
        String outFile = "src\\HW2-Output.txt";
        Scanner scanner = new Scanner();
        Cell[][] sheet = new Cell[10][6];
        Cell.InitializeCells(sheet);
        sheet = scanner.CreateTable(inFile, sheet);
        Cell.DisplayCells(sheet, outFile);
    }
}
