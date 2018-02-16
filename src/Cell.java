/**
 * Created by Erin on 2/16/2018.
 */
public class Cell {

    private String input;
    private Boolean error = false;
    private AsciiTable.Token_Type type;
    private int row;
    private int column;


    public Cell(String in, AsciiTable.Token_Type type, char row, int column) {
        this.input = in;
        this.type = type;
        this.row = row;
        this.column = column;
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

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

}
