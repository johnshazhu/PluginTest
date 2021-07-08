package com.tbs.plugin1.util

import android.content.ComponentName
import android.content.pm.ActivityInfo
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

class HookHandler(private val manager: Any?) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any? {
        if ("getActivityInfo" == method?.name && (args?.size?:0) > 0) {
            val component = args!![0]
            if (component is ComponentName) {
                val activityInfo = ActivityInfo()
                activityInfo.packageName = component.packageName
                activityInfo.name = component.className
                activityInfo.taskAffinity = activityInfo.packageName
                activityInfo.configChanges = ActivityInfo.CONFIG_MCC or ActivityInfo.CONFIG_MNC
                return activityInfo
            }
        }

        if (args == null) {
            return method?.invoke(manager)
        }

        return method?.invoke(manager, *args)
    }
}