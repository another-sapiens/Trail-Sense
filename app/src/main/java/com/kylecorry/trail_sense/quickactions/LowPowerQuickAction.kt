package com.kylecorry.trail_sense.quickactions

import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.shared.CustomUiUtils
import com.kylecorry.trail_sense.shared.LowPowerMode
import com.kylecorry.trail_sense.shared.QuickActionButton

class LowPowerQuickAction(button: FloatingActionButton, fragment: Fragment): QuickActionButton(button, fragment) {

    private val lowerPowerMode by lazy { LowPowerMode(context) }

    private fun update() {
        CustomUiUtils.setButtonState(
            button,
            lowerPowerMode.isEnabled()
        )
    }

    override fun onCreate() {
        button.setImageResource(R.drawable.ic_tool_battery)
        button.setOnClickListener {
            if (lowerPowerMode.isEnabled()){
                lowerPowerMode.disable(fragment.activity)
            } else {
                lowerPowerMode.enable(fragment.activity)
            }
        }
        update()
    }

    override fun onResume() {
        update()
    }

    override fun onPause() {
    }

    override fun onDestroy() {
    }
}