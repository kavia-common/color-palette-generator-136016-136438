package org.example.app.model

import kotlin.random.Random
import kotlin.system.measureTimeMillis

/**
 * Palette generation logic with visual distinctness enforcement.
 * Standalone and UI-agnostic for unit testing and reuse.
 */
class PaletteGenerator(
    private val rng: Random = Random.Default,
    private val maxAttempts: Int = 500
) {

    // PUBLIC_INTERFACE
    fun generateDistinctPalette(
        /** Number of colors to generate */
        size: Int = 5,
        /** Minimum distance in HSL space (0..1), tuned for good visual distinctness */
        minDistance: Float = 0.22f,
        /** Bounds for S and L to avoid very dark/light or washed-out colors */
        saturationRange: ClosedFloatingPointRange<Float> = 45f..85f,
        lightnessRange: ClosedFloatingPointRange<Float> = 35f..70f
    ): List<HslColor> {
        require(size > 0) { "Palette size must be > 0" }

        val palette = mutableListOf<HslColor>()
        var attempts = 0

        while (palette.size < size && attempts < maxAttempts) {
            attempts++
            val h = rng.nextInt(0, 360).toFloat()
            val s = rng.nextInt(saturationRange.start.toInt(), saturationRange.endInclusive.toInt() + 1).toFloat()
            val l = rng.nextInt(lightnessRange.start.toInt(), lightnessRange.endInclusive.toInt() + 1).toFloat()
            val candidate = HslColor(h, s, l)

            if (palette.none { it.distanceTo(candidate) < minDistance }) {
                palette.add(candidate)
            }
        }

        if (palette.size < size) {
            // Fallback: fill remaining slots with forced hue separation to guarantee size
            val remaining = size - palette.size
            val baseHueStep = 360f / (remaining + 1)
            var baseHue = rng.nextFloat() * 360f
            repeat(remaining) {
                baseHue = (baseHue + baseHueStep) % 360
                val s = rng.nextInt(55, 85).toFloat()
                val l = rng.nextInt(40, 65).toFloat()
                palette.add(HslColor(baseHue, s, l))
            }
        }

        return palette
    }
}

/**
 * Tiny debounce helper to guard rapid requests.
 */
class Debouncer(private val windowMillis: Long = 400L) {
    private var lastTime = 0L

    // PUBLIC_INTERFACE
    fun shouldProceed(now: Long = System.currentTimeMillis()): Boolean {
        val ok = (now - lastTime) >= windowMillis
        if (ok) lastTime = now
        return ok
    }
}
