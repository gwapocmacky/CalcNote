package com.example.calcnote;

public class ExprEvaluator {
    public static double eval(String expr) {
        if (expr == null) return 0;
        String s = expr.replace('÷', '/').replace('×', '*');
        Parser p = new Parser(s);
        double v = p.parse();
        if (Double.isNaN(v) || Double.isInfinite(v)) return 0;
        return v;
    }

    private static class Parser {
        private final String s;
        private int pos;
        Parser(String s) { this.s = s; this.pos = 0; }
        double parse() {
            skipSpaces();
            double v = parseExpr();
            return v;
        }
        private double parseExpr() {
            double v = parseTerm();
            while (true) {
                skipSpaces();
                if (match('+')) { v += parseTerm(); }
                else if (match('-')) { v -= parseTerm(); }
                else break;
            }
            return v;
        }
        private double parseTerm() {
            double v = parseFactor();
            while (true) {
                skipSpaces();
                if (match('*')) { v *= parseFactor(); }
                else if (match('/')) {
                    double d = parseFactor();
                    if (d == 0) return 0;
                    v /= d;
                }
                else break;
            }
            return v;
        }
        private double parseFactor() {
            skipSpaces();
            if (match('(')) {
                double v = parseExpr();
                skipSpaces();
                match(')');
                return v;
            }
            if (match('-')) return -parseFactor();
            if (match('+')) return parseFactor();
            // number
            int start = pos;
            while (pos < s.length() && (Character.isDigit(s.charAt(pos)) || s.charAt(pos)=='.' || s.charAt(pos)==',')) pos++;
            if (pos == start) return 0;
            String num = s.substring(start, pos).replace(',', '.');
            try { return Double.parseDouble(num); } catch (Exception e) { return 0; }
        }
        private void skipSpaces() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) pos++;
        }
        private boolean match(char c) {
            if (pos < s.length() && s.charAt(pos) == c) { pos++; return true; }
            return false;
        }
    }

    private static final java.util.regex.Pattern EXPR = java.util.regex.Pattern.compile("\\d[\\d.,]*(?:\\s*[+\\-*/÷]\\s*\\d[\\d.,]*)+");

    public static boolean hasNumber(String s) {
        if (s == null) return false;
        for (int i = 0; i < s.length(); i++) if (Character.isDigit(s.charAt(i))) return true;
        return false;
    }

    public static double lineTotal(String line) {
        if (line == null) return 0;
        double total = 0;
        String work = line;
        java.util.regex.Matcher m = EXPR.matcher(work);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            total += eval(m.group());
            m.appendReplacement(sb, " ");
        }
        m.appendTail(sb);
        work = sb.toString();
        // standalone numbers
        java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("\\d[\\d.,]*").matcher(work);
        while (m2.find()) {
            try { total += Double.parseDouble(m2.group().replace(',', '.')); } catch (Exception ignored) {}
        }
        return total;
    }

    public static double grandTotal(String text) {
        if (text == null) return 0;
        double total = 0;
        for (String line : text.split("\\n", -1)) {
            total += lineTotal(line);
        }
        return total;
    }
}
