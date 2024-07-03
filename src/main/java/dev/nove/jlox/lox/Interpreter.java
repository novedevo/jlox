package dev.nove.jlox.lox;

class Interpreter implements Expr.Visitor<Object> {
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
                return (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                return (double) left >= (double) right;
            }
            case LESS -> {
                return (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                return (double) left < (double) right;
            }
            case MINUS -> {
                return (double) left - (double) right;
            }
            case PLUS -> {
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                } else if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                } else {
                    return null;
                }
            }
            case SLASH -> {
                return (double) left / (double) right;
            }
            case STAR -> {
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
