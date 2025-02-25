package org.d3ifcool.telyuconsign.ui

import org.junit.Assert.*
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.d3ifcool.telyuconsign.R

@RunWith(AndroidJUnit4::class)
class LoginActivityTest{
    @Test
    fun goToRegister() {
        val activityScenario = ActivityScenario.launch(
            LoginActivity::class.java
        )
        onView(withId(org.d3ifcool.telyuconsign.R.id.signUpTextView)).perform(click())
    }
}