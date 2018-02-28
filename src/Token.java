/**
 * Created by Erin on 2/24/2018.
 */
public class Token {

//    public TreeNode.ExpKind kind = null;
    public TokenType tokenType = null;
    public Integer value = null;
    public String id = null;
    public Cell.Operation operation;


    public Token(TokenType tokenType, Integer value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    public Token(TokenType tokenType, String id) {
        this.tokenType = tokenType;
        this.id = id;
    }

    public Token(TokenType tokenType, Cell.Operation operation) {
        this.tokenType = tokenType;
        this.operation = operation;
    }


    enum TokenType{ERROR, ID, NUM, PLUS, MINUS, TIMES, DIVIDE, LPAREN, RPAREN}

}
