package com.tbs.plugin1.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.lang.reflect.Proxy

object PMSHookHelper {
    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    fun hookPackageManager(context: Context?) {
        context ?: return
        try {
            // 获取全局的ActivityThread对象
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod =
                activityThreadClass.getDeclaredMethod("currentActivityThread")
            currentActivityThreadMethod.isAccessible = true
            val currentActivityThread = currentActivityThreadMethod.invoke(null)

            // 获取ActivityThread里面原始的 sPackageManager
            val sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager")
            sPackageManagerField.isAccessible = true
            val sPackageManager = sPackageManagerField[currentActivityThread]

            // 准备好代理对象, 用来替换原始的对象
            val iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager")
            val proxy = Proxy.newProxyInstance(
                iPackageManagerInterface.classLoader, arrayOf(iPackageManagerInterface),
                HookHandler(sPackageManager)
            )

            // 1. 替换掉ActivityThread里面的 sPackageManager 字段
            sPackageManagerField[currentActivityThread] = proxy

            // 2. 替换 ApplicationPackageManager里面的 mPm对象
            val pm = context.packageManager
            val mPmField = pm.javaClass.getDeclaredField("mPM")
            mPmField.isAccessible = true
            mPmField[pm] = proxy
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PMSHookHelper", "Hook failed")
        }
    }
}