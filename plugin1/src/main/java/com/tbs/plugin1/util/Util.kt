package com.tbs.plugin1.util

import android.content.Context
import android.content.res.AssetManager
import android.content.res.Resources
import android.util.Log
import android.widget.Toast

object Util {
    private const val TAG = "Util"
    private var resource: Resources? = null

    fun test(context: Context?) {
        context ?: return
        Log.i(TAG, "test")
        Toast.makeText(context, "toast in plugin apk file", Toast.LENGTH_SHORT).show()
    }

    fun getLoadedResource(context: Context?, path: String?): Resources? {
        return resource ?: loadResource(context, path)
    }

    private fun loadResource(context: Context?, path: String?): Resources? {
        context ?: return null
        path ?: return null

        try {
            val assetManager = AssetManager::class.java.newInstance()
            val addAssetPathMethod = AssetManager::class.java.getMethod("addAssetPath", String::class.java)
            addAssetPathMethod.isAccessible = true
            addAssetPathMethod.invoke(assetManager, path)

            resource = Resources(assetManager, context.resources.displayMetrics, context.resources.configuration)
            return resource
        } catch (r : Throwable) {
            r.printStackTrace()
        }
        return null
    }
}