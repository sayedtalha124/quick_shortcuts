package app.quickshortcuts

import android.annotation.SuppressLint
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.getActivity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.Q
import android.os.Build.VERSION_CODES.TIRAMISU
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.Tile.STATE_ACTIVE
import android.service.quicksettings.Tile.STATE_INACTIVE
import android.service.quicksettings.TileService
import app.quickshortcuts.SpeedDialNotification.find
import app.quickshortcuts.SpeedDialNotification.handle
import app.quickshortcuts.SpeedDialNotification.notificationManager
import app.quickshortcuts.SpeedDialNotification.toggle
import app.quickshortcuts.core.getUserPreference
import app.quickshortcuts.core.setSpeedDial
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.DateFormat.SHORT
import java.text.DateFormat.getTimeInstance
import java.util.Date
import kotlin.jvm.java

class SpeedDialTileService : TileService() {
    companion object {
        fun Context.requestTileUpdate() = requestListeningState(this, ComponentName(this, SpeedDialTileService::class.java))
    }
    override fun onStartListening() = refreshTile()


    override fun onTileAdded() {
        // This method is called when the tile is first added.
        qsTile.state = Tile.STATE_INACTIVE
        qsTile.updateTile()
        GlobalScope.launch {
            setSpeedDial(this@SpeedDialTileService, true)
        }
        showNotification()
    }
    /* override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         GlobalScope.launch {
             getUserPreference(this@SpeedDialTileService).collectLatest {
                 handle(intent, it)
             }

         }
         requestTileUpdate()
         stopSelfResult(startId)
         return START_NOT_STICKY
     }*/
    fun showNotification(){
        GlobalScope.launch {
            getUserPreference(this@SpeedDialTileService).collectLatest {
                when (notificationManager()?.areNotificationsEnabled()) {
                    true -> toggle(it).also { refreshTile() }
                    else -> requestNotificationsPermission()
                }
            }

        }
    }

    override fun onClick() {
        // This method is called when the user clicks on the tile.
        qsTile.state = if (qsTile.state == Tile.STATE_ACTIVE) {
            Tile.STATE_INACTIVE
        } else {
            Tile.STATE_ACTIVE
        }
        GlobalScope.launch {
            setSpeedDial(this@SpeedDialTileService, true)
        }
        showNotification()

        qsTile.updateTile()
    }
    private fun refreshTile() = qsTile?.run {
        state = when (find()) {
            null -> {
                STATE_INACTIVE
            }

            else -> {
                STATE_ACTIVE
            }
        }
        updateTile()
    } ?: Unit
    private fun requestNotificationsPermission() =
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }.let {
            @SuppressLint("StartActivityAndCollapseDeprecated")
            if (SDK_INT <= TIRAMISU) @Suppress("DEPRECATION") startActivityAndCollapse(it)
            else startActivityAndCollapse(getActivity(this, 0, it, FLAG_IMMUTABLE))
        }

    override fun onTileRemoved() {
        // This method is called when the tile is removed.
        GlobalScope.launch {
            setSpeedDial(this@SpeedDialTileService, false)
        }
    }

}
