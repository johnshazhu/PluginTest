package com.tbs.plugin1.base

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AppCompatActivity
import com.tbs.plugin1.BuildConfig
import com.tbs.plugin1.util.PMSHookHelper
import com.tbs.plugin1.util.Util

open class BaseActivity : AppCompatActivity() {
    private var context: Context? = null
    private val isPluginApk = BuildConfig.isPluginApk

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (isPluginApk) {
            PMSHookHelper.hookPackageManager(base)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isPluginApk) {
            val cls = Class.forName("android.app.ContextImpl")
            val packageInfoField = cls.getDeclaredField("mPackageInfo")
            packageInfoField.isAccessible = true
            if (baseContext is ContextWrapper) {
                val packageInfo = packageInfoField.get((baseContext as ContextWrapper).baseContext)

                val packageNameField = packageInfo.javaClass.getDeclaredField("mPackageName")
                packageNameField.isAccessible = true
                packageNameField.set(packageInfo, "com.tbs.plugin1")
            }
        }
        super.onCreate(savedInstanceState)

        if (isPluginApk) {
            val applicationCls = application.javaClass
            val pathField = applicationCls.getDeclaredField("pluginPath")
            pathField.isAccessible = true
            val pluginPath = pathField.get(application) as String?
            Log.i("BaseActivity", "pluginPath : $pluginPath")

            val resources = Util.getLoadedResource(applicationContext, pluginPath)
            context = ContextThemeWrapper(baseContext, 0)
            try {
                val resourcesField = context!!.javaClass.getDeclaredField("mResources")
                resourcesField.isAccessible = true
                resourcesField.set(context, resources)
            } catch (r: Throwable) {
                r.printStackTrace()
            }
        }
    }

    fun getContext(): Context? {
        return if (isPluginApk) context else this
    }
}