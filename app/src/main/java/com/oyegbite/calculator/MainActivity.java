package com.oyegbite.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.oyegbite.calculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityMainBinding binding;

    private EditText inputScreen;
    private TextView outputScreen;

    private TextView deleteAll;
    private TextView openParen;
    private TextView closeParen;
    private TextView divide;

    private TextView seven;
    private TextView eight;
    private TextView nine;
    private TextView multiply;

    private TextView four;
    private TextView five;
    private TextView six;
    private TextView minus;

    private TextView one;
    private TextView two;
    private TextView three;
    private TextView plus;

    private TextView zero;
    private TextView point;
    private TextView equals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView( R.layout.activity_main);
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        hideKeyboard();

        StringBuilder inputString = new StringBuilder();

        activateInputScreen();
        activateOutputScreen();

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

    private void activateInputScreen() {
        inputScreen = findViewById(R.id.input_screen);
    }

    private void activateOutputScreen() {
        outputScreen= findViewById(R.id.output_screen);
    }

    private void activateDeleteAllButton() {
        deleteAll = findViewById(R.id.delete_all);
        deleteAll.setOnClickListener(this);
    }

    private void activateOpenParenButton() {
        openParen = findViewById(R.id.open_paren);
        openParen.setOnClickListener(this);
    }

    private void activateCloseParenButton() {
        closeParen = findViewById(R.id.close_paren);
        closeParen.setOnClickListener(this);
    }

    private void activateDivideButton() {
        divide = findViewById(R.id.divide);
        divide.setOnClickListener(this);
    }

    private void activateSevenButton() {
        seven = findViewById(R.id.seven);
        seven.setOnClickListener(this);
    }

    private void activateEightButton() {
        eight = findViewById(R.id.eight);
        eight.setOnClickListener(this);
    }

    private void activateNineButton() {
        nine = findViewById(R.id.nine);
        nine.setOnClickListener(this);
    }

    private void activateMultiplyButton() {
        multiply = findViewById(R.id.multiply);
        multiply.setOnClickListener(this);
    }

    private void activateFourButton() {
        four = findViewById(R.id.four);
        four.setOnClickListener(this);
    }

    private void activateFiveButton() {
        five = findViewById(R.id.five);
        five.setOnClickListener(this);
    }

    private void activateSixButton() {
        six = findViewById(R.id.six);
        six.setOnClickListener(this);
    }

    private void activateMinusButton() {
        minus = findViewById(R.id.minus);
        minus.setOnClickListener(this);
    }

    private void activateOneButton() {
        one = findViewById(R.id.one);
        one.setOnClickListener(this);
    }

    private void activateTwoButton() {
        two = findViewById(R.id.two);
        two.setOnClickListener(this);
    }

    private void activateThreeButton() {
        three = findViewById(R.id.three);
        three.setOnClickListener(this);
    }

    private void activatePlusButton() {
        plus = findViewById(R.id.plus);
        plus.setOnClickListener(this);
    }

    private void activateZeroButton() {
        zero = findViewById(R.id.zero);
        zero.setOnClickListener(this);
    }

    private void activatePointButton() {
        point = findViewById(R.id.point);
        point.setOnClickListener(this);
    }

    private void activateEqualsButton() {
        equals = findViewById(R.id.equals);
        equals.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        vibrate();
    }

    private void vibrate() {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(40);
    }

    @Override
    protected void onResume() {
        hideKeyboard();
        super.onResume();
    }

    private void hideKeyboard() {
    }
}
