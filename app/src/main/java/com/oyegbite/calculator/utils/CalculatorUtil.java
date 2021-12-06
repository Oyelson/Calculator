package com.oyegbite.calculator.utils;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.Map;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuilder;

@RequiresApi(api = Build.VERSION_CODES.R)
public class CalculatorUtil {
    private static final String TAG = CalculatorUtil.class.getSimpleName();

    private static final Map<String, Integer> operatorPrecedence =
            Map.ofEntries(
                    Map.entry("^", 3), // Exponent
                    Map.entry("/", 2), // Division
                    Map.entry("÷", 2), // Division
                    Map.entry("*", 2), // Multiplication
                    Map.entry("×", 2), // Multiplication
                    Map.entry("+", 1), // Addition
                    Map.entry("-", 1),  // Subtraction
                    Map.entry("–", 1)  // Subtraction
            );

    private static Context mContext;

    public static Double calculate(Context context, String infixExpression) {
        mContext = context;
        List<String> postfixExpression = convertInfixToPostfix(infixExpression);
        return evaluatePostfix(postfixExpression);
    }

    /**
     * Convert infixExpression say "-2+1*5(2(+5+12/6))" to something like "0-2+1*5*(2*(0+5+12/6))"
     *
     * @param infixExpression
     * @return
     */
    public static String includeMultiplyOperatorIfNeeded(String infixExpression) {
        infixExpression = infixExpression.replaceAll(" ", "");

        StringBuilder sb = new StringBuilder();
        int stringLength  = infixExpression.length();
        for (int i = 0; i < stringLength; i++) {
            char token = infixExpression.charAt(i);
            // char prevToken = infixExpression.charAt(i-1);
            // char nextToken = infixExpression.charAt(i+1);

            if ((i == 0 && isTokenOperator(String.valueOf(token)))
                    || (i > 0
                    && isTokenOperator(String.valueOf(token))
                    && isOpenParentheses(String.valueOf(infixExpression.charAt(i-1))))) {
                // "-2+1" --> "0-2+1"
                // OR
                // "2-(+2+1)+5" --> "2-(0+2+1)+5"
                sb.append("0");
            }

            if (i > 0 && isCloseParentheses(
                    String.valueOf(infixExpression.charAt(i-1)))
                    && (isOpenParentheses(String.valueOf(token))
                    || Character.isDigit(token))) {
                // "2*(3+2)5" --> "2*(3+2)*5"
                // "(3+2)(5+2)" --> "(3+2)*(5+2)"
                sb.append("*");
            }

            sb.append(token);

            if (i < stringLength - 1) {
                if (Character.isDigit(token)
                        && isOpenParentheses(String.valueOf(infixExpression.charAt(i+1)))) {
                    sb.append("*"); // "2(3+2)+5" --> "2*(3+2)+5"
                }
            }
        }
        return sb.toString();
    }

    /**
     * Infix expression which is the user input is in the form of
     * "<operand> <operator> <operand>"
     * E.g A + (B + C)
     * A is an operand, + is an operator, B is an operand, C is an operand,
     * (B + C) is an operand (since the addition would result to an operand)
     *
     * If I get an operator of lower priority, that marks the boundary of the right
     * operand because of associativity rule
     *
     * 2098+300*91-84/3
     *
     * if I see an operator, I need to look on top of stack
     *
     * When you see a closing parentheses, keep popping from stack until you see an opening parentheses
     * then stop the pop at that opening parentheses since it would mark the boundary of an operand
     *
     * "2098+300*91-84/3" => ["2098", "300", "91", "*", "+", "84", "3", "/", "-"]
     *
     * => "2098+300*91-84/3"
     *                ^
     * => post = [2098, 300, 91, ]...
     * => operator = [+, *]...
     *
     * "(2098+300*(91-84))/3" => ["2098", "300", "91", "84", "-", "*", "+", "3", "/"]
     *
     * Assumes infixExpression is a valid expression.
     *
     * @param infixExpression input mathematical expression from user.
     */
    public static List<String> convertInfixToPostfix(String infixExpression) {
        infixExpression = includeMultiplyOperatorIfNeeded(infixExpression);

        Log.i(TAG, "infixExpression before = " + infixExpression);

        String newInfixExpression = infixExpression.replaceAll("[()]", "");

        Log.i(TAG, "infixExpression after = " + newInfixExpression);

        if (!new Expression(mContext).validate(newInfixExpression)) {
            return new ArrayList<String>();
        }

        Stack<String> operatorStack = new Stack<>();
        List<String> postfix = new ArrayList<>();

        List<String> digits = new ArrayList<>();
        for(int i = 0; i < infixExpression.length(); i++) {
            String token = String.valueOf(infixExpression.charAt(i));

            if (token.equals(" ")) continue;

            if (isOpenParentheses(token)) { // If we meet a starting boundary
                operatorStack.add(token);
            } else if (isCloseParentheses(token)) { // If we meet a closing boundary
                // When we see a closing boundary, and our cumulative digits is not empty,
                // then our digits becomes a number.
                if (!digits.isEmpty()) {
                    postfix.add(String.join("", digits));
                    digits = new ArrayList<>(); // reset digits to take another number
                }
                while (!operatorStack.isEmpty() && !isOpenParentheses(operatorStack.peek())) {
                    postfix.add(operatorStack.pop());
                }
                // Remove the opening parentheses that marks the operand boundary
                operatorStack.pop();

            } else if (isTokenOperator(token)) {
                // When we see an operator, and our cummulative digits is not empty,
                // then our digits becomes a number.
                if (!digits.isEmpty()) {
                    postfix.add(String.join("", digits));
                    digits = new ArrayList<>(); // reset digits to take another number
                }
                while (
                        !operatorStack.isEmpty()
                                && !isOpenParentheses(operatorStack.peek())
                                && hasHigherPrecedence(operatorStack.peek(), token)) {
                    postfix.add(operatorStack.pop());
                }

                if (i == 0 || (i > 0 && isOpenParentheses(String.valueOf(infixExpression.charAt(i-1))))) {
                    // 1. Case of say s = "-2+1"
                    //    (we would convert it to s = "0-2+1")
                    // 2. Case of say s = "1-(+1+1)"
                    //    (we would convert it to s = "1-(0+1+1)")
                    postfix.add("0");
                }
                operatorStack.add(token);
            } else {
                // Get cumulative of digits (say we have 2098+300*91-84/3)
                digits.add(token);
            }
        }

        if (!digits.isEmpty()) postfix.add(String.join("", digits));

        while (!operatorStack.isEmpty()) {
            postfix.add(operatorStack.pop());
        }
        return postfix;
    }

