package com.tbs.myapplication.hook

import android.os.Build
import android.util.Log
import dalvik.system.DexClassLoader
import dalvik.system.DexFile
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException

object BaseDexClassLoaderHookHelper {
    @Throws(
        InvocationTargetException::class,
        IllegalAccessException::class,
        NoSuchFieldException::class,
        NoSuchMethodException::class,
        IOException::class,
        InstantiationException::class
    )
    fun patchClassLoader(cl: ClassLoader, apkFile: File, optDexFile: File) {
        val pathListField = DexClassLoader::class.java.superclass.getDeclaredField("pathList")
        pathListField.isAccessible = true
        val pathListObj = pathListField.get(cl)

        val dexElementArrayField = pathListObj.javaClass.getDeclaredField("dexElements")
        dexElementArrayField.isAccessible = true
        val dexElements: Array<Any> = dexElementArrayField.get(pathListObj) as Array<Any>

        val elementClass = dexElements.javaClass.componentType
        Log.i(AMSHookHelper.TAG, "patchClassLoader element $elementClass")

        // 创建一个数组, 用来替换原始的数组
        var newElements = dexElements.copyOf()
        val dexFile = DexFile.loadDex(apkFile.canonicalPath, optDexFile.absolutePath, 0)
        val addObj = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val constructor = elementClass.getConstructor(DexFile::class.java, File::class.java)
            constructor.newInstance(dexFile, apkFile)
        } else {
            val constructor = elementClass.getConstructor(
                File::class.java,
                Boolean::class.javaPrimitiveType,
                File::class.java,
                DexFile::class.java
            )
            constructor.newInstance(apkFile, false, apkFile, dexFile)
        }
        newElements = newElements.plus(addObj)

        dexElementArrayField.set(pathListObj, newElements)

        Log.i(AMSHookHelper.TAG, "patchClassLoader element ${newElements.size}")
    }
}