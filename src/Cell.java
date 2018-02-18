import java.io.*;

/**
 * Created by Erin on 2/16/2018.
 */


public class Cell {

    private String id;
    private boolean error = false;
    private CellType type;
    private String display;
    private int value;
    private Operation op;
    private static boolean initialized;
    private Cell left;
    private Cell right;


    public Cell() {
        this.id = "";
        this.error = false;
        this.type = CellType.BLANK;
        this.display = "";
        this.value = 0;
        this.left = null;
        this.right = null;
    }


    public static Cell[][] InitializeCells(Cell[][] sheet){
        if(initialized){return null;}
        for (int i=0; i<10; ++i) {
            for (int j = 0; j < 6; ++j) {
                sheet[i][j] = new Cell();
                sheet[i][j].setId(i, j);
            }
        }
        initialized = true;
        return sheet;
    }

    public Cell GetCell(String id, Cell[][] sheet) {
        if (id.length() != 2) return null;
        int row = (int)id.charAt(1) - (int)'0';
        int col = (int)id.charAt(0) - (int)'A';
        return sheet[row][col];
    }

    public void SetTXTCell(String txt) {
        error = false;
        type = CellType.TEXT;
        display = txt;
        value = 0;
        left = null;
        op = Operation.NONE;
        right = null;
    }

    public void SetNUMCell(int num, int sign) {
        error = false;
        type = CellType.NUMBER;
        value = num * sign;
        display = Integer.toString(value);
        left = null;
        op = Operation.NONE;
        right = null;
    }

    public void SetNUMCell(String num, int sign) {
        error = false;
        type = CellType.NUMBER;
        value = Integer.parseInt(num) * sign;
        display = Integer.toString(value);
        left = null;
        op = Operation.NONE;
        right = null;
    }

    public void SetEXPCell(Cell lf, Operation o, Cell rt) {
        error = false;
        type = CellType.EXPRESSION;
        left = lf;
        op = o;
        right = rt;
        // value as exp
        CalculateExpression();
        display = Integer.toString(value);
    }

    public void CalculateExpression() {
        if (type != CellType.EXPRESSION || left == null || op == Operation.NONE || right == null) return;
        int X = left.getValue();
        int Y = right.getValue();
        switch (op) {
            case ADD : value = X + Y; break;
            case SUBTRACT: value = X - Y; break;
            case MULTIPLY: value = X * Y; break;
            case DIVIDE:
                if (Y == 0) {
                    System.out.println("ERROR: Divide by zero in cell " + id);
                    error = true;
                    value = 0;
                    return;
                }
                value = X / Y; break;
            default :
                System.out.println("ERROR: Unrecognized operator in cell " + id);
                error = true;
                value = 0;
                break;
        }
    }

    public static void CalculateCells(Cell[][] cell) {
        for (int i=0; i<10; ++i) for (int j=0; j<6; ++j) {
            if (cell[i][j].type == CellType.EXPRESSION)
                cell[i][j].CalculateExpression();
        }
    }

    public void setId(int row, int col) {this.id = "" + (char)(col+'A')+(char)(row + '0');}
    public String getId() {return id;}
    public Boolean isError() {return error;}
    public CellType getType() {return type;}
    public int getValue() {return value;}
    public int getRow() {return (int)id.charAt(0) - (int)'0';}
    public int getCol() {return (int)id.charAt(1) - (int)'A';}

    public static void DisplayCells(Cell[][] cell, String outputFile) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(outputFile, "UTF-8");

            String header = String.format("%5s%c%5s%5s%c%5s%5s%c%5s%5s%c%5s%5s%c%5s%5s%c%5s%5s%c","",'|',"A","",
                    '|',"B","",'|',"C","",'|',"D", "",'|',"E","",'|',"F","",'|');
            System.out.println(header);
            writer.println(header);


            for (int i=0; i<10; ++i) {
                System.out.print(String.format("%2s%d%2s%c","",i,"",'|'));
                writer.print(String.format("%2s%d%2s%c","",i,"",'|'));
                for (int j = 0; j < 6; ++j) {
                    String str = String.format("%5s%5s%c","",cell[i][j].display,'|');
                    System.out.print(str);
                    writer.print(str);
                }
                System.out.println();
                writer.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            writer.close();
        }
    }

    public enum CellType {
        TEXT, NUMBER, EXPRESSION, BLANK
    }

    public enum Operation {
        NONE, ADD, SUBTRACT, MULTIPLY, DIVIDE
    }


}
