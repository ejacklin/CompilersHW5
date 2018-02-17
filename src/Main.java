import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Main {

    private Cell sheet[][] = new Cell[6][10];
    private int idx;

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
        idx = 0;

        //remove blank spaces from start of line
        while(AsciiTable.ASCII[lineChars[idx]] == 0){idx++;}
        //check for and ignore comment lines (lines won't start with symbol)
        if(AsciiTable.ASCII[lineChars[idx]] == 2){return;}

        char row = lineChars[idx++];
        int col = Integer.parseInt(String.valueOf(lineChars[idx++]));
//        System.out.println("Row: " + row + " Col: " + col);

        while(idx < lineChars.length){
            Cell cell;
            char type = AsciiTable.ASCII[lineChars[idx]];

            switch (type){
                case 0: //whitespace
                    break;
                case 3:     //number
                    cell = NumToken(lineChars);
                    cell.setColumn(col);
                    cell.setRow(row);
                    sheet[(int) AsciiTable.Row.get(row)][col] = cell;
                    break;
                case 2:     // only thing this can be is '-'
                    idx++;
                    cell = NumToken(lineChars);
                    cell.setColumn(col);
                    cell.setRow(row);
                    cell.setInput("-" + cell.getInput());
                    break;

                case 6:     // '='
                    cell = EquationToken(lineChars);
                    cell.setColumn(col);
                    cell.setRow(row);
                    sheet[(int) AsciiTable.Row.get(row)][col] = cell;
                    break;
                case 7:     // ' " '
                    cell = TextToken(lineChars);
                    cell.setColumn(col);
                    cell.setRow(row);
                    sheet[(int) AsciiTable.Row.get(row)][col] = cell;
                    break;
                default:
                    System.out.println("Unhandled case");
            }
            idx++;
        }
    }

    private Cell EquationToken(char[] lineChars) {
        StringBuilder eq = new StringBuilder();
        char symbols[] = new char[5];
        char rows[] = new char[5];
        char cols[] = new char[5];
        int s = 0;
        int r = 0;
        int c = 0;
        char[] charsLeft = Arrays.copyOfRange(lineChars, idx, lineChars.length-1);
        for(char ch : charsLeft){
            eq.append(ch);
            char type = AsciiTable.ASCII[ch];
            switch (type){
                case 0:
                    break;
                case 2:
                    symbols[s] = ch;
                    break;
                case 3:

                    break;
                case 4:
                    rows[r] =
                    break;
                default:
                    System.out.println("Error parsing equation");
                    break;


            }
        }


    }


    private Cell NumToken(char[] lineChars){
        StringBuilder num = new StringBuilder();
        while (AsciiTable.ASCII[lineChars[idx]] == 3){
            num.append(lineChars[idx]);

            if(idx == lineChars.length - 1) {break;} //see if we're at end of lineChars
            idx++;
        }
        return new Cell(num.toString(), AsciiTable.Token_Type.NUM);
    }

    private Cell TextToken(char[] lineChars){
        idx++;  //move on from first quote
        String word = "";
        while (AsciiTable.ASCII[lineChars[idx]] != 7){
            word += lineChars[idx];
            idx++;
        }
        return new Cell(word, AsciiTable.Token_Type.TEXT);
    }

    public static void main(String[] args) {
        Main m = new Main();
        m.CreateTable();

    }
}
