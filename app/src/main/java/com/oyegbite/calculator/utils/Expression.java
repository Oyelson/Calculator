package com.oyegbite.calculator.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.oyegbite.calculator.R;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expression {
    private static final String TAG = Expression.class.getSimpleName();

    private Context mContext;

    private StringBuilder mInput;
    private StringBuilder mRawInput;
    private final Set<String> operators;
    private boolean mIsInputValid = true;

    private final String POINT;
    private final String MINUS;
    private final String PLUS;
    private final String DIVIDE;
    private final String MULTIPLY;
    private final String OPEN;
    private final String CLOSE;


    public Expression(Context context) {
        mContext = context;
        OPEN = context.getString(R.string.open_paren);
        CLOSE = context.getString(R.string.close_paren);
        POINT = context.getString(R.string.point);
        MINUS = context.getString(R.string.minus);
        PLUS = context.getString(R.string.plus);
        DIVIDE = context.getString(R.string.divide);
        MULTIPLY = context.getString(R.string.multiply);

        // ÷ × - +
        operators = new HashSet<>(
                Arrays.asList(
                        "+", "-", "/", "*", "–",
                        MULTIPLY,
                        DIVIDE,
                        MINUS,
                        PLUS
                )
        );
        clear();
    }

    public void add(String token) {
        mRawInput.append(token);
        mInput = modify(mRawInput.toString());
        mIsInputValid = validate(mInput.toString());
    }

    public void pop() {
        if (mRawInput.length() > 0) {
            mRawInput.deleteCharAt(mRawInput.length()-1);
            mInput = modify(mRawInput.toString());
            mIsInputValid = validate(mInput.toString());
        }
    }

    public boolean isValid() {
        return mIsInputValid;
    }

    public void clear() {
        mRawInput = new StringBuilder();
        mInput = new StringBuilder();
        mIsInputValid = false;
    }

    public String getRawInput() {
        return mRawInput.toString();
    }

    public String getEditedInput() {
        return mInput.toString();
    }

    public String getComputationInput() {
        if (mInput.length() == 0) return "";

        StringBuilder sb = new StringBuilder();
        int len = mInput.length();
        int opened = 0;

        for (int i = 0; i < len; i++) {
            String token = mInput.charAt(i) + "";

            if (i == 0 && token.equals(POINT)) {
                sb.append("0");
            } else if (i > 0) {
                char previousToken = mInput.charAt(i - 1);
                if (token.equals(POINT) && !Character.isDigit(previousToken)) {
                    sb.append("0");
                }
            }

            if (operators.contains(token)) {
                sb.append(" ");
                sb.append(token);
                sb.append(" ");

            } else {
                sb.append(token);
            }


            if (token.equals(OPEN)) {
                opened++;
            } else if (token.equals(CLOSE)) {
                if (opened > 0) {
                    opened--;
                } else {
                    sb.append(CLOSE);
                }
            }

            if (token.equals(POINT) && (i == len - 1 || (i + 1) < len && !Character.isDigit(mInput.charAt(i + 1)))) {
                sb.append("0");
            }

        }

        while (opened > 0) {
            sb.append(CLOSE);
            opened--;
        }

        return sb.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public Double compute() {
        try {
            return CalculatorUtil.calculate(mContext, getComputationInput());
        } catch (Exception e) {
            return null;
        }
    }

    public String getReadableInput() {
        StringBuilder numbers = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mInput.length(); i++) {
            String token = mInput.charAt(i) + "";
            if (Character.isDigit(mInput.charAt(i)) || token.equals(POINT)) {
                numbers.append(token);
            } else {
                // Format number in 3's (e.g "20932" => "20 932" or "3194.9323" => "3 194.932 3")
                if (numbers.length() > 0) {
                    sb.append(formatNumber(numbers.toString()));
                }
                numbers = new StringBuilder();

                if (operators.contains(token)) {
                    sb.append(" ");
                    sb.append(token);
                    sb.append(" ");

                } else {
                    sb.append(token);
                }

            }
        }

        if (numbers.length() > 0) sb.append(numbers.toString());
        return sb.toString();
    }

    public String formatNumber(String number) {
        StringBuilder sb = new StringBuilder();
        String[] numbers = number.split("\\.");
        if (numbers.length == 0) return "";
        if (numbers.length == 1) {
            return formatBeforeDecimal(numbers[0]);
        }
        return formatBeforeDecimal(numbers[0]) + POINT + formatAfterDecimal(numbers[1]);
    }

    public String formatBeforeDecimal(String before) {
        StringBuilder sb = new StringBuilder();
        if (before.length() > 0) {
            int residue = before.length() % 3;
            int remaining = before.length() - residue;
            int start = 0;
            int end = residue;

            if (residue > 0) {
                sb.append(before.substring(start, end));
                sb.append(" ");
            }

            while (remaining > 0) {
                start = end;
                end = start + 3;
                sb.append(before.substring(start, end));
                sb.append(" ");
                remaining -= 3;
            }
        }

        return sb.substring(0, sb.length()-1);
    }

    public String formatAfterDecimal(String after) {
        StringBuilder sb = new StringBuilder();

        if (after.length() > 0) {
            int residue = after.length() % 3;
            int remaining = after.length() - residue;
            int start = 0;
            int end = 0;

            while (remaining > 0) {
                start = end;
                end = start + 3;
                sb.append(after.substring(start, end));
                sb.append(" ");
                remaining -= 3;
            }

            if (residue > 0) {
                start = end;
                end = start + residue;
                sb.append(after.substring(start, end));
                sb.append(" ");
            }
        }

        return sb.substring(0, sb.length()-1);
    }

    private StringBuilder modify(String input) {
        // -+÷×
        input = input.replaceAll("(?:\\s+|[^-\\-+÷×*/().\\d]*)", "");

        StringBuilder sb = new StringBuilder();
        int len = input.length();

        for (int i = 0; i < len; i++) {
            String token = input.charAt(i) + "";

            if (i == 0 && token.equals(POINT)) {
                sb.append("0");
            } else if (i > 0) {
                char previousToken = input.charAt(i - 1);
                if (token.equals(POINT) && !Character.isDigit(previousToken)) {
                    sb.append("0");
                }
            }

            sb.append(token);

            if (token.equals(POINT) && (i + 1) < len && !Character.isDigit(input.charAt(i + 1))) {
                sb.append("0");
            }

        }

        return sb;
    }

    public boolean validate(String input) {
        if (input.length() == 0) return true;
        if (!hadValidSequence(input)) return false;
        Log.i(TAG,"Passed valid sequence");

        // operand operator operand
        // Operators: + - /÷ *×
        // Operands: 0 to 9
        // Other tokens: ., ( and )
        // 1. Only ., +, -, ( or operand at beginning of expression (if it is a point, add a "0" before it).
        String regex1 = "^\\s*(?:0\\.|[–\\-+]|\\(+|[1-9])";
        Matcher matcher1 = Pattern.compile(regex1).matcher(input);
        if (!matcher1.find()) return false;
        Log.i(TAG, "Passed regex1");

        // 2. No adjacent operators.
        String regex2 = "(?:[–\\-+÷×*/]\\s*){2,}"; // Get all adjacent operators
        Matcher matcher2 = Pattern.compile(regex2).matcher(input);
        if (matcher2.find()) return false;
        Log.i(TAG, "Passed regex2");

        // 3. No point just before the open parentheses.
        // Get all point that is followed by an open parentheses.
        String regex3 = "(?:[.]+\\s*(?=\\())";
        Matcher matcher3 = Pattern.compile(regex3).matcher(input);
        if (matcher3.find()) return false;
        Log.i(TAG, "Passed regex3");

        // 4. Only +, -, (, and operands just after the open parentheses
        // (i.e *, /, ., ) should not be just after the open parentheses)
        // Get all the *, /, ., ) just after the open parentheses.
        String regex4 = "(?:\\()\\s*(?=[÷×/*.)])";
        Matcher matcher4 = Pattern.compile(regex4).matcher(input);
        if (matcher4.find()) return false;
        Log.i(TAG, "Passed regex4");

        // 5. No operator, point, or ( just before the close parentheses.
        // Get all operator, point or ( just before the close parentheses.
        String regex5 = "[–\\-+/*÷×.(]\\s*(?=\\))";
        Matcher matcher5 = Pattern.compile(regex5).matcher(input);
        if (matcher5.find()) return false;
        Log.i(TAG, "Passed regex5");

        // 6. No operand or point just after the close parentheses.
        // Get all operand or point just after the close parentheses
        String regex6 = "(?:\\))\\s*(?=[0-9.])";
        Matcher matcher6 = Pattern.compile(regex6).matcher(input);
        if (matcher6.find()) return false;
        Log.i(TAG, "Passed regex6");

        // 7. Only a number can preceed and also follow a point.
        // Get all non-number that preceed or follow a point.
        String regex7 = "([–\\-+/*÷×)(.]\\s*(?=\\.)|(?:\\.)\\s*(?=[–\\-+/*÷×)(.]))";
        Matcher matcher7 = Pattern.compile(regex7).matcher(input);
        if (matcher7.find()) return false;
        Log.i(TAG, "Passed regex7");

        return true;
    }

    /**
     * Validate parentheses and numbers simultaneously.
     *
     * @param input character sequence to validate
     * @return false if we detect invalid parentheses combination sequence
     *          (e.g ")(2 + 5 )))")
     *          or operand and point combination sequence
     *          (e.g "00.3" or "0980" or "024.00.42" or "94.00.42" or "00")
     *          else we return true.
     */
    private boolean hadValidSequence(String input) {
        StringBuilder numbersAndPointOnly = new StringBuilder();
        int opened = 0;
        for (int i = 0; i < input.length(); i++) {
            String token = input.charAt(i) + "";

            if (Character.isDigit(input.charAt(i)) || token.equals(POINT)) {
                numbersAndPointOnly.append(token);
            } else {
                if (!isNumberValid(numbersAndPointOnly.toString())) return false;
                numbersAndPointOnly = new StringBuilder();
            }

            if (token.equals(OPEN)) {
                opened++;
            } else if (token.equals(CLOSE)) {
                if (opened > 0) opened--;
                else return false;
            }
        }

        return isNumberValid(numbersAndPointOnly.toString());
    }

    /**
     * Correct input: "230.6" or "0.0344" or "0.50030" or "2003.323"
     * Wrong input: "00.3" or "0980" or "024.00.42" or "94.00.42" or "00"
     *
     * @param input to validate
     * @return true if we have a valid number and point sequence else false
     */
    private boolean isNumberValid(String input) {
        if (input.length() == 0) return true;

        // Correct: "230.6" or "0.0344" or "0.50030" or "2003.323"
        // Wrong: "00.3" or "0980" or "024.00.42" or "94.00.42"
        String regex = "^(?:(?:0|[1-9]+\\d*)(?:\\.\\d+)?|0|(?:\\.\\d+)?|(?:0|[1-9]+\\d*)(?:\\.)?)$";
        return Pattern.matches(regex, input);
    }
}