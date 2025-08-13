package com.ahmadrd.storyapp.ui.auth.login


import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.ahmadrd.storyapp.R
import com.ahmadrd.storyapp.ui.detail.DetailActivity
import com.ahmadrd.storyapp.utils.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_and_logout_flow() {
        // Input Email and Password
        onView(withId(R.id.emailEditText)).perform(
            typeText("ahmaddahuri212@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordEditText)).perform(
            replaceText("ahmad123"),
            closeSoftKeyboard()
        )

        // Check Matches Input For Email and Password
        onView(withId(R.id.emailEditText))
            .check(matches(withText("ahmaddahuri212@gmail.com")))
        onView(withId(R.id.passwordEditText))
            .check(matches(withText("ahmad123")))

        // Click Login
        onView(withId(R.id.loginButton))
            .perform(click())

        // Click Next in AlertDialog
        onView(withText(R.string.next))
            .check(matches(isDisplayed()))
            .perform(click())

        // Check if RecyclerView is displayed
        onView(withId(R.id.rvStory))
            .check(matches(isDisplayed()))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(10))

        // Click Settings Menu
        onView(withId(R.id.navigation_settings))
            .perform(click())

        // Check Settings Activity is displayed and Click Logout
        onView(withId(R.id.button_logout))
            .check(matches(isDisplayed()))
            .perform(click())

        // Check if Login Activity is displayed
        onView(withId(R.id.loginButton))
            .check(matches(isDisplayed()))
    }

    @Test
    fun load_detail_story_Success() {
        // Input Email and Password
        onView(withId(R.id.emailEditText)).perform(
            typeText("ahmaddahuri212@gmail.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordEditText)).perform(
            replaceText("ahmad123"),
            closeSoftKeyboard()
        )

        // Check Matches Input For Email and Password
        onView(withId(R.id.emailEditText))
            .check(matches(withText("ahmaddahuri212@gmail.com")))
        onView(withId(R.id.passwordEditText))
            .check(matches(withText("ahmad123")))

        // Click Login
        onView(withId(R.id.loginButton))
            .perform(click())

        // Click Next in AlertDialog
        onView(withText(R.string.next))
            .check(matches(isDisplayed()))
            .perform(click())

        // Check if RecyclerView is displayed and Click First Item
        Intents.init()
        onView(withId(R.id.rvStory)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )
        intended(hasComponent(DetailActivity::class.java.name))

        // Check if Detail Activity is displayed
        onView(withId(R.id.detail)).check(matches(isDisplayed()))
    }

}
