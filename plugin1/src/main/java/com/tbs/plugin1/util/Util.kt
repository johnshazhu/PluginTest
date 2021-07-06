package com.tbs.plugin1.util

import android.content.Context
import android.widget.Toast

object Util {
    fun test(context: Context?) {
        context ?: return
        Toast.makeText(context, "toast in plugin apk file", Toast.LENGTH_SHORT).show()
    }
}