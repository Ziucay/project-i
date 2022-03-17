
%{
package compiler;
import java.util.*;
%}
%token LEFT_PAREN
%token RIGHT_PAREN
%token LEFT_BRACE
%token LEFT_SQUARE_BRACE
%token RIGHT_SQUARE_BRACE 
%token RIGHT_BRACE 
%token COMMA 
%token DOT 
%token MINUS 
%token PLUS 
%token SLASH
%token STAR
%token PERCENT
%token EQUAL
%token EQUAL_EQUAL 
%token SLASH_EQUAL 
%token GREATER 
%token GREATER_EQUAL 
%token LESS 
%token LESS_EQUAL 
%token DOT_DOT 
%token IDENTIFIER
%token SEMICOLON 
%token COLON 
%token TYPE_BOOLEAN 
%token TYPE_INTEGER 
%token TYPE_REAL 
%token TYPE_RECORD 
%token TYPE_ARRAY
%token TYPE_STRING
%token AND
%token ARRAY
%token BOOLEAN
%token ELSE
%token END
%token FALSE
%token FOR
%token IF
%token IN
%token INTEGER
%token IS
%token LOOP
%token NOT
%token OR
%token PRINT
%token REAL
%token RECORD
%token RETURN
%token REVERSE
%token ROUTINE
%token THEN
%token TRUE
%token TYPE
%token VAR
%token WHILE
%token XOR
%token STRING
%token WALRUS
%token EOF
%%
Program
	: Lines EOF
	| 
	;

Lines   : Line Lines
	| Line
	| 
	;

Line    : VariableDeclaration {root.descendants.add($1.obj);}
	| RoutineDeclaration {root.descendants.add($1.obj);}
	;

VariableDeclaration
	: VAR ModifiablePrimary COLON Type {$$ = new ParserVal(new Node("variable-declaration", null, Arrays.asList($2.obj, $4.obj)));}
	| VAR ModifiablePrimary COLON Type IS Expression {$$ = new ParserVal(new Node("variable-declaration", null, Arrays.asList($2.obj, $4.obj, $6.obj)));}
	| VAR ModifiablePrimary IS Expression {$$ = new ParserVal(new Node("variable-declaration", null, Arrays.asList($2.obj, $4.obj)));}
	;

RoutineDeclaration
	: RoutineKeyword ModifiablePrimary LEFT_PAREN Parameters RIGHT_PAREN IS Body END {$$ = new ParserVal(new Node("routine-declaration", null, Arrays.asList($2.obj, $4.obj, $7.obj)));}
	| RoutineKeyword ModifiablePrimary LEFT_PAREN Parameters RIGHT_PAREN COLON Type IS Body END {$$ = new ParserVal(new Node("routine-declaration", null, Arrays.asList($2.obj, $4.obj,$7.obj,$9.obj)));}
	;

Parameters
	: ParameterDeclaration {$$ = $1;}
	| ParameterDeclaration COMMA Parameters {((Node)$3.obj).descendants.add($1.obj); $$ = $3;}
	| {$$ = new ParserVal(new Node("parameters", null));}
	;

ParameterDeclaration
	: ModifiablePrimary COLON Type {$$ = new ParserVal(new Node("parameter-declaration", null, Arrays.asList($1.obj, $3.obj)));}
	;

Type
	: INTEGER {$$ = new ParserVal(new Node("type-integer", null));}
	| REAL {$$ = new ParserVal(new Node("type-real", null));}
	| BOOLEAN {$$ = new ParserVal(new Node("type-boolean", null));}
	;

Body
	: Statements {$$ = new ParserVal(new Node("body", null, blockStack.peek())); blockStack.pop();}
	;

Statements : Statement Statements
	   | 
	   ;

Statement 
	: Assignment {$$ = $1; blockStack.peek().add($1.obj);}
	| RoutineCall {$$ = $1; blockStack.peek().add($1.obj);}
	| WhileLoop {$$ = $1; blockStack.peek().add($1.obj);}
	| ForLoop {$$ = $1; blockStack.peek().add($1.obj);}
	| IfStatement {$$ = $1; blockStack.peek().add($1.obj);}
	| VariableDeclaration {$$ = $1; blockStack.peek().add($1.obj);}
	;

Assignment
	: ModifiablePrimary WALRUS Expression {$$ = new ParserVal(new Node("assignment", null, Arrays.asList($1.obj, $3.obj)));}
	;

RoutineCall
	: ModifiablePrimary LEFT_PAREN Expressions RIGHT_PAREN {$$ = new ParserVal(new Node("routine-call", null, Arrays.asList($1.obj, $3.obj)));}

Expressions
	: Expression {$$ = $1;}
	| Expression COMMA Expressions {((Node)$3.obj).descendants.add($1.obj); $$ = $3;}
	;

WhileLoop
	: WhileKeyword Expression LOOP Body END {$$ = new ParserVal(new Node("while", null, Arrays.asList($2.obj, $4.obj)));}
	;

WhileKeyword: WHILE { blockStack.push(new LinkedList<>());}
ForKeyword: FOR { blockStack.push(new LinkedList<>());}
IfKeyword: IF { blockStack.push(new LinkedList<>());}
ElseKeyword: ELSE { blockStack.push(new LinkedList<>());}
RoutineKeyword: ROUTINE { blockStack.push(new LinkedList<>());}

