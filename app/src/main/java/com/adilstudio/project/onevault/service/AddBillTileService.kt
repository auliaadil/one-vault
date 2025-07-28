package com.adilstudio.project.onevault.service

import android.app.PendingIntent
import android.content.Intent
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import android.os.Build
import com.adilstudio.project.onevault.MainActivity

@RequiresApi(Build.VERSION_CODES.N)
class AddBillTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        // Update tile state when it becomes visible
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        // Create intent to open the app and navigate to Add Bill screen
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "add_bill")
        }

        // For Android 14+ (API 34+), use the new PendingIntent method
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            // For older versions, use the legacy Intent method
            startActivityAndCollapse(intent)
        }
    }

    private fun updateTile() {
        qsTile?.let { tile ->
            tile.label = "Add Bill"
            tile.contentDescription = "Quick add bill to OneVault"
            tile.state = android.service.quicksettings.Tile.STATE_INACTIVE
            tile.updateTile()
        }
    }
}
