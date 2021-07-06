package com.tbs.myapplication.loader

import android.util.Log
import dalvik.system.DexClassLoader

open class PluginClassLoader(
    path: String?,
    dir: String?,
    libSearchPath: String?,
    parentLoader: ClassLoader?
) : DexClassLoader(path, dir, libSearchPath, parentLoader) {
    init {
        Log.i("PluginClassLoader", "$path, $dir")
    }
}