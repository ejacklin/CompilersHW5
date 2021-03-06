import java.io.*;
import java.util.ArrayList;

/**
 * Created by Erin on 2/16/2018.
 */


public class Cell {

    private String id;
    private boolean error = false;
    private String errorMessage;
    private CellType type;
    private String display;
    public int value;
    public double doubleValue;
    private Operation op;
    private static boolean initialized;
    private Cell left;
    private Cell right;
    private ArrayList<Cell> controllers;
    private ArrayList<Cell> users;
    private ArrayList<Cell> oldConts;
    private TreeNode treeNode;
    static private ArrayList<String> validIds;
    public NumberType numberType;








    public Cell() {
        this.id = "";
        this.error = false;
        this.type = CellType.BLANK;
        this.display = "";
//        this.value;
        this.left = null;
        this.right = null;
        this.controllers = new ArrayList<>();
        this.users = new ArrayList<>();
        this.validIds = new ArrayList<>();
        this.oldConts = null;
    }


    public static Cell[][] InitializeCells(Cell[][] sheet){
        if(initialized){return null;}
        for (int i=0; i<10; ++i) {
            for (int j = 0; j < 6; ++j) {
                sheet[i][j] = new Cell();
                sheet[i][j].setId(i, j);
                validIds.add("" + (char)(j+'A')+(char)(i + '0'));
            }
        }
        initialized = true;
        return sheet;
    }

    public static Cell GetCell(String id, Cell[][] sheet) {
        if (id.length() != 2) return null;
        int row = (int)id.charAt(1) - (int)'0';
        int col = (int)id.charAt(0) - (int)'A';
        return sheet[row][col];
    }

    public void SetBlankCell(){
        this.error = false;
        this.numberType = NumberType.NONE;
        this.type = CellType.BLANK;
        this.display = "";
        this.value = 0;
        this.left = null;
        op = Operation.NONE;
        this.right = null;
        UpdateCells();
    }

    public void SetTXTCell(String txt) {
        error = false;
        type = CellType.TEXT;
        numberType = NumberType.NONE;
        display = txt;
        value = 0;
        left = null;
        op = Operation.NONE;
        right = null;
        UpdateCells();
    }

    public void SetNUMCell(int num) {
        error = false;
        type = CellType.NUMBER;
        value = num;
        display = Integer.toString(value);
        left = null;
        op = Operation.NONE;
        right = null;
        numberType = NumberType.INTEGER;

        UpdateCells();
    }

    public void SetNUMCell(double num) {
        error = false;
        type = CellType.NUMBER;
        doubleValue = num;
        display = Double.toString(doubleValue);
        left = null;
        op = Operation.NONE;
        right = null;
        numberType = NumberType.DOUBLE;

        UpdateCells();
    }

