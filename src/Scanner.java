
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Erin on 2/17/2018.
 */
public class Scanner {
    private int idx;
    private Cell[][] sheet;
    private Cell cell;

    private PrintWriter writer = null;

    public Cell[][] CreateTable(String fileName, Cell[][] sheet, PrintWriter writer){
        this.sheet = sheet;
        this.writer = writer;
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
//                System.out.println(str);
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
            String err = "Error parsing: " + line + "\n";
            err += "Row value, not a number";
            System.out.println(err);
            writer.println(err);
            return;
        }

        //try to access cell, throw out of bounds if unavailable
        try {
            cell = sheet[row][col];
        }catch (ArrayIndexOutOfBoundsException e){
            String err = "Error parsing: " + line + "\n";

            if(row > 9){
                err += "There are only 10 rows";
            }
            if(col > 5){
                err += "There are only 6 rows";
            }
            System.out.println(err);
            writer.println(err);
            return;
        }

        try{
            if(AsciiTable.ASCII[lineChars[idx]] != 0){

                throw new IllegalArgumentException("INVALID CELL");
            }
        } catch(IllegalArgumentException e){
            String err = "Error parsing: " + line + "\n";
            err += "Must be a space between cell location and cell entry";
            System.out.println(err);
            writer.println(err);
            return;
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
                case 2:
                case 3:     //number
                    NumToken(lineChars);
                    break;
                case 6:     // '='
                    ArrayList<Token> tokens = EquationTokens(lineChars);
                    Parse parse = new Parse(writer);
                    parse.ParseTree(tokens, sheet, cell.getId());
                    break;
                case 7:     // ' " '
                    TextToken(lineChars);
                    break;
                case 8:
                    System.out.println("Comments don't belong in cells");
                    cell.SetError();
                    cell.SetErrorMessage("Comments don't belong in cells");
                    idx = lineChars.length;
                    break;
                default:
                    System.out.println("Unhandled case");
                    idx++;
            }
        }
    }


    private void NumToken(char[] lineChars) {
        String number = "";
        String pp = "^(-)?(\\d)+(\\.(\\d)+)?(([Ee])?([+-])?(\\d)+)?$";
        Pattern pattern = Pattern.compile(pp);

        while (AsciiTable.ASCII[lineChars[idx]] != 0) {   //assume there could be spaces after
            number += lineChars[idx];
            idx++;
            if (idx > lineChars.length - 1) {
                break;
            }
        }
        Matcher matcher = pattern.matcher(number);
        if (!matcher.matches()){
            System.out.println("Error in cell, not a number");
            cell.SetError();
            cell.SetErrorMessage("Not able to parse number");
        }else{
            if((matcher.group(3) != null) || (matcher.group(6) != null)){
                Double d = Double.parseDouble(number);
                cell.SetNUMCell(d);
            }else{
                Integer i = Integer.parseInt(number);
                cell.SetNUMCell(i);
            }
        }




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
                    cell.SetError();
                    cell.SetErrorMessage("Token ID does not exist");
                    idx = lineChars.length;
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
//        if(!cell.VerifyValidID(id)){
//            cell.SetError();
//            cell.SetErrorMessage("Token ID does not exist");
//        }
        return id;
    }
}