package com.test;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Button;

import androidx.appcompat.widget.AppCompatButton;

public class CustomButtonExample extends AppCompatButton {


    public CustomButtonExample(Context context) {
        super(context);
        this.setTextColor(Color.BLUE);
        this.setText("This button is mad from native");
    }


}
