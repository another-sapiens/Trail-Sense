package com.kylecorry.trail_sense.tools.backtrack.infrastructure.services

import android.app.Notification
import android.content.Context
import android.content.Intent
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.navigation.infrastructure.persistence.BeaconRepo
import com.kylecorry.trail_sense.shared.FormatServiceV2
import com.kylecorry.trail_sense.shared.NavigationUtils
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trail_sense.shared.sensors.SensorService
import com.kylecorry.trail_sense.tools.backtrack.domain.Backtrack
import com.kylecorry.trail_sense.tools.backtrack.infrastructure.persistence.WaypointRepo
import com.kylecorry.trail_sense.tools.backtrack.infrastructure.receivers.StopBacktrackReceiver
import com.kylecorry.trailsensecore.infrastructure.services.CoroutineIntervalService
import com.kylecorry.trailsensecore.infrastructure.system.IntentUtils
import com.kylecorry.trailsensecore.infrastructure.system.NotificationUtils
import java.time.Duration

class BacktrackAlwaysOnService : CoroutineIntervalService(TAG) {
    private val gps by lazy { sensorService.getGPS(true) }
    private val cellSignal by lazy { sensorService.getCellSignal(true) }
    private val sensorService by lazy { SensorService(applicationContext) }
    private val waypointRepo by lazy { WaypointRepo.getInstance(applicationContext) }
    private val beaconRepo by lazy { BeaconRepo.getInstance(applicationContext) }
    private val prefs by lazy { UserPreferences(applicationContext) }
    private val formatService by lazy { FormatServiceV2(this) }

    private val backtrack by lazy {
        Backtrack(this, gps, cellSignal, waypointRepo, beaconRepo, prefs.backtrackSaveCellHistory)
    }

    override val foregroundNotificationId: Int
        get() = 578879

    override val period: Duration
        get() = prefs.backtrackRecordFrequency

    override fun getForegroundNotification(): Notification {
        val openAction = NavigationUtils.pendingIntent(this, R.id.fragmentBacktrack)

        val stopAction = NotificationUtils.action(
            getString(R.string.stop_monitoring),
            StopBacktrackReceiver.pendingIntent(this),
            R.drawable.ic_cancel
        )

        return NotificationUtils.persistent(
            this,
            FOREGROUND_CHANNEL_ID,
            getString(R.string.backtrack_notification_channel),
            getString(
                R.string.backtrack_high_priority_notification,
                formatService.formatDuration(prefs.backtrackRecordFrequency)
            ),
            R.drawable.ic_tool_backtrack,
            intent = openAction,
            actions = listOf(stopAction)
        )
    }

    override suspend fun doWork() {
        backtrack.recordLocation()
    }

    override fun onDestroy() {
        stopService(true)
        super.onDestroy()
    }

    companion object {
        const val TAG = "BacktrackHighPriorityService"
        const val FOREGROUND_CHANNEL_ID = "Backtrack"

        fun intent(context: Context): Intent {
            return Intent(context, BacktrackAlwaysOnService::class.java)
        }

        fun start(context: Context) {
            IntentUtils.startService(context, intent(context), foreground = true)
        }

    }

}