    /**
     * E.g postfixExpression: ["2", "3", "*", "5", "4", "*", "+", "9", "-"]
     * If the token seen is an operator, it should be applied to the last
     * two operands preceeding it and perform the operation.
     *
     * @param postfixExpression has its operators after its operands.
     * @return the evaluation of the postfix expression.
     */
    public static Double evaluatePostfix(List<String> postfixExpression) {
        Log.i(TAG, "postfixExpression = " + postfixExpression);
        // A token here is either an operand or an operator.
        Stack<Double> stack = new Stack<>();
        for (String token: postfixExpression) {
            // If token is an operator, then it should be applied to
            // the last two operands preceding it and perform the operation.
            if (isTokenOperator(token)) {
                double secondOperand = stack.pop();
                double firstOperand = stack.pop();

                double result = performOperation(firstOperand, secondOperand, token);
                stack.push(result);
            } else {
                stack.push(Double.valueOf(token));
            }
        }

        // If postfix expression is valid, the stack would just have one value left.
        if (stack.empty()) return null;

        Log.i(TAG, "stack = " + Arrays.toString(stack.toArray()));
        return stack.peek();
    }

    private static Boolean hasHigherPrecedence(String topOperator, String newOperator) {
        return operatorPrecedence.get(topOperator) >= operatorPrecedence.get(newOperator);
    }

    private static Boolean isTokenOperator(String token) {
        return token.equals("+")
                || token.equals("-")
                || token.equals("–")
                || token.equals("*")
                || token.equals("×")
                || token.equals("/")
                || token.equals("÷")
                || token.equals("^");
    }

    public static Boolean isDigitEmpty(String digits) {
        return TextUtils.isEmpty(digits);
    }

    public static Boolean isOpenParentheses(String token) {
        return token.equals("(")
                || token.equals("{")
                || token.equals("[");
    }

    public static Boolean isCloseParentheses(String token) {
        return token.equals(")")
                || token.equals("}")
                || token.equals("]");
    }

    private static Double performOperation(double firstOperand, double secondOperand, String operator) {
        switch (operator) {
            case "^":
                return Math.pow(firstOperand, secondOperand);
            case "*":
            case "×":
                return firstOperand * secondOperand;
            case "/":
            case "÷":
                return firstOperand / (double) secondOperand;
            case "+":
                return firstOperand + secondOperand;
            case "-":
            case "–":
                return firstOperand - secondOperand;
        }
        return Double.MAX_VALUE;
    }

//    public static void main(String[] args) {
//        // String infixExpression = "(20+5)*(500/(125*2))";
//        // String infixExpression = "5(500/(125*2))";
//        // String infixExpression = "  5 (2 (5 + 12 / 6))";
//        // String infixExpression = " -2+1*5 (2 ( +5+12/6))";
//        // String infixExpression = "2 + 3(7 – 9)";
//        // String infixExpression = "45 + (1250 * 100) / 10";
//        String infixExpression = "(0.+2)(5+2(7))";
//        System.out.println(calculate(infixExpression));
//    }

}
