/**
 * Created by Erin on 2/16/2018.
 */
public class Cell {

    private String input;
    private Boolean error = false;
    private AsciiTable.Token_Type type;
    private int rowInt;
    private char rowChar;
    private int column;

    public Cell() {}

    public Cell(String in, AsciiTable.Token_Type type, char rowChar, int column) {
        this.input = in;
        this.type = type;
        this.rowChar = rowChar;
        this.column = column;
    }

    public Cell(String in, AsciiTable.Token_Type type) {
        this.input = in;
        this.type = type;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public Boolean isError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public AsciiTable.Token_Type getType() {
        return type;
    }

    public void setType(AsciiTable.Token_Type type) {
        this.type = type;
    }

    public char getRow() {
        return rowChar;
    }

    public int getRowInt() {
        return rowInt;
    }

    public void setRow(char row) {
        this.rowChar = row;
        this.rowInt = (int) AsciiTable.Row.get(row);
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

}
