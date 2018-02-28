import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Erin on 2/23/2018.
 */
public class Parse {

    static Token token;
    static Cell cell;
    static int tokenIdx = 0;
    ArrayList<Token> tokens;
    static Cell[][] sheet;
    static int indents = 4;



    void Parse(ArrayList<Token> tokens, Cell[][] sheet, String id){
        tokenIdx = 0;
        this.sheet = sheet;
        this.tokens = tokens;
        cell = Cell.GetCell(id, sheet);
        cell.ClearControllers();
        TreeNode treeNode;
        token = tokens.get(0);
        treeNode = Exp();
        cell.SetEXPCell(treeNode);
    }

    static int TraverseTree(TreeNode node){

        if(node == null){ return -1;}

        switch(node.kind){
            case IdK:
                Cell cell = Cell.GetCell(node.name,sheet);
                node.cell = cell;
                if(node.cell == null){Error();}
                //check for blank cells, set current cell to error
                if( node.cell.getType() == Cell.CellType.BLANK){
                    throw new java.lang.RuntimeException("blank cell!");
                }
                return node.cell.getValue();
            case ConstK:
                return node.value;
            case OpK:
                switch (node.op){
                    case PLUS:
                        return TraverseTree(node.child[0]) + TraverseTree(node.child[1]);
                    case MINUS:
                        return TraverseTree(node.child[0]) - TraverseTree(node.child[1]);
                    case TIMES:
//                        System.out.print(" " + "*" + " ");
                        return TraverseTree(node.child[0]) * TraverseTree(node.child[1]);
                    case DIVIDE:
//                        System.out.print(" " + "/" + " ");
                        return TraverseTree(node.child[0]) / TraverseTree(node.child[1]);
                    case LPAREN:
//                        System.out.print(" " + "(" + " ");
                        break;
                    case RPAREN:
//                        System.out.print(" " + ")" + " ");
                        break;
                }
                break;
        }
        return -1;

    }

    static void PrintTree(TreeNode node){
        if(node == null){ return;}

        switch(node.kind){
            case IdK:
                System.out.println(node.toString());
                break;
            case ConstK:
                System.out.println(node.toString());
                break;
            case OpK:
                System.out.println(node.toString());
                switch (node.op){
                    case PLUS:
                        PrintTree(node.child[0]);
                        PrintTree(node.child[1]);
                        break;
                    case MINUS:
                        System.out.println(node.toString());
                       PrintTree(node.child[0]);
                        PrintTree(node.child[1]);
                        break;
                    case TIMES:
                        PrintTree(node.child[0]);
                        PrintTree(node.child[1]);
                        break;
                    case DIVIDE:
                        PrintTree(node.child[0]);
                        PrintTree(node.child[1]);
                        break;
                    case LPAREN:
                        System.out.println(" " + "(" + " ");
                        break;
                    case RPAREN:
                        System.out.print(" " + ")" + " ");
                        break;
                }
                break;
        }
    }

    void Match(Token.TokenType expected){
        if (expected == token.tokenType){
            GetNextToken();
        }else{
            Error();
        }
    }

    private void GetNextToken() {
        tokenIdx++;
        if(tokenIdx < tokens.size()){
            token = tokens.get(tokenIdx);
        }
    }

    static private void Error() {
        System.out.println("ERROR");
        System.exit(1);
    }

    public TreeNode Exp(){
        TreeNode t = Term();
        while((token.tokenType == Token.TokenType.PLUS) ||(token.tokenType == Token.TokenType.MINUS)){
            TreeNode p = new TreeNode(TreeNode.ExpKind.OpK);
            p.child[0] = t;
            p.op = token.tokenType;
            t = p;
            Match(token.tokenType);
            t.child[1] = Term();
        }
        return t;
    }

    public TreeNode Term(){
        TreeNode t = Factor();
        while((token.tokenType == Token.TokenType.TIMES) ||(token.tokenType == Token.TokenType.DIVIDE)){
            TreeNode p = new TreeNode(TreeNode.ExpKind.OpK);
            p.lineno = indents;
            p.child[0] = t;
            p.op = token.tokenType;
            t = p;
            Match(token.tokenType);
            p.child[1] = Factor();
        }
        return t;
    }

    public TreeNode Factor(){
        TreeNode treeNode = null;
        switch(token.tokenType){
            case NUM:
                treeNode = new TreeNode(TreeNode.ExpKind.ConstK);
                treeNode.value = token.value;
                treeNode.lineno = 2 * indents;
                Match(Token.TokenType.NUM);
                break;
            case ID:
                treeNode = new TreeNode(TreeNode.ExpKind.IdK);
                treeNode.name = token.id;
                treeNode.lineno = 2 * indents;
                Cell controllerCell = Cell.GetCell(treeNode.name,sheet);
                treeNode.cell = controllerCell;
                cell.AddController(controllerCell);
                controllerCell.AddUser(cell);
                Match(Token.TokenType.ID);
                break;
            case LPAREN:
                Match(Token.TokenType.LPAREN);
                treeNode = Exp();
                Match(Token.TokenType.RPAREN);
                break;
            default:
                System.out.println("Unexpected token in Factor");
                break;
        }
        return treeNode;
    }
}
