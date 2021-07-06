package com.tbs.myapplication

import android.app.Application
import android.content.Context
import android.os.FileUtils
import android.util.Log
import com.tbs.myapplication.hook.AMSHookHelper
import com.tbs.myapplication.hook.BaseDexClassLoaderHookHelper
import com.tbs.myapplication.loader.PluginClassLoader
import com.tbs.myapplication.ui.main.MainFragment
import java.io.File
import java.io.FileOutputStream

class BaseApplication : Application() {
    private lateinit var pluginLoader: PluginClassLoader

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        base?.let {
            val apkFile = copyPluginToInternalStorage(it)
            apkFile ?: return

            pluginLoader = PluginClassLoader(apkFile.absolutePath,
                it.cacheDir?.absolutePath,
                null,
                it.classLoader)

            val optDexFile = "${it.cacheDir?.absolutePath}/plugin/1.dex"
            val dexFile = File(optDexFile)
            if (!dexFile.exists()) {
                dexFile.createNewFile()
            }
            BaseDexClassLoaderHookHelper.patchClassLoader(classLoader, apkFile, dexFile)
        }
    }

    private fun copyPluginToInternalStorage(context: Context) : File? {
        Log.i(AMSHookHelper.TAG, "copyPluginToInternalStorage : ${context.cacheDir?.absolutePath}")

        val cacheDir = context.cacheDir?.absolutePath
        cacheDir ?: return null

        val apkDir = "$cacheDir/plugin"
        val apkDirFile = File(apkDir)
        var success = true
        if (!apkDirFile.exists()) {
            success = apkDirFile.mkdir()
        }

        if (success) {
            val apkFile = File("$apkDir/1.apk")
            if (!apkFile.exists()) {
                success = apkFile.createNewFile()
                if (success) {
                    val inputStream = context.assets?.open(MainFragment.PLUGIN_NAME)
                    inputStream ?: return null

                    val fileOutputStream = FileOutputStream(apkFile)
                    try {
                        FileUtils.copy(inputStream, fileOutputStream)
                    } catch (r : Throwable) {
                        r.printStackTrace()
                    } finally {
                        inputStream.close()
                        fileOutputStream.close()
                    }
                }
            }
            return apkFile
        }

        return null
    }

    fun getPluginLoader() = pluginLoader
}