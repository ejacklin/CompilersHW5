
/**
 * Created by Erin on 2/23/2018.
 */
public class TreeNode {
    private int MAX_CHILDREN = 3;
    public TreeNode[] child;
    public Token.TokenType op;
    public String name;
    public int value;
    public ExpKind kind;
    public Cell cell;
    public int lineno = 0;
    private int INDENT = 4;



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
                line += "\t <VALUE>: " + Integer.toString(value);
                break;
            case ConstK:
                String t;
                if(cell.GetNumberType() == Cell.NumberType.INTEGER){t = "Integer";}else{t = "Double";}
                line += "<VALUE>: " + Integer.toString(value) + "<TYPE>: " + t;
                break;
            case IdK:
                line += "<VALUE>: " + cell.getId() + " = "+ Integer.toString(cell.getValue());
                break;

        }
        return line;
    }

    enum ExpKind {OpK, ConstK, IdK}

    public boolean HasChildren(){
        return (child[0] != null) || (child[1] != null);
    }
}
