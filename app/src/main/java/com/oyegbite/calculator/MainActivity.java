package com.oyegbite.calculator;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.oyegbite.calculator.databinding.ActivityMainBinding;
import com.oyegbite.calculator.utils.Expression;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private Toast mToast;

    private TextView delete, deleteAll, openParen, closeParen, divide;
    private TextView seven, eight, nine, multiply;
    private TextView four, five, six, minus;
    private TextView one, two, three, plus;
    private TextView zero, point, equals;
    
    private Map<Integer, String> operandsID;
    private Map<Integer, String> operatorsID;
    private Map<Integer, String> parenthesesID;

    private EditText inputScreen;
    private TextView outputScreen;
    
    private Expression mExpression;
    private int lastClickedButtonID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setBinding();

        setOperandsID();
        setOperatorsID();
        setParenthesesID();

        mExpression = new Expression(this);
    }

    private void setBinding() {
        activateAllButtons();
    }

    private void activateAllButtons() {
        activateInputScreen();
        activateOutputScreen();

        activateDeleteButton();
        activateDeleteAllButton();
        activateOpenParenButton();
        activateCloseParenButton();
        activateDivideButton();

        activateSevenButton();
        activateEightButton();
        activateNineButton();
        activateMultiplyButton();

        activateFourButton();
        activateFiveButton();
        activateSixButton();
        activateMinusButton();

        activateOneButton();
        activateTwoButton();
        activateThreeButton();
        activatePlusButton();

        activateZeroButton();
        activatePointButton();
        activateEqualsButton();
    }

    private void setOperandsID() {
        operandsID = new HashMap<Integer, String>() {{
           put(R.id.zero, getString(R.string.zero));
           put(R.id.one, getString(R.string.one));
           put(R.id.two, getString(R.string.two));
           put(R.id.three, getString(R.string.three));
           put(R.id.four, getString(R.string.four));
           put(R.id.five, getString(R.string.five));
           put(R.id.six, getString(R.string.six));
           put(R.id.seven, getString(R.string.seven));
           put(R.id.eight, getString(R.string.eight));
           put(R.id.nine, getString(R.string.nine));
           put(R.id.point, getString(R.string.point));
        }};
    }

    private void setOperatorsID() {
        operatorsID = new HashMap<Integer, String>() {{
            put(R.id.divide, getString(R.string.divide));
            put(R.id.multiply, getString(R.string.multiply));
            put(R.id.minus, getString(R.string.minus));
            put(R.id.plus, getString(R.string.plus));
        }};
    }

    private void setParenthesesID() {
        parenthesesID = new HashMap<Integer, String>() {{
            put(R.id.open_paren, getString(R.string.open_paren));
            put(R.id.close_paren, getString(R.string.close_paren));
        }};
    }

    private void activateInputScreen() {
        inputScreen = mBinding.screen.inputScreen;
        inputScreen.setSelection(inputScreen.getText().length());
        hideKeyboard();
        inputScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
    }

    private void activateOutputScreen() {
        outputScreen = mBinding.screen.outputScreen;
    }

    private void activateDeleteAllButton() {
        deleteAll = mBinding.operandOperatorLayout.cancelToDivide.deleteAll;
        deleteAll.setOnClickListener(this);
    }

    private void activateDeleteButton() {
        delete = mBinding.delete;
        delete.setOnClickListener(this);
    }

    private void activateOpenParenButton() {
        openParen = mBinding.operandOperatorLayout.cancelToDivide.openParen;
        openParen.setOnClickListener(this);
    }

    private void activateCloseParenButton() {
        closeParen = mBinding.operandOperatorLayout.cancelToDivide.closeParen;
        closeParen.setOnClickListener(this);
    }

    private void activateDivideButton() {
        divide = mBinding.operandOperatorLayout.cancelToDivide.divide;
        divide.setOnClickListener(this);
    }

    private void activateSevenButton() {
        seven = mBinding.operandOperatorLayout.sevenToMultiply.seven;
        seven.setOnClickListener(this);
    }

    private void activateEightButton() {
        eight = mBinding.operandOperatorLayout.sevenToMultiply.eight;
        eight.setOnClickListener(this);
    }

    private void activateNineButton() {
        nine = mBinding.operandOperatorLayout.sevenToMultiply.nine;
        nine.setOnClickListener(this);
    }

    private void activateMultiplyButton() {
        multiply = mBinding.operandOperatorLayout.sevenToMultiply.multiply;
        multiply.setOnClickListener(this);
    }

    private void activateFourButton() {
        four = mBinding.operandOperatorLayout.fourToMinus.four;
        four.setOnClickListener(this);
    }

    private void activateFiveButton() {
        five = mBinding.operandOperatorLayout.fourToMinus.five;
        five.setOnClickListener(this);
    }

    private void activateSixButton() {
        six = mBinding.operandOperatorLayout.fourToMinus.six;
        six.setOnClickListener(this);
    }

    private void activateMinusButton() {
        minus = mBinding.operandOperatorLayout.fourToMinus.minus;
        minus.setOnClickListener(this);
    }

    private void activateOneButton() {
        one = mBinding.operandOperatorLayout.oneToPlus.one;
        one.setOnClickListener(this);
    }

    private void activateTwoButton() {
        two = mBinding.operandOperatorLayout.oneToPlus.two;
        two.setOnClickListener(this);
    }

    private void activateThreeButton() {
        three = mBinding.operandOperatorLayout.oneToPlus.three;
        three.setOnClickListener(this);
    }

    private void activatePlusButton() {
        plus = mBinding.operandOperatorLayout.oneToPlus.plus;
        plus.setOnClickListener(this);
    }

    private void activateZeroButton() {
        zero = mBinding.operandOperatorLayout.zeroToEquals.zero;
        zero.setOnClickListener(this);
    }

    private void activatePointButton() {
        point = mBinding.operandOperatorLayout.zeroToEquals.point;
        point.setOnClickListener(this);
    }

    private void activateEqualsButton() {
        equals = mBinding.operandOperatorLayout.zeroToEquals.equals;
        equals.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onClick(View view) {
        vibrateDevice();
        int viewID = view.getId();

        if (lastClickedButtonID == R.id.equals && viewID != R.id.equals) {
            mExpression.clear();
        }

        Log.i(TAG, "");
        if (viewID == R.id.equals) {
            Log.i(TAG, getString(R.string.equals) + " " + getString(R.string.was_clicked));

        } else if (viewID == R.id.delete_all) { // When the delete button is clicked
            Log.i(TAG, getString(R.string.delete_all_title) + " " + getString(R.string.was_clicked));
            clearInput();
            displayInput();

        } else if (viewID == R.id.delete) {
            mExpression.pop();
            displayInput();

        } else {
            if (operatorsID.containsKey(viewID)) {
                Log.i(TAG, operatorsID.get(viewID) + " " + getString(R.string.was_clicked));

                mExpression.add(operatorsID.get(viewID));
                if (mExpression.isValid()) {
                    displayInput();
                } else {
                    mExpression.pop();
                }

            } else if (operandsID.containsKey(viewID)) {
                Log.i(TAG, operandsID.get(viewID) + " " + getString(R.string.was_clicked));

                mExpression.add(operandsID.get(viewID));
                if (mExpression.isValid()) {
                    displayInput();
                } else {
                    mExpression.pop();
                }

            } else if (parenthesesID.containsKey(viewID)) {
                Log.i(TAG, parenthesesID.get(viewID) + " " + getString(R.string.was_clicked));

                mExpression.add(parenthesesID.get(viewID));
                if (mExpression.isValid()) {
                    displayInput();
                } else {
                    mExpression.pop();
                }
            }
        }

        displayResult();
        lastClickedButtonID = viewID;
    }

    private void clearInput() {
        mExpression.clear();
    }

    private void displayInput() {
        inputScreen.setText(mExpression.getReadableInput());
        inputScreen.setSelection(inputScreen.getText().length()); // Place cursor at end of
        
        Log.i(TAG, "Raw Input = '" + mExpression.getRawInput() + "'");
        Log.i(TAG, "Readable Input = '" + mExpression.getReadableInput() + "'");
        Log.i(TAG, "Edited Input = '" + mExpression.getEditedInput() + "'");
        Log.i(TAG, "Computational Input = '" + mExpression.getComputationInput() + "'");
    }

    private void vibrateDevice() {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
    }
    
    
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void displayResult() {
        Double result = mExpression.compute();
        if (result != null) {
            outputScreen.setText(String.format(Locale.getDefault(), "%f", result));
        } else {
            if (mExpression.getRawInput().isEmpty()) {
                outputScreen.setText("");
            } else {
                outputScreen.setText(outputScreen.getText());
            }
        }
    }

    @Override
    protected void onResume() {
        hideKeyboard();
        super.onResume();
    }

    @Override
    protected void onStart() {
        hideKeyboard();
        super.onStart();
    }

    private void hideKeyboard() {
        Log.i(TAG, "inputScreen = " + inputScreen);
        InputMethodManager imm = (InputMethodManager) getSystemService (Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow (inputScreen.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
