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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.oyegbite.calculator.databinding.ActivityMainBinding;
import com.oyegbite.calculator.utils.AppUtils;
import com.oyegbite.calculator.utils.Expression;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private Toast mToast;
    private InputMethodManager mImm;

    private Map<Integer, String> operandsID;
    private Map<Integer, String> operatorsID;
    private Map<Integer, String> parenthesesID;

    private EditText inputScreen;
    private TextView outputScreen;
    
    private Expression mExpression;
    private int lastClickedButtonID;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setBinding() {
        activateInputScreen();
        activateOutputScreen();
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
            put(R.id.exponent, getString(R.string.exponent));
        }};
    }

    private void setParenthesesID() {
        parenthesesID = new HashMap<Integer, String>() {{
            put(R.id.open_paren, getString(R.string.open_paren));
            put(R.id.close_paren, getString(R.string.close_paren));
        }};
    }

    private void activateOutputScreen() {
        outputScreen = mBinding.screen.outputScreen;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void activateInputScreen() {
        inputScreen = mBinding.screen.inputScreen;
//        inputScreen.setSelection(inputScreen.getText().length());
//        inputScreen.requestFocus();
        inputScreen.setShowSoftInputOnFocus(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onClick(View view) {
        vibrateDevice();
        int viewID = view.getId();

        Log.i(TAG, "inputScreen = '" + inputScreen.getText().toString() + "'");
        Log.i(TAG, "inputScreen.getSelectionStart() = " + inputScreen.getSelectionStart());
        Log.i(TAG, "inputScreen.getSelectionEnd() = " + inputScreen.getSelectionEnd());
        int cursorPosStart = Math.max(0, inputScreen.getSelectionStart());
        int cursorPosEnd = Math.max(0, inputScreen.getSelectionEnd());

        if (viewID == R.id.equals) {
            Log.i(TAG, getString(R.string.equals) + " " + getString(R.string.was_clicked));
            displayFinalResult();
            lastClickedButtonID = viewID;
            return;

        } else if (viewID == R.id.clear) { // When the backspace button is clicked
            Log.i(TAG, getString(R.string.clear_title) + " " + getString(R.string.was_clicked));
            clearInput();
            displayInput();

        } else if (viewID == R.id.backspace) {
            mExpression.pop();
            displayInput();

        } else {
            if (operatorsID.containsKey(viewID)) {
                Log.i(TAG, operatorsID.get(viewID) + " " + getString(R.string.was_clicked));

                mExpression.add(operatorsID.get(viewID), cursorPosStart, cursorPosEnd, inputScreen);
                if (mExpression.isValid()) {
                    displayInput();
                } else {
                    mExpression.pop();
                }

            } else if (operandsID.containsKey(viewID)) {
                Log.i(TAG, operandsID.get(viewID) + " " + getString(R.string.was_clicked));

                mExpression.add(operandsID.get(viewID), cursorPosStart, cursorPosEnd, inputScreen);
                if (mExpression.isValid()) {
                    displayInput();
                } else {
                    mExpression.pop();
                }

            } else if (parenthesesID.containsKey(viewID)) {
                Log.i(TAG, parenthesesID.get(viewID) + " " + getString(R.string.was_clicked));

                mExpression.add(parenthesesID.get(viewID), cursorPosStart, cursorPosEnd, inputScreen);
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
        inputScreen.setText(mExpression.getReadableInput(), TextView.BufferType.SPANNABLE);
//        inputScreen.setSelection(inputScreen.getText().length()); // Place cursor at end of string
        
        Log.i(TAG, "Raw Input = '" + mExpression.getRawInput() + "'");
        Log.i(TAG, "Readable Input = '" + mExpression.getReadableInput() + "'");
        Log.i(TAG, "Edited Input = '" + mExpression.getEditedInput() + "'");
        Log.i(TAG, "Computational Input = '" + mExpression.getComputationInput() + "'");
    }
    
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void displayResult() {
        String result = mExpression.compute();
        if (result != null) {
            if (result.equals("Infinity")) {
                mToast = AppUtils.showToastLong(
                        this,
                        getString(R.string.cant_divide_by_zero),
                        mToast,
                        Toast.LENGTH_SHORT
                );
                return;
            }
            outputScreen.setText(result);

        } else {
            if (mExpression.getRawInput().isEmpty()) {
                outputScreen.setText("");
            } else {
                outputScreen.setText(outputScreen.getText());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void displayFinalResult() {
        String result = mExpression.compute();
        if (result != null) {
            if (result.equals("Infinity")) {
                mToast = AppUtils.showToastLong(
                        this,
                        getString(R.string.cant_divide_by_zero),
                        mToast,
                        Toast.LENGTH_SHORT
                );
                return;
            }
            mExpression.clear();
            int cursorPosStart = Math.max(0, inputScreen.getSelectionStart());
            int cursorPosEnd = Math.max(0, inputScreen.getSelectionEnd());
            mExpression.add(result, cursorPosStart, cursorPosEnd, inputScreen);
            inputScreen.setText(result);
//            inputScreen.setSelection(inputScreen.getText().length());
            outputScreen.setText("");

        } else {
            if (mExpression.getRawInput().isEmpty()) {
                outputScreen.setText("");
            } else {
                outputScreen.setText(getString(R.string.error));
            }
        }
    }

    private void vibrateDevice() {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
    }

}
