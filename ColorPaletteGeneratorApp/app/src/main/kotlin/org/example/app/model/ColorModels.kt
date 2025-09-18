package org.example.app.model

import android.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Data models and conversions for color palette items.
 * This module is UI-agnostic and intended to be unit-testable and reusable.
 */

// PUBLIC_INTERFACE
data class HslColor(
    /** Hue 0..360 */
    val h: Float,
    /** Saturation 0..100 */
    val s: Float,
    /** Lightness 0..100 */
    val l: Float
) {
    /** This returns a hex string #RRGGBB. */
    fun toHex(): String {
        val rgb = toRgb()
        return String.format("#%02X%02X%02X", rgb.r, rgb.g, rgb.b)
    }

    /** Convert to Android packed ARGB int (opaque) */
    fun toColorInt(): Int {
        val rgb = toRgb()
        return Color.rgb(rgb.r, rgb.g, rgb.b)
    }

    /** Convert to human readable HSL string e.g., hsl(210, 60%, 50%) */
    fun toHslString(): String = "hsl(${h.toInt()}, ${s.toInt()}%, ${l.toInt()}%)"

    private fun toRgb(): Rgb {
        // Convert HSL to RGB (0..255)
        val sNorm = s / 100f
        val lNorm = l / 100f

        val c = (1 - abs(2 * lNorm - 1)) * sNorm
        val hPrime = (h / 60f)
        val x = c * (1 - abs(hPrime % 2 - 1))
        val (r1, g1, b1) = when {
            hPrime < 1 -> Triple(c, x, 0f)
            hPrime < 2 -> Triple(x, c, 0f)
            hPrime < 3 -> Triple(0f, c, x)
            hPrime < 4 -> Triple(0f, x, c)
            hPrime < 5 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        val m = lNorm - c / 2f
        return Rgb(
            ((r1 + m) * 255).toInt().coerceIn(0, 255),
            ((g1 + m) * 255).toInt().coerceIn(0, 255),
            ((b1 + m) * 255).toInt().coerceIn(0, 255)
        )
    }

    /** Simple distance metric in HSL space with circular hue consideration. */
    fun distanceTo(other: HslColor): Float {
        val dh = min(abs(h - other.h), 360 - abs(h - other.h)) / 360f
        val ds = abs(s - other.s) / 100f
        val dl = abs(l - other.l) / 100f
        return (dh * 0.6f) + (ds * 0.2f) + (dl * 0.2f)
    }
}

data class Rgb(val r: Int, val g: Int, val b: Int)
