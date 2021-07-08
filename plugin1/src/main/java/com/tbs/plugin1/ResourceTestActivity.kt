package com.tbs.plugin1

import android.os.Bundle
import android.view.LayoutInflater
import com.tbs.plugin1.base.BaseActivity

class ResourceTestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = LayoutInflater.from(getContext()).inflate(R.layout.activity_main, null)
        setContentView(view)
    }
}