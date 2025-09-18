package org.example.app

import org.example.app.model.HslColor
import org.example.app.model.PaletteGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class PaletteGeneratorTest {

    @Test
    fun generatesFiveDistinctColorsByDefault() {
        val gen = PaletteGenerator(Random(123456))
        val palette = gen.generateDistinctPalette()
        assertEquals(5, palette.size)
        assertAllDistinct(palette)
        assertAllInRanges(palette)
    }

    @Test
    fun supportsVariableSize() {
        val gen = PaletteGenerator(Random(98765))
        val palette = gen.generateDistinctPalette(size = 7)
        assertEquals(7, palette.size)
        assertAllDistinct(palette)
        assertAllInRanges(palette)
    }

    private fun assertAllDistinct(palette: List<HslColor>) {
        // Validate uniqueness by HSL triple rounding to avoid floating tiny diffs.
        val set = palette.map { Triple(it.h.toInt(), it.s.toInt(), it.l.toInt()) }.toSet()
        assertTrue("Palette contains near-duplicate HSL values", set.size == palette.size)
    }

    private fun assertAllInRanges(palette: List<HslColor>) {
        palette.forEach {
            assertTrue(it.h in 0f..360f)
            assertTrue(it.s in 0f..100f)
            assertTrue(it.l in 0f..100f)
        }
    }
}
