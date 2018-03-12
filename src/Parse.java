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
    static private PrintWriter writer;

    public Parse(PrintWriter writer) {
        this.writer = writer;
    }


    void ParseTree(ArrayList<Token> tokens, Cell[][] sheet, String id){
        tokenIdx = 0;
        this.sheet = sheet;
        this.tokens = tokens;
        cell = Cell.GetCell(id, sheet);
        cell.ClearControllers();
        TreeNode treeNode;
        if(tokens.isEmpty()){System.out.println("No tokens for parse tree.");return;}
        token = tokens.get(0);
        treeNode = Exp();
        cell.SetEXPCell(treeNode);
    }


    static ReturnTreeStruct TraverseTree(TreeNode node){

        if(node == null){return new ReturnTreeStruct();}
        ReturnTreeStruct returnTreeStruct = new ReturnTreeStruct();
        switch(node.kind){
            case IdK:
                Cell cell = Cell.GetCell(node.name,sheet);
                node.cell = cell;
                if(node.cell == null){Error();}
                //check for blank cells, set current cell to error
                if( node.cell.getType() == Cell.CellType.BLANK){
                    throw new java.lang.RuntimeException("Blank");
                }
                if( node.cell.getType() == Cell.CellType.TEXT){
                    throw new java.lang.RuntimeException("Text");
                }

                if( node.cell.numberType == Cell.NumberType.DOUBLE){
                    returnTreeStruct.isInt = false;
                    returnTreeStruct.value = node.cell.doubleValue;
                }else{
                    returnTreeStruct.value = (double) node.cell.value;
                }

                return returnTreeStruct;
            case ConstK:
                if( node.cell.numberType == Cell.NumberType.DOUBLE){
                    returnTreeStruct.isInt = false;
                    returnTreeStruct.value = node.cell.doubleValue;
                }else{
                    returnTreeStruct.value = (double) node.cell.value;
                }

                return returnTreeStruct;
            case OpK:



                Cell.NumberType type = Cell.NumberType.NONE;

                Cell.NumberType child0Type = node.child[0].cell.GetNumberType();
                Cell.NumberType child1Type = node.child[1].cell.GetNumberType();

                if((child0Type == Cell.NumberType.DOUBLE) && (child1Type == Cell.NumberType.DOUBLE)){
                    node.cell.SetNumberType(Cell.NumberType.DOUBLE);
                    type = Cell.NumberType.DOUBLE;
                }else if(( child0Type == Cell.NumberType.DOUBLE) && (child1Type == Cell.NumberType.INTEGER)){
                    type = Cell.NumberType.DOUBLE;
                    node.child[1].cell.SetNumberType(Cell.NumberType.DOUBLE);
                    node.child[1].cell.setValue((double)( node.child[1].cell.value));
                    node.cell.SetNumberType(Cell.NumberType.DOUBLE);
                }else if(( child0Type == Cell.NumberType.INTEGER) && (child1Type == Cell.NumberType.DOUBLE)){
                    type = Cell.NumberType.DOUBLE;
                    node.child[0].cell.SetNumberType(Cell.NumberType.DOUBLE);
                    node.child[0].cell.setValue((double)( node.child[0].cell.value));
                    node.cell.SetNumberType(Cell.NumberType.DOUBLE);
                }else if(( child0Type == Cell.NumberType.INTEGER) && (child1Type == Cell.NumberType.INTEGER)){
                    type = Cell.NumberType.INTEGER;
                    node.cell.SetNumberType(Cell.NumberType.INTEGER);
                }
                
                switch (node.op){
                    case PLUS:
                        if(type == Cell.NumberType.INTEGER) {
                            node.value = TraverseTree(node.child[0]) + TraverseTree(node.child[1]);
                            return node.value;
                        }else{
                            node.doubleValue = TraverseTree(node.child[0]) + TraverseTree(node.child[1]);
                            return node.doubleValue;
                        }
                    case MINUS:
                        node.value = TraverseTree(node.child[0]) - TraverseTree(node.child[1]);
                        return node.value;
                    case TIMES:
                        node.value = TraverseTree(node.child[0]) * TraverseTree(node.child[1]);
                        return node.value;
                    case DIVIDE:
                        node.value = TraverseTree(node.child[0]) / TraverseTree(node.child[1]);
                        return node.value;
                }
                break;
        }
        return -1;

    }

    static void PrintTree(TreeNode node, int level){
        if(node == null){ return;}
        //visit node, print, then left node, then right
        node.lineno = level;
        System.out.println(node.toString());
        writer.println(node.toString());
        if(node.HasChildren()){level++;}
        PrintTree(node.child[0], level);
        PrintTree(node.child[1], level);
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
                Match(Token.TokenType.NUM);
                break;
            case ID:
                treeNode = new TreeNode(TreeNode.ExpKind.IdK);
                treeNode.name = token.id;
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
