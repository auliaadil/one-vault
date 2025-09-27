package com.adilstudio.project.onevault.service

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.adilstudio.project.onevault.MainActivity

@RequiresApi(Build.VERSION_CODES.N)
class ScanBillTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        // Open the app and show scanner dialog immediately
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to", "bill_list")
            putExtra("show_scanner", true)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            startActivityAndCollapse(pendingIntent)
        } else {
            startActivityAndCollapse(intent)
        }
    }

    private fun updateTile() {
        qsTile?.let { tile ->
            tile.label = "Scan Bill"
            tile.contentDescription = "Quick scan bill with camera"
            tile.state = android.service.quicksettings.Tile.STATE_INACTIVE
            tile.updateTile()
        }
    }
}
