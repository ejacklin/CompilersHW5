import java.io.PrintWriter;
import java.util.ArrayList;

import static javafx.scene.input.KeyCode.DIVIDE;
import static javafx.scene.input.KeyCode.MULTIPLY;
import static javax.management.Query.TIMES;

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
                cell = Cell.GetCell(node.name,sheet);
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
                    node.numberType = Cell.NumberType.DOUBLE;
                    node.doubleValue = node.cell.doubleValue;
                }else{
                    node.numberType = Cell.NumberType.INTEGER;
                    returnTreeStruct.isInt = true;
                    node.intValue = node.cell.value;
                    returnTreeStruct.value = (double) node.cell.value;
                }
                return returnTreeStruct;

            case ConstK:
                if( node.cell.numberType == Cell.NumberType.DOUBLE){
                    returnTreeStruct.isInt = false;
                    returnTreeStruct.value = node.cell.doubleValue;
                    node.doubleValue = node.cell.doubleValue;
                }else{
                    returnTreeStruct.value = (double) node.cell.value;
                    node.intValue = node.cell.value;
                }
                return returnTreeStruct;

            case OpK:
                ReturnTreeStruct returnTreeStruct1 = TraverseTree(node.child[0]);
                ReturnTreeStruct returnTreeStruct2 = TraverseTree(node.child[1]);
                switch (node.op){
                    case PLUS:
                        if(!returnTreeStruct1.isInt || !returnTreeStruct2.isInt){
                        returnTreeStruct1.isInt = false;
                        returnTreeStruct2.isInt = false;
                        returnTreeStruct.value = ReturnTreeStruct.Add((double)returnTreeStruct1.value, (double)returnTreeStruct2.value);
                        returnTreeStruct.isInt = false;
                        node.doubleValue = returnTreeStruct.value;
                        node.numberType = Cell.NumberType.DOUBLE;
                    }else{
                        node.intValue = ReturnTreeStruct.Add((int)returnTreeStruct1.value, (int)returnTreeStruct2.value);
                        returnTreeStruct.value = node.intValue;
                        node.numberType = Cell.NumberType.INTEGER;
                    }
                        return returnTreeStruct;

                    case MINUS:
                        if(!returnTreeStruct1.isInt || !returnTreeStruct2.isInt){
                            returnTreeStruct1.isInt = false;
                            returnTreeStruct2.isInt = false;
                            returnTreeStruct.isInt = false;
                            returnTreeStruct.value = ReturnTreeStruct.Sub((double)returnTreeStruct1.value, (double)returnTreeStruct2.value);
                            node.doubleValue = returnTreeStruct.value;
                            node.numberType = Cell.NumberType.DOUBLE;
                        }else{
                            node.intValue = ReturnTreeStruct.Sub((int)returnTreeStruct1.value, (int)returnTreeStruct2.value);
                            returnTreeStruct.value = node.intValue;
                            node.numberType = Cell.NumberType.INTEGER;
                        }
                        return returnTreeStruct;
                    case TIMES:
                        if(!returnTreeStruct1.isInt || !returnTreeStruct2.isInt){
                            returnTreeStruct1.isInt = false;
                            returnTreeStruct2.isInt = false;
                            returnTreeStruct.isInt = false;
                            returnTreeStruct.value = ReturnTreeStruct.Multiply((double)returnTreeStruct1.value, (double)returnTreeStruct2.value);
                            node.doubleValue = returnTreeStruct.value;
                            node.numberType = Cell.NumberType.DOUBLE;
                       }else{
                           node.intValue = ReturnTreeStruct.Multiply((int)returnTreeStruct1.value, (int)returnTreeStruct2.value);
                           returnTreeStruct.value = node.intValue;
                           node.numberType = Cell.NumberType.INTEGER;
                       }
                        return returnTreeStruct;
                    case DIVIDE:
                        if(!returnTreeStruct1.isInt || !returnTreeStruct2.isInt){
                            returnTreeStruct1.isInt = false;
                            returnTreeStruct2.isInt = false;
                            returnTreeStruct.isInt = false;
                            returnTreeStruct.value = ReturnTreeStruct.Divide((double)returnTreeStruct1.value, (double)returnTreeStruct2.value);
                            node.doubleValue = returnTreeStruct.value;
                            node.numberType = Cell.NumberType.DOUBLE;
                        }else{
                            node.intValue = ReturnTreeStruct.Divide((int)returnTreeStruct1.value, (int)returnTreeStruct2.value);
                            returnTreeStruct.value = node.intValue;
                            node.numberType = Cell.NumberType.INTEGER;
                        }
                        return returnTreeStruct;

                }
                break;
        }
        return null;

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
