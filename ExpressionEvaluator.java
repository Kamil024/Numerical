public class ExpressionEvaluator {
    private int pos = -1;
    private int ch;
    private String expr;

    public double eval(String expression) {
        this.expr = expression;
        this.pos = -1;
        nextChar();
        double x = parseExpression();
        if (pos < expr.length()) {
            throw new RuntimeException("Unexpected: " + (char)ch);
        }
        return x;
    }

    private void nextChar() {
        ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
    }

    private boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    private double parseExpression() {
        double x = parseTerm();
        for (;;) {
            if (eat('+')) {
                x += parseTerm();
            } else if (eat('-')) {
                x -= parseTerm();
            } else {
                return x;
            }
        }
    }

    private double parseTerm() {
        double x = parseFactor();
        for (;;) {
            if (eat('*')) {
                x *= parseFactor();
            } else if (eat('/')) {
                x /= parseFactor();
            } else {
                return x;
            }
        }
    }

    private double parseFactor() {
        if (eat('+')) return parseFactor();
        if (eat('-')) return -parseFactor();

        double x;
        int startPos = this.pos;

        if (eat('(')) {
            x = parseExpression();
            eat(')');
        } else if ((ch >= '0' && ch <= '9') || ch == '.') {
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(expr.substring(startPos, this.pos));
        } else if (ch >= 'a' && ch <= 'z') {
            while (ch >= 'a' && ch <= 'z') nextChar();
            String func = expr.substring(startPos, this.pos);
            x = parseFactor();
            if (func.equals("sqrt")) x = Math.sqrt(x);
            else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
            else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
            else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
            else if (func.equals("ln")) x = Math.log(x);
            else if (func.equals("log")) x = Math.log10(x);
            else throw new RuntimeException("Unknown function: " + func);
        } else {
            throw new RuntimeException("Unexpected: " + (char)ch);
        }

        if (eat('^') || eat('*') && eat('*')) {
            x = Math.pow(x, parseFactor());
        }

        return x;
    }
}