    public void SetNUMCell(String num, boolean isInteger) {
        error = false;
        type = CellType.NUMBER;
        value = Integer.parseInt(num);
        display = Integer.toString(value);
        left = null;
        op = Operation.NONE;
        right = null;
        numberType = NumberType.INTEGER;
        UpdateCells();
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


    public void SetEXPCell(TreeNode treeNode) {
        error = false;
        numberType = NumberType.NONE;
        type = CellType.EXPRESSION;
        this.treeNode = treeNode;
        CalculateExpression();
        RemoveOldControllers();
    }

    public void AddController(Cell cell){
        if(!controllers.contains(cell)) {
            controllers.add(cell);
        }
    }

    public void AddUser(Cell cell){
        if(!users.contains(cell)) {
            users.add(cell);
        }
    }

    public void RemoveUser(Cell cell){
        if(users.contains(cell)){
            users.remove(cell);
        }
    }

    public void RemoveOldControllers(){

        oldConts.removeAll(controllers);
        for (Cell c : oldConts) {
                c.RemoveUser(this);
        }

    }

    public void ClearControllers(){

        if((oldConts !=null) && (!oldConts.isEmpty())) {
            oldConts.clear();
        }
        oldConts = new ArrayList<>(controllers);
        if(!controllers.isEmpty()) {
            controllers.clear();
        }
    }


    public void CalculateExpression() {
        try {
            ReturnTreeStruct result = Parse.TraverseTree(treeNode);
            if(result.isInt){
                display = Integer.toString((int)result.value);
                value = (int)result.value;
            }else{
                display = Double.toString(result.value);
                doubleValue = result.value;
            }

        }catch(RuntimeException e){
            error = true;
            display = "ERROR";
        }
    }


    public void UpdateCells() {
        if(!users.isEmpty()){
            for (Cell u: users) {
                try {
                    ReturnTreeStruct result = Parse.TraverseTree(u.treeNode);
                    if(result.isInt){
                        u.display = Integer.toString((int)result.value);
                        u.value = (int)result.value;
                    }else{
                        u.display = Double.toString(result.value);
                        u.doubleValue = result.value;
                    }


//                    u.display = Integer.toString(result);
//                    u.value = result;
                    u.error = false;
                }catch(RuntimeException e){
                    u.error = true;
                    u.errorMessage = "Invalid cell value in equation";
                    u.display = "ERROR";
                }
            }
        }
    }

    public void setId(int row, int col) {this.id = "" + (char)(col+'A')+(char)(row + '0');}
    public String getId() {return id;}
    public Boolean isError() {return error;}
    public void SetError() {error = true; display = "ERROR";}
    public String GetErrorMessage(){return errorMessage;}
    public void SetErrorMessage( String errorMsg){errorMessage = errorMsg;}
    public CellType getType() {return type;}
    public int getValue() {return value;}
    public void setValue(double val){doubleValue = val;value = (int) val;}
    public void setValue(int val){doubleValue = (double) val;value = val;}
    public int getRow() {return (int)id.charAt(0) - (int)'0';}
    public int getCol() {return (int)id.charAt(1) - (int)'A';}
    public NumberType GetNumberType() {
        return numberType;
    }
    public void SetNumberType(NumberType numberType) {
        this.numberType = numberType;
    }


    public static void DisplayAll(Cell[][] cell, PrintWriter writer) {


            //Display Cells
            String header = String.format("%6s%c%6s%6s%c%6s%6s%c%6s%6s%c%6s%6s%c%6s%6s%c%6s%6s%c","",'|',"A","",
                    '|',"B","",'|',"C","",'|',"D", "",'|',"E","",'|',"F","",'|');
            System.out.println(header);
            writer.println(header);


            for (int i=0; i<10; ++i) {
                System.out.print(String.format("%3s%d%2s%c","",i,"",'|'));
                writer.print(String.format("%3s%d%2s%c","",i,"",'|'));
                for (int j = 0; j < 6; ++j) {
                    String str = String.format("%6s%6s%c","",cell[i][j].display,'|');
                    System.out.print(str);
                    writer.print(str);
                }
                System.out.println();
                writer.println();
            }

            System.out.println();
            writer.println();

            //display users/controllers
            String str;
            for (int i=0; i<10; ++i) {
                for (int j = 0; j < 6; ++j) {
                    Cell c = cell[i][j];
                    str = c.toString();
                    System.out.print(str);
                    writer.print(str);

                    //print tree if expression
                    if(c.getType() == CellType.EXPRESSION) {
                        System.out.println();
                        writer.println();
                        System.out.println("Expression AST:");
                        writer.println("Expression AST:");
                        Parse.PrintTree(c.treeNode, 0);
                    }
                    System.out.println();
                    writer.println();
                }
                System.out.println();
                writer.println();
            }


    }


    @Override
    public String toString() {
        String str = id + ":";

        if(error){
            str += "\n\tCELL ERROR: " + errorMessage + "\n";
        }
        if(type != CellType.BLANK) {
            str += "\n\tValue: " + display;
            if(numberType == NumberType.INTEGER){
                str += " (int) ";
            }else if(numberType == NumberType.DOUBLE){
                str += " (double) ";
            }
            str += "\n\tControllers: {";
            for (Cell c : controllers) {
                str += c.id + " ";
            }
            str += "}\n\tUsers: {";
            for (Cell c : users) {
                str += c.id + " ";
            }
            str += "}\n";
        }else{
            str += " EMPTY";
        }


        return str;
    }


    public enum CellType {
        TEXT, NUMBER, EXPRESSION, BLANK
    }



    public enum Operation {
        NONE, ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    public enum NumberType{
        DOUBLE, INTEGER, NONE
    }


}
