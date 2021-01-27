//package com.test.utils
//
//import android.app.Activity
//import android.content.Context
//import android.util.AttributeSet
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toolbar
//import androidx.appcompat.app.AppCompatActivity
//import com.test.R
//
//class Toolbar(context: Context, attrs: AttributeSet): Toolbar(context, attrs) {
//
//    init {
//        inflate(context, R.layout.toolbar, this)
//
//        val title: TextView = findViewById(R.id.toolbar_title)
//        val backButton: ImageView = findViewById(R.id.button_back)
//
//        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Toolbar)
//        title.text = attributes.getText(R.styleable.Toolbar_title)
//
//        backButton.setOnClickListener{
//            if (context is Activity || context is AppCompatActivity) {
//                (context as Activity).onBackPressed()
//            }
//        }
//
//        attributes.recycle()
//    }
//
//}