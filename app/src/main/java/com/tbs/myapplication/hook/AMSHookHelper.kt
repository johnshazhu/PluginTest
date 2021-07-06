package com.tbs.myapplication.hook

import android.annotation.SuppressLint
import android.os.Build
import android.os.Handler
import java.lang.reflect.Field
import java.lang.reflect.Proxy

object AMSHookHelper {
    const val TAG = "AMSHookHelper"
    const val EXTRA_TARGET_INTENT = "extra_target_intent"

    @SuppressLint("PrivateApi")
    @Throws(ClassNotFoundException::class, IllegalAccessException::class, NoSuchFieldException::class)
    fun execStartActivityEntry() {
        val managerCls: Class<*>?
        val instanceField: Field?
        val iManagerInterfaceCls: Class<*>?

        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                managerCls = Class.forName("android.app.ActivityTaskManager")
                instanceField = managerCls.getDeclaredField("IActivityTaskManagerSingleton")
                iManagerInterfaceCls = Class.forName("android.app.IActivityTaskManager")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                managerCls = Class.forName("android.app.ActivityManager")
                instanceField = managerCls.getDeclaredField("IActivityManagerSingleton")
                iManagerInterfaceCls = Class.forName("android.app.IActivityManager")
            }
            else -> {
                managerCls = Class.forName("android.app.ActivityManagerNative")
                instanceField = managerCls.getDeclaredField("gDefault")
                iManagerInterfaceCls = Class.forName("android.app.IActivityManager")
            }
        }

        managerCls ?: return
        instanceField ?: return

        instanceField.isAccessible = true

        val instanceObj = instanceField.get(null)

        val singletonCls = Class.forName("android.util.Singleton")
        val singletonInstanceField = singletonCls.getDeclaredField("mInstance")
        singletonInstanceField.isAccessible = true

        val iActivityOrTaskManager = singletonInstanceField.get(instanceObj)
        val proxy = Proxy.newProxyInstance(Thread.currentThread().contextClassLoader,
            arrayOf(iManagerInterfaceCls),
            IActivityManagerHandler(iActivityOrTaskManager))
        singletonInstanceField.set(instanceObj, proxy)
    }

    @SuppressLint("PrivateApi")
    @Throws(ClassNotFoundException::class, IllegalAccessException::class, NoSuchFieldException::class)
    fun activityThreadHandler() {
        val activityThreadCls = Class.forName("android.app.ActivityThread")
        val curActivityThreadField = activityThreadCls.getDeclaredField("sCurrentActivityThread")
        curActivityThreadField.isAccessible = true
        val curActivityThread = curActivityThreadField.get(null)

        val mHField = activityThreadCls.getDeclaredField("mH")
        mHField.isAccessible = true
        val handler: Handler = mHField.get(curActivityThread) as Handler

        val msgId: Int
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
//            val msgIdField = mHObj.javaClass.getDeclaredField("EXECUTE_TRANSACTION")
//            msgIdField.isAccessible = true
            msgId = 159//msgIdField.get(mHObj)
        } else {
//            val msgIdField = mHObj.javaClass.getDeclaredField("LAUNCH_ACTIVITY")
//            msgIdField.isAccessible = true
            msgId = 100//msgIdField.get(mHObj)
        }

        val callbacksField = Handler::class.java.getDeclaredField("mCallback")
        callbacksField.isAccessible = true
        callbacksField.set(handler, ActivityThreadHandlerCallback(handler, msgId))
    }
}