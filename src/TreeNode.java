
/**
 * Created by Erin on 2/23/2018.
 */
public class TreeNode {
    private int MAX_CHILDREN = 3;
    public TreeNode[] child;
    public Token.TokenType op;
    public String name;
    public double doubleValue;
    public int intValue;
    public int value;
    public ExpKind kind;
    public Cell cell;
    public int lineno = 0;
    private int INDENT = 4;
    public Cell.NumberType numberType;


    TreeNode(ExpKind kind){
        this.kind = kind;
        this.child = new TreeNode[MAX_CHILDREN];
    }

    @Override
    public String toString() {
        String line = "";
        String buff = "                                                  ";
        if(lineno != 0) {
            line += buff.substring(0,lineno*INDENT);
        }
        switch (kind){
            case OpK:
                line +="<OP>: ";
                switch (op){
                    case PLUS:
                        line += "PLUS";
                        break;
                    case MINUS:
                        line += "MINUS";
                        break;
                    case TIMES:
                        line += "TIMES";
                        break;
                    case DIVIDE:
                        line += "DIVIDE";
                        break;
                }

                if(numberType == Cell.NumberType.INTEGER){
                    line += "\t <VALUE>: " + Integer.toString(intValue);
                    line += " (int) ";
                }else if(numberType == Cell.NumberType.DOUBLE){
                    line += "\t <VALUE>: " + Double.toString(doubleValue);
                    line += " (double) ";
                }
                break;
            case ConstK:
                if(numberType == Cell.NumberType.INTEGER){
                    line += "<VALUE>: " + Integer.toString(intValue) + "<TYPE>: Integer";
                }else{
                    line += "<VALUE>: " + Double.toString(doubleValue) + "<TYPE>: Double";
                }

                break;
            case IdK:

                if(numberType == Cell.NumberType.INTEGER){
                    line += "<VALUE>: " + cell.getId() + " = "+ Integer.toString(cell.value);
                    line += " (int) ";
                }else if(numberType == Cell.NumberType.DOUBLE){
                    line += "<VALUE>: " + cell.getId() + " = "+ Double.toString(cell.doubleValue);
                    line += " (double) ";
                }
                break;

        }
        return line;
    }

    enum ExpKind {OpK, ConstK, IdK}

    public boolean HasChildren(){
        return (child[0] != null) || (child[1] != null);
    }
}
