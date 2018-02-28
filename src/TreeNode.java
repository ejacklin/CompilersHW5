
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



    TreeNode(ExpKind kind){
        this.kind = kind;
        this.child = new TreeNode[MAX_CHILDREN];
    }

    @Override
    public String toString() {
        String line = "";
        String buff = "        ";
        if(lineno != 0) {
            line += buff.substring(0,lineno);
        }
        switch (kind){
            case OpK:
                line +="TYPE: ";
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
                break;
            case ConstK:
                line += "VALUE: " + Integer.toString(value);
                break;
            case IdK:
                line += "VALUE: " + Integer.toString(cell.getValue());
                break;

        }
        return line;
    }

    enum ExpKind {OpK, ConstK, IdK}

}
