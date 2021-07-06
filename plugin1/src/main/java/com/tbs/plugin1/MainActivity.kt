package com.tbs.plugin1

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.RelativeLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("MainActivity", "onCreate com.tbs.plugin1.MainActivity")
        val root = RelativeLayout(this)
        root.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT)
        root.setBackgroundColor(0x55CEAC or (0xFF shl 24))

        val helloWorld = TextView(this)
        helloWorld.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        helloWorld.text = "Hello World!"
        helloWorld.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        helloWorld.setTextColor(resources.getColor(R.color.white))
        (helloWorld.layoutParams as RelativeLayout.LayoutParams).rules[RelativeLayout.CENTER_HORIZONTAL] =
            RelativeLayout.TRUE
        (helloWorld.layoutParams as RelativeLayout.LayoutParams).topMargin = 120
        root.addView(helloWorld)

        val addTextView = TextView(this)
        addTextView.layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        addTextView.text = "Test added View"
        addTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
        addTextView.setTextColor(resources.getColor(R.color.design_default_color_primary))
        (addTextView.layoutParams as RelativeLayout.LayoutParams).rules[RelativeLayout.CENTER_HORIZONTAL] =
            RelativeLayout.TRUE
        (addTextView.layoutParams as RelativeLayout.LayoutParams).topMargin =
            360
        root.addView(addTextView)

        setContentView(root)
    }
}