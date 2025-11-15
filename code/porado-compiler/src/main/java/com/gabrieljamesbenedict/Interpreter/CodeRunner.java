package com.gabrieljamesbenedict.Interpreter;

import com.gabrieljamesbenedict.Interpreter.Symbol;
import com.gabrieljamesbenedict.Interpreter.SymbolTable;
import com.gabrieljamesbenedict.SyntaxAnalysis.AbstractSyntaxTree;
import com.gabrieljamesbenedict.SyntaxAnalysis.Node;
import com.gabrieljamesbenedict.SyntaxAnalysis.NodeType;
import lombok.Data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class CodeRunner {

    public final AbstractSyntaxTree ast;
    public final SymbolTable symbolTable = new SymbolTable();

    private final Deque<SymbolTable> scopeStack = new ArrayDeque<>();


    public void run() {
        scopeStack.clear();
        scopeStack.push(new SymbolTable());
        visitProgram(ast.getRoot());
    }


    private static class ReturnValue extends RuntimeException {
        final Object value;
        public ReturnValue(Object value) { this.value = value; }
    }

    private static class BreakSignal extends RuntimeException {}
    private static class ContinueSignal extends RuntimeException {}


    private void visitProgram(Node program) {
        if (program == null) return;
        for (Node child : program.getChildren()) {
            execStatement(child);
        }
    }


    private void execStatement(Node node) {
        if (node == null) return;

        switch (node.getType()) {

            case EMPTY_STATEMENT:
                return;

            case BLOCK_STATEMENT:
                executeBlock(node);
                return;

            case VARIABLE_DECLARATION:
                execVariableDeclaration(node);
                return;

            case ARRAY_DECLARATION:
                execArrayDeclaration(node);
                return;

            case FUNCTION_DECLARATION:
                execFunctionDeclaration(node);
                return;

            case PRINT:
                Node printExpr = node.getChildren().getFirst();
                Object value = evalExpression(printExpr);
                System.out.println(stringify(value));
                break;

            case ASSIGNMENT:
            case ADDITION_ASSIGNMENT:
            case SUBTRACTION_ASSIGNMENT:
            case MULTIPLICATION_ASSIGNMENT:
            case DIVISION_ASSIGNMENT:
            case MODULO_ASSIGNMENT:
                execAssignment(node);
                return;

            case RETURN_STATEMENT:
                if (!node.getChildren().isEmpty() && node.getChildren().getFirst().getType() != NodeType.NO_RETURN) {
                    Object r = evalExpression(node.getChildren().getFirst());
                    throw new ReturnValue(r);
                } else {
                    throw new ReturnValue(null);
                }

            case BREAK:
                throw new BreakSignal();

            case CONTINUE:
                throw new ContinueSignal();

            case CONDITIONAL:
                execConditional(node);
                return;

            case SWITCH:
                execSwitch(node);
                return;

            case LOOP:
                execLoop(node);
                return;

            case EOF:
                return;


            default:
                if (isExpressionNode(node.getType())) {
                    evalExpression(node);
                    return;
                }

                throw new RuntimeException("Unhandled statement node type: " + node.getType());
        }
    }


    private Object eval(Node node) {
        switch (node.getType()) {

            case LITERAL_INT -> {
                return Integer.parseInt(node.getText());
            }
            case LITERAL_FLOAT -> {
                return Float.parseFloat(node.getText());
            }
            case LITERAL_STRING -> {
                return node.getText();//.replace("\"","");
            }
            case LITERAL_CHAR -> {
                return node.getText();//.replace("''","");
            }
            case LITERAL_TRUE -> {
                return true;
            }
            case LITERAL_FALSE -> {
                return false;
            }
            case VARIABLE_ACCESS -> {
                Node varNameNode = node.getChildById(NodeType.VARIABLE_NAME);
                Symbol symbol = symbolTable.lookup(varNameNode.getText());
                if (symbol == null) throw new RuntimeException("Undefined variable: " + varNameNode.getText());
                return symbol.getData();
            }
            case ARRAY_ACCESS -> {
                Node arrayNameNode = node.getChildById(NodeType.ARRAY_NAME);
                Symbol arraySymbol = symbolTable.lookup(arrayNameNode.getText());
                if (arraySymbol == null) throw new RuntimeException("Undefined array: " + arrayNameNode.getText());
                Object[] array = (Object[]) arraySymbol.getData();
                int index = (int) eval(node.getChildById(NodeType.LITERAL_INT));
                return array[index];
            }
            // Add more operators here (ADDITION, SUBTRACTION, etc.)
            default -> throw new RuntimeException("Cannot evaluate node type: " + node.getType());
        }
    }



    private void executeBlock(Node block) {
        SymbolTable blockScope = new SymbolTable();
        scopeStack.push(blockScope);
        try {
            for (Node child : block.getChildren()) {
                execStatement(child);
            }
        } catch (BreakSignal | ContinueSignal bs) {
            throw bs;
        } finally {
            scopeStack.pop();
        }
    }

    private void execVariableDeclaration(Node decl) {
        // Expect first child to be VARIABLE_NAME (text set)
        Node nameNode = decl.getChildById(NodeType.VARIABLE_NAME);
        String name = nameNode.getText();
        Object initial = null;
        if (decl.getChildren().stream().anyMatch(n -> n.getType() == NodeType.VARIABLE_BODY)) {
            Node body = decl.getChildById(NodeType.VARIABLE_BODY);
            if (!body.getChildren().isEmpty()) {
                initial = evalExpression(body.getChildren().getFirst());
            }
        }
        Symbol sym = Symbol.builder().name(name).data(initial).build();
        currentScope().add(sym);
    }

    private void execArrayDeclaration(Node decl) {
        Node arrNameNode = decl.getChildById(NodeType.ARRAY_NAME);
        if (arrNameNode == null) throw new RuntimeException("Array must have a name");
        String name = arrNameNode.getText();

        List<Object> arr = new ArrayList<>();

        Node body = decl.getChildById(NodeType.ARRAY_BODY);
        if (body != null && !body.getChildren().isEmpty()) {
            Node arrLiteral = body.getChildren().getFirst();
            if (arrLiteral.getType() == NodeType.LITERAL_ARRAY) {
                for (Node el : arrLiteral.getChildren()) {
                    arr.add(parseLiteralNode(el));
                }
            } else {
                // maybe copying another array
                Symbol other = lookupSymbol(arrLiteral.getText());
                if (other == null) throw new RuntimeException("Unknown array: " + arrLiteral.getText());
                Object data = other.getData();
                if (!(data instanceof List)) throw new RuntimeException("Not an array: " + arrLiteral.getText());
                arr = new ArrayList<>((List<Object>) data);
            }
        }

        Symbol sym = Symbol.builder().name(name).data(arr).build();
        currentScope().add(sym);
    }



    private void execFunctionDeclaration(Node decl) {
        Node nameNode = decl.getChildById(NodeType.FUNCTION_NAME);
        String name = nameNode.getText();

        Symbol sym = Symbol.builder().name(name).data(decl).build();
        symbolTable.add(sym);
    }


    private void execAssignment(Node node) {
        Node left = node.getChildren().getFirst();
        Node right = node.getChildren().get(1);
        Object rVal = evalExpression(right);

        switch (node.getType()) {
            case ASSIGNMENT:
                assignToTarget(left, rVal);
                break;
            case ADDITION_ASSIGNMENT:
                assignToTarget(left, arithmeticOpOnTarget(left, rVal, NodeType.ADDITION));
                break;
            case SUBTRACTION_ASSIGNMENT:
                assignToTarget(left, arithmeticOpOnTarget(left, rVal, NodeType.SUBTRACTION));
                break;
            case MULTIPLICATION_ASSIGNMENT:
                assignToTarget(left, arithmeticOpOnTarget(left, rVal, NodeType.MULTIPLICATION));
                break;
            case DIVISION_ASSIGNMENT:
                assignToTarget(left, arithmeticOpOnTarget(left, rVal, NodeType.DIVISION));
                break;
            case MODULO_ASSIGNMENT:
                assignToTarget(left, arithmeticOpOnTarget(left, rVal, NodeType.MODULO));
                break;
            default:
                throw new RuntimeException("Unknown assignment type: " + node.getType());
        }
    }

    private Object arithmeticOpOnTarget(Node left, Object rVal, NodeType op) {
        Object currentVal = loadFromTarget(left);
        Node temp = new Node();
        temp.setType(op);
        temp.addChild(makeLiteralFromObject(currentVal));
        temp.addChild(makeLiteralFromObject(rVal));
        return evalExpression(temp);
    }

    private void assignToTarget(Node target, Object value) {
        if (target.getType() == NodeType.VARIABLE_ACCESS) {
            String name = target.getText();
            Symbol s = lookupSymbol(name);
            if (s == null) {
                s = Symbol.builder().name(name).data(value).build();
                symbolTable.add(s);
            } else {
                s.setData(value);
            }
        } else if (target.getType() == NodeType.ARRAY_ACCESS) {
            String name = target.getText();
            Symbol s = lookupSymbol(name);
            if (s == null) throw new RuntimeException("Unknown array: " + name);
            Object data = s.getData();
            if (!(data instanceof List)) throw new RuntimeException(name + " is not an array");
            List<Object> arr = (List<Object>) data;

            if (target.getChildren().isEmpty())
                throw new RuntimeException("Array assignment missing index: " + name);

            Node idxNode = target.getChildren().getFirst();
            int idx = (int) toLong(evalExpression(idxNode));

            if (idx < 0 || idx >= arr.size())
                throw new RuntimeException("Array index out of bounds: " + idx);

            arr.set(idx, value);
        } else {
            throw new RuntimeException("Unsupported assignment target: " + target.getType());
        }
    }


    // read the current value of a target (variable or array access)
    private Object loadFromTarget(Node target) {
        if (target.getType() == NodeType.VARIABLE_ACCESS) {
            String name = target.getText();
            Symbol s = lookupSymbol(name);
            if (s == null) throw new RuntimeException("Unknown variable: " + name);
            return s.getData();
        } else if (target.getType() == NodeType.ARRAY_ACCESS) {
            String name = target.getText();
            Symbol s = lookupSymbol(name);
            if (s == null) throw new RuntimeException("Unknown array: " + name);
            Object data = s.getData();
            if (!(data instanceof List)) throw new RuntimeException(name + " is not an array");
            List<Object> arr = (List<Object>) data;
            Object idxObj = evalExpression(target.getChildren().getFirst());
            int idx = (int) toLong(idxObj);
            return arr.get(idx);
        } else {
            throw new RuntimeException("Unsupported assignment source: " + target.getType());
        }
    }

    // -----------------------
    // Conditionals & Switch
    // -----------------------

    private void execConditional(Node cond) {
        // children: IF, ELSE_IF*, ELSE?
        for (Node n : cond.getChildren()) {
            if (n.getType() == NodeType.IF) {
                Node condExpr = n.getChildById(NodeType.IF_CONDITION).getChildren().getFirst();
                boolean truth = truthy(evalExpression(condExpr));
                if (truth) {
                    execStatement(n.getChildById(NodeType.IF_BODY).getChildren().getFirst());
                    return;
                }
            } else if (n.getType() == NodeType.ELSE_IF) {
                Node condExpr = n.getChildById(NodeType.ELSE_IF_CONDITION).getChildren().getFirst();
                boolean truth = truthy(evalExpression(condExpr));
                if (truth) {
                    execStatement(n.getChildById(NodeType.ELSE_IF_BODY).getChildren().getFirst());
                    return;
                }
            } else if (n.getType() == NodeType.ELSE) {
                execStatement(n.getChildById(NodeType.ELSE_BODY).getChildren().getFirst());
                return;
            }
        }
    }

    private void execSwitch(Node switchNode) {
        // Evaluate the switch expression
        Node switchExprNode = switchNode.getChildById(NodeType.SWITCH_EXPRESSION);
        if (switchExprNode == null || switchExprNode.getChildren().isEmpty()) {
            throw new RuntimeException("Switch has no expression");
        }
        Object key = evalExpression(switchExprNode.getChildren().getFirst());

        // Get the CASES node
        Node casesNode = switchNode.getChildById(NodeType.CASES);
        if (casesNode == null) return;

        boolean ran = false;

        for (Node caseNode : casesNode.getChildren()) {
            Node caseExprNode = caseNode.getChildById(NodeType.CASE_EXPRESSION);
            if (caseExprNode == null || caseExprNode.getChildren().isEmpty()) continue;

            Object caseVal = evalExpression(caseExprNode.getChildren().getFirst());
            if (Objects.equals(caseVal, key)) {
                // Execute the case body
                Node caseBody = caseNode.getChildById(NodeType.CASE_BODY);
                if (caseBody != null) {
                    for (Node stmt : caseBody.getChildren()) {
                        if (stmt.getType() == NodeType.BLOCK_STATEMENT) {
                            execStatement(stmt); // BLOCK_STATEMENT will handle its own children
                        } else {
                            execStatement(stmt);
                        }
                    }
                }
                ran = true;
                break;// only execute the first matching case
            }
        }

        if (!ran) {
            Node defaultNode = switchNode.getChildren().stream()
                    .filter(n -> n.getType() == NodeType.DEFAULT)
                    .findFirst().orElse(null);

            if (defaultNode != null) {
                Node defaultBody = defaultNode.getChildById(NodeType.DEFAULT_BODY);
                if (defaultBody != null) {
                    for (Node stmt : defaultBody.getChildren()) {
                        if (stmt.getType() == NodeType.BLOCK_STATEMENT) {
                            execStatement(stmt);
                        } else {
                            execStatement(stmt);
                        }
                    }
                }
            }
        }
    }


    private void execLoop(Node loopNode) {
        // loopNode children contain nodes like WHILE, UNTIL, FOR, REPEAT, etc
        for (Node n : loopNode.getChildren()) {
            switch (n.getType()) {
                case WHILE: {
                    Node condn = n.getChildById(NodeType.LOOP_CONDITION).getChildren().getFirst();
                    Node body = n.getChildById(NodeType.LOOP_BODY).getChildren().getFirst();

                    while (truthy(evalExpression(condn))) {
                        try {
                            execStatement(body);
                        } catch (BreakSignal bs) {
                            return;
                        } catch (ContinueSignal cs) {
                        }
                    }
                    break;
                }
                case UNTIL: {
                    Node condn = n.getChildById(NodeType.LOOP_CONDITION).getChildren().getFirst();
                    Node body = n.getChildById(NodeType.LOOP_BODY).getChildren().getFirst();

                    while (!truthy(evalExpression(condn))) {
                        try {
                            execStatement(body);
                        } catch (BreakSignal bs) {
                            return;
                        } catch (ContinueSignal cs) {
                        }
                    }
                    break;
                }
                case REPEAT: {
                    Node amountNode = n.getChildById(NodeType.REPEAT_AMOUNT).getChildren().getFirst();
                    long times = toLong(evalExpression(amountNode));
                    Node body = n.getChildById(NodeType.LOOP_BODY).getChildren().getFirst();
                    for (long i = 0; i < times; i++) {
                        try {
                            execStatement(body);
                        } catch (BreakSignal bs) {
                            return;
                        } catch (ContinueSignal cs) {
                        }
                    }
                    break;
                }

                case FOR: {
                    Node body = n.getChildById(NodeType.LOOP_BODY).getChildren().getFirst();

                    String forVar = null;
                    String forArr = null;

                    for (Node ch : n.getChildren()) {
                        if (ch.getType() == NodeType.FOR_VARIABLE && ch.getText() != null) forVar = ch.getText();
                        if (ch.getType() == NodeType.FOR_ARRAY && ch.getText() != null) forArr = ch.getText();
                    }
                    if (forArr == null) {
                        throw new RuntimeException("FOR loop interpreter expected FOR_ARRAY name present on node");
                    }
                    Symbol arrSym = lookupSymbol(forArr);
                    if (arrSym == null || !(arrSym.getData() instanceof List)) throw new RuntimeException("FOR source not array: " + forArr);
                    List<Object> arr = (List<Object>) arrSym.getData();
                    for (int i = 0; i < arr.size(); i++) {
                        if (forVar != null) {
                            // set element into current scope
                            Symbol s = lookupLocalSymbol(forVar);
                            if (s == null) {
                                s = Symbol.builder().name(forVar).data(arr.get(i)).build();
                                currentScope().add(s);
                            } else {
                                s.setData(arr.get(i));
                            }
                        }
                        try {
                            execStatement(body);
                        } catch (BreakSignal bs) { break; }
                        catch (ContinueSignal cs) { continue; }
                    }
                    break;
                }
                default: break;
            }
        }
    }


    private Object evalExpression(Node node) {
        if (node == null) return null;
        switch (node.getType()) {

            case LITERAL_INT:
                return Long.parseLong(node.getText());
            case LITERAL_FLOAT:
                return Double.parseDouble(node.getText());
            case LITERAL_STRING:
                return node.getText();
            case LITERAL_CHAR:
                String txt = node.getText();
                if (txt.length() >= 3 && txt.startsWith("'") && txt.endsWith("'")) {
                    return txt.charAt(1);
                }
                throw new RuntimeException("Invalid char literal: " + txt);
            case LITERAL_TRUE:
                return Boolean.TRUE;
            case LITERAL_FALSE:
                return Boolean.FALSE;
            case LITERAL_ARRAY: {
                List<Object> arr = new ArrayList<>();
                for (Node el : node.getChildren()) arr.add(parseLiteralNode(el));
                return arr;
            }

            // Variable access
            case VARIABLE_ACCESS: {
                String name = node.getText();
                Symbol s = lookupSymbol(name);
                if (s == null) throw new RuntimeException("Undefined variable: " + name);
                return s.getData();
            }

            // Variable name
            case VARIABLE_NAME: {
                String name = node.getText();
                Symbol s = lookupSymbol(name);
                if (s == null) throw new RuntimeException("Undefined variable: " + name);
                return s.getData();
            }

            // Array access
            case ARRAY_ACCESS: {
                String name = node.getText();
                Symbol s = lookupSymbol(name);
                if (s == null) throw new RuntimeException("Undefined array: " + name);
                Object data = s.getData();
                if (!(data instanceof List)) throw new RuntimeException(name + " is not an array");
                List<Object> arr = (List<Object>) data;

                if (node.getChildren().isEmpty())
                    throw new RuntimeException("Array index missing for " + name);

                Node idxNode = node.getChildren().getFirst();  // first child is the index expression
                int idx = (int) toLong(evalExpression(idxNode));

                if (idx < 0 || idx >= arr.size())
                    throw new RuntimeException("Array index out of bounds: " + idx);

                return arr.get(idx);
            }


            // function call
            case FUNCTION_CALL: {
                String fname = node.getText();
                Symbol fsym = lookupSymbol(fname);
                if (fsym == null) throw new RuntimeException("Unknown function: " + fname);
                Object funcData = fsym.getData();
                if (!(funcData instanceof Node)) throw new RuntimeException("Function symbol malformed: " + fname);
                Node funcDecl = (Node) funcData;
                // get parameter names and body
                List<Node> paramNodes = new ArrayList<>();
                if (funcDecl.getChildren().stream().anyMatch(n -> n.getType() == NodeType.FUNCTION_PARAMETERS)) {
                    Node params = funcDecl.getChildById(NodeType.FUNCTION_PARAMETERS);
                    paramNodes = params.getChildren();
                }
                Node body = funcDecl.getChildById(NodeType.FUNCTION_BODY);

                // build argument values from FUNCTION_ARGUMENTS (child 0)
                Node argsNode = node.getChildById(NodeType.FUNCTION_ARGUMENTS);
                List<Object> argValues = argsNode.getChildren().stream()
                        .map(arg -> {
                            // each FUNCTION_ARGUMENT contains a FUNCTION_ARGUMENT_NAME which holds lexeme text for literal or identifier
                            Node argName = arg.getChildById(NodeType.FUNCTION_ARGUMENT_NAME);
                            Node argType = arg.getChildById(NodeType.FUNCTION_ARGUMENT_TYPE);
                            // parser produced argument nodes with lexeme text; for simplicity we evaluate by parsing text or treating as literal
                            // If argType text starts with LITERAL_ we parse literal; else if IDENTIFIER then lookup
                            String typeText = argType.getText();
                            if (typeText == null) {
                                // try evaluate the argument child by evaluating the FUNCTION_ARGUMENT_NAME as expression
                                return tryParseLiteralOrLookup(argName.getText());
                            } else {
                                // try parse literal or lookup
                                return tryParseLiteralOrLookup(argName.getText());
                            }
                        }).collect(Collectors.toList());

                // call: create new scope and bind parameters
                SymbolTable local = new SymbolTable();
                scopeStack.push(local);
                try {
                    // bind params
                    for (int i = 0; i < paramNodes.size(); i++) {
                        Node param = paramNodes.get(i);
                        String pname = param.getChildById(NodeType.FUNCTION_PARAMETER_NAME).getText();
                        Object pval = (i < argValues.size()) ? argValues.get(i) : null;
                        local.add(Symbol.builder().name(pname).data(pval).build());
                    }

                    // execute function body: body is FUNCTION_BODY -> BLOCK_STATEMENT
                    try {
                        execStatement(body.getChildren().getFirst());
                    } catch (ReturnValue rv) {
                        return rv.value;
                    }
                    // no explicit return
                    return null;
                } finally {
                    scopeStack.pop();
                }
            }

            // Binary operators (structure: node.children[0] left, children[1] right)
            case ADDITION:
                return numericBinaryOp(node, (a, b) -> a + b);
            case SUBTRACTION:
                return numericBinaryOp(node, (a, b) -> a - b);
            case MULTIPLICATION:
                return numericBinaryOp(node, (a, b) -> a * b);
            case DIVISION:
                return numericBinaryOp(node, (a, b) -> a / b);
            case MODULO:
                return numericBinaryOp(node, (a, b) -> a % b);

            case EQUALS:
                return Objects.equals(evalExpression(node.getChildren().getFirst()), evalExpression(node.getChildren().get(1)));
            case NOTEQUALS:
                return !Objects.equals(evalExpression(node.getChildren().getFirst()), evalExpression(node.getChildren().get(1)));

            case LESS:
                return compareNumeric(node) < 0;
            case LESSEQUALS:
                return compareNumeric(node) <= 0;
            case GREATER:
                return compareNumeric(node) > 0;
            case GREATEREQUALS:
                return compareNumeric(node) >= 0;

            case AND:
                return truthy(evalExpression(node.getChildren().getFirst())) && truthy(evalExpression(node.getChildren().get(1)));
            case NAND:
                return !(truthy(evalExpression(node.getChildren().getFirst())) && truthy(evalExpression(node.getChildren().get(1))));
            case OR:
                return truthy(evalExpression(node.getChildren().getFirst())) || truthy(evalExpression(node.getChildren().get(1)));
            case NOR:
                return !(truthy(evalExpression(node.getChildren().getFirst())) || truthy(evalExpression(node.getChildren().get(1))));
            case XOR: {
                boolean a = truthy(evalExpression(node.getChildren().getFirst()));
                boolean b = truthy(evalExpression(node.getChildren().get(1)));
                return a ^ b;
            }
            case XNOR: {
                boolean a = truthy(evalExpression(node.getChildren().getFirst()));
                boolean b = truthy(evalExpression(node.getChildren().get(1)));
                return !(a ^ b);
            }

            case NEGATIVE:
                Object v = evalExpression(node.getChildren().getFirst());
                if (v instanceof Double) return -((Double) v);
                return -toLong(v);

            case PRE_INCREMENT:
            case POST_INCREMENT: {
                Node target = node.getChildren().getFirst();
                Object current = loadFromTarget(target);
                Object newv = numericBinaryRaw(current, 1, (a, b) -> a + b);
                assignToTarget(target, newv);
                return node.getType() == NodeType.PRE_INCREMENT ? newv : current;
            }

            case PRE_DECREMENT:
            case POST_DECREMENT: {
                Node target = node.getChildren().getFirst();
                Object current = loadFromTarget(target);
                Object newv = numericBinaryRaw(current, 1, (a, b) -> a - b);
                assignToTarget(target, newv);
                return node.getType() == NodeType.PRE_DECREMENT ? newv : current;
            }

            default:
                throw new RuntimeException("Unhandled expression node: " + node.getType());
        }
    }

    // -----------------------
    // Small helpers
    // -----------------------

    private boolean isExpressionNode(NodeType t) {
        // treat many node types as expressions
        switch (t) {
            case ADDITION: case SUBTRACTION: case MULTIPLICATION: case DIVISION: case MODULO:
            case NEGATIVE:
            case LITERAL_INT: case LITERAL_FLOAT: case LITERAL_STRING: case LITERAL_CHAR:
            case LITERAL_TRUE: case LITERAL_FALSE: case LITERAL_ARRAY:
            case VARIABLE_ACCESS: case ARRAY_ACCESS:
            case EQUALS: case NOTEQUALS:
            case LESS: case LESSEQUALS: case GREATER: case GREATEREQUALS:
            case AND: case OR: case XOR: case XNOR: case NAND: case NOR:
            case FUNCTION_CALL:
            case PRE_INCREMENT: case POST_INCREMENT: case PRE_DECREMENT: case POST_DECREMENT:
                return true;
            default:
                return false;
        }
    }

    private Object parseLiteralNode(Node node) {
        switch (node.getType()) {
            case ARRAY_ELEMENT:
                // parser stores token lexeme in text
                return tryParseLiteralOrLookup(node.getText());
            case LITERAL_INT:
                return Long.parseLong(node.getText());
            case LITERAL_FLOAT:
                return Double.parseDouble(node.getText());
            case LITERAL_STRING:
                return node.getText();
            case LITERAL_CHAR:
                return node.getText().charAt(0);
            case LITERAL_TRUE:
                return true;
            case LITERAL_FALSE:
                return false;
            default:
                // fallback: evaluate as expression
                return evalExpression(node);
        }
    }

    private Object tryParseLiteralOrLookup(String lexeme) {
        if (lexeme == null) return null;
        // try integer
        try { return Long.parseLong(lexeme); } catch (Exception ignored) {}
        try { return Double.parseDouble(lexeme); } catch (Exception ignored) {}
        if ("true".equalsIgnoreCase(lexeme)) return true;
        if ("false".equalsIgnoreCase(lexeme)) return false;
        if (lexeme.length() >= 2 && lexeme.startsWith("\"") && lexeme.endsWith("\"")) return lexeme.substring(1, lexeme.length()-1);
        // else lookup variable
        Symbol s = lookupSymbol(lexeme);
        if (s != null) return s.getData();
        // fallback raw string
        return lexeme;
    }

    private Node makeLiteralFromObject(Object o) {
        Node n = new Node();
        if (o == null) { n.setType(NodeType.LITERAL_STRING); n.setText("null"); return n; }
        if (o instanceof Long) { n.setType(NodeType.LITERAL_INT); n.setText(Long.toString((Long)o)); return n; }
        if (o instanceof Double) { n.setType(NodeType.LITERAL_FLOAT); n.setText(Double.toString((Double)o)); return n; }
        if (o instanceof Boolean) { n.setType((Boolean)o ? NodeType.LITERAL_TRUE : NodeType.LITERAL_FALSE); n.setText(Boolean.toString((Boolean)o)); return n; }
        if (o instanceof String) { n.setType(NodeType.LITERAL_STRING); n.setText((String)o); return n; }
        // arrays and other types are not used here
        n.setType(NodeType.LITERAL_STRING);
        n.setText(o.toString());
        return n;
    }

    private long toLong(Object v) {
        if (v == null) return 0;
        if (v instanceof Long) return (Long) v;
        if (v instanceof Integer) return (Integer) v;
        if (v instanceof Double) return ((Double) v).longValue();
        if (v instanceof String) return Long.parseLong((String) v);
        throw new RuntimeException("Cannot convert to long: " + v.getClass());
    }

    private double toDouble(Object v) {
        if (v == null) return 0.0;
        if (v instanceof Double) return (Double) v;
        if (v instanceof Long) return ((Long) v).doubleValue();
        if (v instanceof Integer) return ((Integer) v).doubleValue();
        if (v instanceof String) return Double.parseDouble((String) v);
        throw new RuntimeException("Cannot convert to double: " + v.getClass());
    }

    private boolean truthy(Object v) {
        if (v == null) return false;
        if (v instanceof Boolean) return (Boolean) v;
        if (v instanceof Long) return ((Long) v) != 0L;
        if (v instanceof Double) return ((Double) v) != 0.0;
        if (v instanceof String) return !((String) v).isEmpty();
        if (v instanceof List) return !((List<?>) v).isEmpty();
        return true;
    }

    private Object numericBinaryOp(Node node, NumericOp op) {
        Object a = evalExpression(node.getChildren().getFirst());
        Object b = evalExpression(node.getChildren().get(1));
        // if any double -> double arithmetic
        if (a instanceof Double || b instanceof Double) {
            double ad = toDouble(a), bd = toDouble(b);
            return op.applyDouble(ad, bd);
        } else {
            long al = toLong(a), bl = toLong(b);
            return op.applyLong(al, bl);
        }
    }

    private Object numericBinaryRaw(Object a, Object bObj, NumericOp op) {
        if (a instanceof Double || bObj instanceof Double) {
            double ad = toDouble(a), bd = toDouble(bObj);
            return op.applyDouble(ad, bd);
        } else {
            long al = toLong(a), bl = toLong(bObj);
            return op.applyLong(al, bl);
        }
    }

    private int compareNumeric(Node node) {
        Object left = evalExpression(node.getChildren().getFirst());
        Object right = evalExpression(node.getChildren().get(1));
        if (left instanceof Double || right instanceof Double) {
            double dL = toDouble(left), dR = toDouble(right);
            return Double.compare(dL, dR);
        } else {
            long l = toLong(left), r = toLong(right);
            return Long.compare(l, r);
        }
    }

    private String stringify(Object value) {
//        if (value instanceof String) return "\"" + value + "\"";
//        if (value instanceof Character) return "'" + value + "'";
        return String.valueOf(value);
    }


    private SymbolTable currentScope() {
        return scopeStack.peek();
    }

    private Symbol lookupLocalSymbol(String name) {
        SymbolTable local = scopeStack.peek();
        if (local == null) return null;
        return local.lookup(name);
    }

    private Symbol lookupSymbol(String name) {
        // check local scopes from top -> bottom
        for (SymbolTable st : scopeStack) {
            Symbol s = st.lookup(name);
            if (s != null) return s;
        }
        // then global symbol table
        return symbolTable.lookup(name);
    }

    private Object loadFromTargetForEval(Node target) {
        return loadFromTarget(target);
    }

    // Interface for numeric operations
    private interface NumericOp {
        long applyLong(long a, long b);
        default double applyDouble(double a, double b) { return applyLong((long)a, (long)b); }
    }
}
