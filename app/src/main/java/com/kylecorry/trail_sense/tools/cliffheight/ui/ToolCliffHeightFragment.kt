package com.kylecorry.trail_sense.tools.cliffheight.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kylecorry.trail_sense.R
import com.kylecorry.trail_sense.databinding.FragmentToolCliffHeightBinding
import com.kylecorry.trail_sense.shared.FormatService
import com.kylecorry.trail_sense.shared.UserPreferences
import com.kylecorry.trailsensecore.domain.physics.PhysicsService
import com.kylecorry.trailsensecore.domain.units.DistanceUnits
import com.kylecorry.trailsensecore.infrastructure.persistence.Cache
import com.kylecorry.trailsensecore.infrastructure.system.UiUtils
import com.kylecorry.trailsensecore.infrastructure.time.Intervalometer
import com.kylecorry.trailsensecore.infrastructure.view.BoundFragment
import java.time.Duration
import java.time.Instant

class ToolCliffHeightFragment : BoundFragment<FragmentToolCliffHeightBinding>() {

    private val physicsService = PhysicsService()
    private val intervalometer = Intervalometer {
        update()
    }
    private val formatService by lazy { FormatService(requireContext()) }
    private val userPrefs by lazy { UserPreferences(requireContext()) }
    private val cache by lazy { Cache(requireContext()) }

    private lateinit var units: DistanceUnits
    private var startTime: Instant? = null
    private var running = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startBtn.setOnClickListener {
            if (running) {
                intervalometer.stop()
                running = false
            } else {
                startTime = Instant.now()
                intervalometer.interval(16)
                running = true
            }
            binding.startBtn.setState(running)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (cache.getBoolean("cache_dialog_tool_cliff_height") != true) {
            UiUtils.alert(
                requireContext(),
                getString(R.string.disclaimer_message_title),
                getString(R.string.tool_cliff_height_disclaimer)
            ) {
                cache.putBoolean("cache_dialog_tool_cliff_height", true)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        units = if (userPrefs.distanceUnits == UserPreferences.DistanceUnits.Meters) {
            DistanceUnits.Meters
        } else {
            DistanceUnits.Feet
        }
    }

    override fun onPause() {
        super.onPause()
        intervalometer.stop()
    }

    fun update() {
        if (startTime == null) {
            return
        }

        val duration = Duration.between(startTime, Instant.now())
        val height = physicsService.fallHeight(duration)
        val converted = height.convertTo(units).distance
        val formatted = formatService.formatDepth(converted, units)

        binding.height.text = formatted
    }

    override fun generateBinding(
        layoutInflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentToolCliffHeightBinding {
        return FragmentToolCliffHeightBinding.inflate(layoutInflater, container, false)
    }

}