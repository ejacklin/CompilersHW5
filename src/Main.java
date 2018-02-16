import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    private Cell sheet[][] = new Cell[6][10];

    private void CreateTable(){
        String str;
        FileReader fr = null;
        BufferedReader br;

        //Read in file
        try {
            fr = new FileReader("src\\HW2-Input.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        br = new BufferedReader(fr);

        try {
            while ((str = br.readLine()) != null) {
                System.out.println(str);
                if(!str.isEmpty()) {
                    ParseLine(str);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                br.close();
                fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void ParseLine(String line){
        char lineChars[] = line.toCharArray();
        int idx = 0;

        //remove blank spaces from start of line
        while(AsciiTable.ASCII[lineChars[idx]] == 0){idx++;}
        //check for and ignore comment lines (lines won't start with symbol)
        if(AsciiTable.ASCII[lineChars[idx]] == 2){return;}

        char row = lineChars[idx++];
        int col = Integer.parseInt(String.valueOf(lineChars[idx++]));
        System.out.println("Row: " + row + " Col: " + col);

        while(idx < lineChars.length){
            char type = AsciiTable.ASCII[lineChars[idx]];

        }

        

    }



    private Cell NumToken(char[] line, int idx){

    }

    private Cell SymToken(char[] line, int idx){

    }



//    Cell getCell(char row, int col){
////        return sheet[]
//    }

    public static void main(String[] args) {
        Main m = new Main();
        m.CreateTable();

//        System.out.println("Hello World!");
    }
}