ForLoop
	: ForKeyword ModifiablePrimary Range LOOP Body END {$$ = new ParserVal(new Node("for", null, Arrays.asList($2.obj, $3.obj, $5.obj)));}
	;

Range
	: IN Expression DOT_DOT Expression {$$ = new ParserVal(new Node("range", null, Arrays.asList($2.obj, $4.obj)));}
	| IN REVERSE Expression DOT_DOT Expression {$$ = new ParserVal(new Node("range-reverse", null, Arrays.asList($3.obj, $5.obj)));}
	;

IfStatement
	: IfKeyword Expression THEN Body END {$$ = new ParserVal(new Node("if", null, Arrays.asList($2.obj, $4.obj)));}
	| IfKeyword Expression THEN Body ElseKeyword Body END {$$ = new ParserVal(new Node("if-else", null, Arrays.asList($2.obj, $4.obj, $6.obj)));}
	;

Expression
	: Relation AND Expression {$$ = new ParserVal(new Node("and", null, Arrays.asList($1.obj, $3.obj)));}
	| Relation OR Expression {$$ = new ParserVal(new Node("or", null, Arrays.asList($1.obj, $3.obj)));}
	| Relation XOR Expression {$$ = new ParserVal(new Node("xor", null, Arrays.asList($1.obj, $3.obj)));}
	| Relation {$$ = $1;}
	; 

Relation
	: Simple LESS Relation {$$ = new ParserVal(new Node("less", null, Arrays.asList($1.obj, $3.obj)));}
	| Simple LESS_EQUAL Relation {$$ = new ParserVal(new Node("less or equal", null, Arrays.asList($1.obj, $3.obj)));}
	| Simple GREATER Relation {$$ = new ParserVal(new Node("more", null, Arrays.asList($1.obj, $3.obj)));}
	| Simple GREATER_EQUAL Relation {$$ = new ParserVal(new Node("more or equal", null, Arrays.asList($1.obj, $3.obj)));}
	| Simple EQUAL Relation {$$ = new ParserVal(new Node("equal", null, Arrays.asList($1.obj, $3.obj)));}
	| Simple SLASH_EQUAL Relation {$$ = new ParserVal(new Node("not equal", null, Arrays.asList($1.obj, $3.obj)));}
	| Simple {$$ = $1;}
	; 

Simple
	: Factor STAR Simple {$$ = new ParserVal(new Node("multiply", null, Arrays.asList($1.obj, $3.obj)));}
	| Factor SLASH Simple {$$ = new ParserVal(new Node("divide", null, Arrays.asList($1.obj, $3.obj)));}
	| Factor PERCENT Simple {$$ = new ParserVal(new Node("percent", null, Arrays.asList($1.obj, $3.obj)));}
	| Factor {$$ = $1;}
	; 

Factor
	: Summand PLUS Factor {$$ = new ParserVal(new Node("plus", null, Arrays.asList($1.obj, $3.obj)));}
	| Summand MINUS Factor {$$ = new ParserVal(new Node("minus", null, Arrays.asList($1.obj, $3.obj)));}
	| Summand {$$ = $1;}
	; 

Summand : Primary {$$ = $1;}
	| LEFT_PAREN Expression RIGHT_PAREN {$$ = new ParserVal(new Node("summand", null, Arrays.asList($2.obj)));}
	; 

Primary
	: TYPE_REAL {$$ = new ParserVal(new Node(yylval.dval.toString(), Double.valueOf($1.dval)));}
	| TYPE_INTEGER {$$ = new ParserVal(new Node(yylval.ival.toString(), Integer.valueOf($1.ival)));}
	| TRUE {$$ = new ParserVal(new Node("true", Boolean.valueOf(true)));}
	| FALSE {$$ = new ParserVal(new Node("false", Boolean.valueOf(false)));}
	| ModifiablePrimary {$$ = new ParserVal(new Node("modifiable", null, Arrays.asList($1.obj)));}
	;

ModifiablePrimary : IDENTIFIER {$$ = new ParserVal(new Node(yylval.sval, null));}
		  ;

%%
    Stack<List<Object>> blockStack = new Stack<>();

    List<Token> tokens;
    int tokenPointer = 0;
    Node root = new Node("root", null, new LinkedList<>());


    void setTokens(List<Token> tokens) {
        this.tokens = tokens;
        tokenPointer = 0;
    }

private void yyerror(String syntax_error) {
	System.out.println("Error: " + syntax_error);
}

    private int yylex() {
        if (tokenPointer == tokens.size())
            return -1;
        Token token = tokens.get(tokenPointer);
        switch (token.type) {
            case TYPE_INTEGER -> this.yylval = new ParserVal(Integer.valueOf(token.lexeme).intValue());
            case TYPE_REAL -> this.yylval = new ParserVal(Double.valueOf(token.lexeme).doubleValue());
            case TYPE_BOOLEAN -> this.yylval = new ParserVal(Boolean.valueOf(token.lexeme).booleanValue());
            case IDENTIFIER -> this.yylval = new ParserVal(token.lexeme);
            default -> this.yylval = new ParserVal(token.lexeme);
        }
        tokenPointer++;
        return tokens.get(tokenPointer - 1).TokenTypeToInt();
    }