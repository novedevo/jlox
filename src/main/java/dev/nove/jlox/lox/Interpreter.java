package dev.nove.jlox.lox;

class Interpreter implements Expr.Visitor<Object> {
    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        return switch (expr.operator.type) {
            case MINUS -> -(double) right;
            case BANG -> !isTruthy(right);
            default -> null; // unreachable
        };
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        } else {
            throw new RuntimeError(operator, "Operand must be a number.");
        }
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        } else if (object instanceof Boolean) {
            return (boolean) object;
        } else {
            return true;
        }
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }

    private String stringify(Object object) {
        if (object == null) {
            return "nil";
        } else if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        } else {
            return object.toString();
        }
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG_EQUAL -> {
                return !isEqual(left, right);
            }
            case EQUAL_EQUAL -> {
                return isEqual(left, right);
            }
            case GREATER -> {
                checkNumberOperand(expr.operator, right);
                return (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperand(expr.operator, right);
                return (double) left >= (double) right;
            }
            case LESS -> {
                checkNumberOperand(expr.operator, right);
                return (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperand(expr.operator, right);
                return (double) left < (double) right;
            }
            case MINUS -> {
                checkNumberOperand(expr.operator, right);
                return (double) left - (double) right;
            }
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                } else if (left instanceof String || right instanceof String) {
                    return stringify(left) + stringify(right);
                } else {
                    throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
                }
            }
            case SLASH -> {
                checkNumberOperand(expr.operator, right);
                if ((double) right == 0.0) {
                    throw new RuntimeError(expr.operator, "Divide by zero error");
                }
                return (double) left / (double) right;
            }
            case STAR -> {
                checkNumberOperand(expr.operator, right);
                return (double) left * (double) right;
            }
            case COMMA -> {
                return right;
            }
            default -> {
                return null;
            } // unreachable
        }
    }

    @Override
    public Object visitTernaryExpr(Expr.Ternary expr) {
        if (isTruthy(expr.condition)) {
            return evaluate(expr.ifTrue);
        } else {
            return evaluate(expr.ifFalse);
        }
    }
}
