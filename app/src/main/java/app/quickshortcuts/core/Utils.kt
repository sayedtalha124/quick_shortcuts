package app.quickshortcuts.core

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import app.quickshortcuts.R

import java.util.regex.Matcher
import java.util.regex.Pattern




object Utils {
    fun requestAddTileService(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val statusBarManager =
                context.getSystemService(Context.STATUS_BAR_SERVICE)
                        as StatusBarManager
            statusBarManager.requestAddTileService(
                ComponentName(
                    context.packageName,
                    "${context.packageName}.SpeedDialTileService",
                ),
                "Speed Dial",
                Icon.createWithResource(
                    context,
                    R.drawable.phone
                ),

                {},
                {}
            )
        }

    }
    fun showLog(content: String) {
        val builder: String =
            content
        Log.e("LOG_MESSAGE", "showLog: $builder")
    }
}