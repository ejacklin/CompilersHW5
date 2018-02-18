
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Erin on 2/17/2018.
 */
public class Scanner {
    private int idx;
    private Cell[][] sheet;
    private Cell cell;

    public Cell[][] CreateTable(String fileName, Cell[][] sheet){
        this.sheet = sheet;
        String str;
        FileReader fr = null;
        BufferedReader br;

        //Read in file
        try {
            fr = new FileReader(fileName);
        } catch (FileNotFoundException e) {e.printStackTrace();}

        br = new BufferedReader(fr);

        try {
            while ((str = br.readLine()) != null) {
                System.out.println(str);
                if(!str.isEmpty()) {
                    ParseLine(str);
                }
            }
            Cell.CalculateCells(sheet);
        }catch (IOException e){e.printStackTrace();
        }finally {
            try {
                br.close();
                fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return sheet;
    }

    private void ParseLine(String line){
        char lineChars[] = line.toCharArray();
        idx = 0;

        //remove blank spaces from start of line
        while(AsciiTable.ASCII[lineChars[idx]] == 0){idx++;}
        //check for and ignore comment lines (lines won't start with symbol)
        if(AsciiTable.ASCII[lineChars[idx]] == 2){return;}

        int col = (int)lineChars[idx++] - 'A';
        int row = Integer.parseInt(String.valueOf(lineChars[idx++]));
        cell = sheet[row][col];

        while(idx < lineChars.length){

            char type = AsciiTable.ASCII[lineChars[idx]];

            switch (type){
                case 0: //whitespace
                    idx++;
                    break;
                case 3:     //number
                    NumToken(lineChars, 1);
                    break;
                case 2:     // only thing this can be is '-'
                    idx++;
                    NumToken(lineChars, -1);
                    break;
                case 6:     // '='
                    EquationToken(lineChars);
                    break;
                case 7:     // ' " '
                    TextToken(lineChars);
                    break;
                default:
                    System.out.println("Unhandled case");
                    idx++;
            }
        }
    }

    private void EquationToken(char[] lineChars) {
        ArrayList<Integer> rows = new ArrayList<>();
        ArrayList<Cell.Operation> ops = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();

        while(AsciiTable.ASCII[lineChars[idx]] != 4){idx++;} //find first letter
        char[] charsLeft = Arrays.copyOfRange(lineChars, idx, lineChars.length);
        for(char ch : charsLeft){
            char type = AsciiTable.ASCII[ch];
            switch (type){
                case 0:
                    break;
                case 2:
                    ops.add((Cell.Operation) AsciiTable.Ops.get(ch));
                    break;
                case 3:
                    rows.add(Character.getNumericValue(ch));
                    break;
                case 4:
                    cols.add((int)ch - (int)'A');
                    break;
                default:
                    System.out.println("Error parsing equation");
                    break;
            }
        }
        Cell left = sheet[rows.get(0)][cols.get(0)];
        Cell right = sheet[rows.get(1)][cols.get(1)];
        cell.SetEXPCell(left,ops.get(0),right);
        idx = lineChars.length;
    }

    private void NumToken(char[] lineChars, int sign){
        String num = "";
        while(AsciiTable.ASCII[lineChars[idx]] == 3){   //assume there could be spaces after
            num += lineChars[idx];
            idx++;
            if(idx > lineChars.length-1) {break;}
        }
        cell.SetNUMCell(num, sign);
    }

    private void TextToken(char[] lineChars){
        idx++;  //move on from first quote
        String word = "";
        while(AsciiTable.ASCII[lineChars[idx]] != 7){   //assume there could be spaces after, look for end "
            word += lineChars[idx];
            idx++;
            if(idx > lineChars.length-1) {break;}
        }
        cell.SetTXTCell(word);
        idx = lineChars.length;
    }
}
