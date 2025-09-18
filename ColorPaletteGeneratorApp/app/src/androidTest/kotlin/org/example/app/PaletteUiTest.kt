package org.example.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.contrib.RecyclerViewActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PaletteUiTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun initialPalette_hasFiveItems_andGenerateFabVisible() {
        onView(withId(R.id.generateFab))
            .check(matches(isDisplayed()))
            .check(matches(withContentDescription(R.string.cd_generate_palette)))

        onView(withId(R.id.paletteRecycler))
            .check(matches(isDisplayed()))
    }
}
