
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
        if(AsciiTable.ASCII[lineChars[idx]] == 8){return;}

        //Read Column
        int col = (int)lineChars[idx++] - 'A';

        //Read row
        String readRow = "";
        int row = -1;
        while(AsciiTable.ASCII[lineChars[idx]] == 3){ readRow += lineChars[idx]; idx++; }
        try {
            row = Integer.parseInt(String.valueOf(readRow));
        }catch(NumberFormatException e){
            System.out.println("Error parsing row value, not a number");
            e.printStackTrace();
        }

        //try to access cell, throw out of bounds if unavailable
        try {
            cell = sheet[row][col];
        }catch (ArrayIndexOutOfBoundsException e){
            if(row > 9){
                System.out.println("There are only 10 rows");
            }
            if(col > 5){
                System.out.println("There are only 6 rows");
            }
            e.printStackTrace();
        }

        try{
            if(AsciiTable.ASCII[lineChars[idx]] != 0){
                System.out.println("Must be a space between cell location and cell entry");
                throw new IllegalArgumentException("INVALID CELL");
            }
        } catch(IllegalArgumentException e){
            e.getMessage();
            System.exit(-1);
        }

        //check for "clear" line
        String onlyWSCheck = new String(lineChars).substring(idx,lineChars.length);
        boolean onlyWS = onlyWSCheck.trim().isEmpty();
        if(onlyWS) {
            cell.ClearControllers();
            cell.SetBlankCell();
            cell.RemoveOldControllers();
            return;}

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
                    ArrayList<Token> tokens = EquationTokens(lineChars);
                    Parse parse = new Parse();
                    parse.Parse(tokens, sheet, cell.getId());
                    break;
                case 7:     // ' " '
                    TextToken(lineChars);
                    break;
                case 8:
                    System.out.println("Comments don't belong in cells");
                    cell.SetError();
                    idx = lineChars.length;
                    break;
                default:
                    System.out.println("Unhandled case");
                    idx++;
            }
        }
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

        if (word.length() > 6) {
            System.out.println("Strings can only be max 6 characters");
            cell.SetError();
        }
        idx = lineChars.length;
    }

    //parse the equation line and save off each
    private ArrayList<Token> EquationTokens(char[] lineChars) {
        ArrayList<Token> tokens = new ArrayList<>();
        idx++; //index past '='
        while ((AsciiTable.ASCII[lineChars[idx]] == 0)){
            idx++;
        } //find first id
        while (idx < lineChars.length) {
            char type = AsciiTable.ASCII[lineChars[idx]];
            switch (type) {
                case 0:idx++;
                    break;
                case 2: //operation
                    char op = lineChars[idx];
                    switch (op){
                        case '+':
                            tokens.add(new Token(Token.TokenType.PLUS, (Cell.Operation) AsciiTable.Ops.get(lineChars[idx])));
                            break;
                        case '-':
                            tokens.add(new Token(Token.TokenType.MINUS, (Cell.Operation) AsciiTable.Ops.get(lineChars[idx])));
                            break;
                        case '*':
                            tokens.add(new Token(Token.TokenType.TIMES, (Cell.Operation) AsciiTable.Ops.get(lineChars[idx])));
                            break;
                        case '/':
                            tokens.add(new Token(Token.TokenType.DIVIDE, (Cell.Operation) AsciiTable.Ops.get(lineChars[idx])));
                            break;
                    }
                    idx++;
                    break;
                case 3: //number
                    Integer num = ExpNumToken(lineChars);
                    tokens.add(new Token(Token.TokenType.NUM, num));
                    break;
                case 4: //id
                    String id = ExpIdToken(lineChars);
                    tokens.add(new Token(Token.TokenType.ID, id));
                    break;
                case 10: //left paren
                    tokens.add(new Token(Token.TokenType.LPAREN, String.valueOf(lineChars[idx])));
                    idx++;
                    break;
                case 11: //right paren
                    tokens.add(new Token(Token.TokenType.RPAREN, String.valueOf(lineChars[idx])));
                    idx++;
                    break;
                default:
                    System.out.println("Error parsing equation");
                    break;
            }
        }
        return tokens;
    }

    Integer ExpNumToken(char[] lineChars){
        String num = "";
        while(AsciiTable.ASCII[lineChars[idx]] == 3){   //assume there could be spaces after
            num += lineChars[idx];
            idx++;
            if(idx > lineChars.length-1) {break;}
        }
        return Integer.parseInt(num);
    }
    String ExpIdToken(char[] lineChars){
        String id = "";
        while((AsciiTable.ASCII[lineChars[idx]] == 3) || (AsciiTable.ASCII[lineChars[idx]] == 4)){   //assume there could be spaces after
            id += lineChars[idx];
            idx++;
            if(idx > lineChars.length-1) {break;}
        }
        return id;
    }
}