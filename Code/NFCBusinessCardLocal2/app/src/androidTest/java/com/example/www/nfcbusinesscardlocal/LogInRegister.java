package com.example.www.nfcbusinesscardlocal;


import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LogInRegister {

    @Rule
    public ActivityTestRule<LogIn> mActivityTestRule = new ActivityTestRule<>(LogIn.class);

    @Test
    public void functionalkity() {
            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ViewInteraction appCompatButton = onView(
                    allOf(withText("Log in"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            2),
                                    0),
                            isDisplayed()));
            appCompatButton.perform(click());

            ViewInteraction appCompatEditText = onView(
                    allOf(withId(R.id.emaillogin),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    1),
                            isDisplayed()));
            appCompatEditText.perform(click());

            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(320);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ViewInteraction appCompatEditText2 = onView(
                    allOf(withId(R.id.emaillogin),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    1),
                            isDisplayed()));
            appCompatEditText2.perform(replaceText("john@gmail.com"), closeSoftKeyboard());



            ViewInteraction appCompatButton2 = onView(
                    allOf(withText("Log in"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            2),
                                    0),
                            isDisplayed()));
            appCompatButton2.perform(click());

            ViewInteraction appCompatTextView = onView(
                    allOf(withText("No account? Register now."),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            3),
                                    0),
                            isDisplayed()));
            appCompatTextView.perform(click());

            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(357);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.btn_signup), withText("Create Account"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                10),
                        isDisplayed()));
        appCompatButton8.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            ViewInteraction appCompatEditText3 = onView(
                    allOf(withId(R.id.input_name),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    0),
                            isDisplayed()));
            appCompatEditText3.perform(click());

            ViewInteraction appCompatEditText4 = onView(
                    allOf(withId(R.id.input_name),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    0),
                            isDisplayed()));
            appCompatEditText4.perform(replaceText("John "), closeSoftKeyboard());
            try {
                Thread.sleep(505);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ViewInteraction appCompatEditText5 = onView(
                    allOf(withId(R.id.input_jobtitle),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    1),
                            isDisplayed()));
            appCompatEditText5.perform(replaceText("intern"), closeSoftKeyboard());

            ViewInteraction appCompatEditText6 = onView(
                    allOf(withId(R.id.input_companyname),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    2),
                            isDisplayed()));
            appCompatEditText6.perform(replaceText("Epiuse South Africa "), closeSoftKeyboard());
        try {
            Thread.sleep(505);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            ViewInteraction appCompatEditText7 = onView(
                    allOf(withId(R.id.input_address),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    3),
                            isDisplayed()));
            appCompatEditText7.perform(replaceText("245 Lynnwood Road "), closeSoftKeyboard());

        try {
            Thread.sleep(305);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            ViewInteraction appCompatEditText8 = onView(
                    allOf(withId(R.id.input_email),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    5),
                            isDisplayed()));
            appCompatEditText8.perform(replaceText("john@gmail.com"), closeSoftKeyboard());

        try {
            Thread.sleep(505);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            ViewInteraction appCompatEditText9 = onView(
                    allOf(withId(R.id.input_telephone),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    6),
                            isDisplayed()));
            appCompatEditText9.perform(replaceText("0125563674"), closeSoftKeyboard());

        try {
            Thread.sleep(505);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            ViewInteraction appCompatEditText10 = onView(
                    allOf(withId(R.id.input_mobile),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    7),
                            isDisplayed()));
            appCompatEditText10.perform(replaceText("0715967064"), closeSoftKeyboard());

        try {
            Thread.sleep(556);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction appCompatEditText11 = onView(
                    allOf(withId(R.id.input_password),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    8),
                            isDisplayed()));
            appCompatEditText11.perform(replaceText("password"), closeSoftKeyboard());

        try {
            Thread.sleep(350);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            ViewInteraction appCompatEditText12 = onView(
                    allOf(withId(R.id.input_confirmPassword),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    9),
                            isDisplayed()));
            appCompatEditText12.perform(replaceText("password1"), closeSoftKeyboard());

        try {
            Thread.sleep(35);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            ViewInteraction appCompatButton3 = onView(
                    allOf(withId(R.id.btn_signup), withText("Create Account"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    10),
                            isDisplayed()));
            appCompatButton3.perform(click());
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText13 = onView(
                    allOf(withId(R.id.input_confirmPassword), withText("password1"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    9),
                            isDisplayed()));
            appCompatEditText13.perform(click());

            ViewInteraction appCompatEditText14 = onView(
                    allOf(withId(R.id.input_confirmPassword), withText("password1"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    9),
                            isDisplayed()));
            appCompatEditText14.perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

            ViewInteraction appCompatEditText15 = onView(
                    allOf(withId(R.id.input_confirmPassword), withText("password1"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    9),
                            isDisplayed()));
            appCompatEditText15.perform(replaceText("password"));

            ViewInteraction appCompatEditText16 = onView(
                    allOf(withId(R.id.input_confirmPassword), withText("password"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    9),
                            isDisplayed()));
            appCompatEditText16.perform(closeSoftKeyboard());


/* */
            ViewInteraction appCompatButton4 = onView(
                    allOf(withId(R.id.btn_signup), withText("Create Account"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            0),
                                    10),
                            isDisplayed()));
            appCompatButton4.perform(click());

            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(3410);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ViewInteraction appCompatEditText17 = onView(
                    allOf(withId(R.id.passwordlogin),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            1),
                                    1),
                            isDisplayed()));
            appCompatEditText17.perform(replaceText("password"), closeSoftKeyboard());



            ViewInteraction appCompatButton5 = onView(
                    allOf(withText("Log in"),
                            childAtPosition(
                                    childAtPosition(
                                            withClassName(is("android.widget.LinearLayout")),
                                            2),
                                    0),
                            isDisplayed()));
            appCompatButton5.perform(click());

            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(3507);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ViewInteraction appCompatButton6 = onView(
                    allOf(childAtPosition(
                            childAtPosition(
                                    withId(R.id.main_card),
                                    1),
                            3),
                            isDisplayed()));
            appCompatButton6.perform(click());

            // Added a sleep statement to match the app's execution delay.
            // The recommended way to handle such scenarios is to use Espresso idling resources:
            // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
            try {
                Thread.sleep(3508);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pressBack();

        ViewInteraction appCompatButton10 = onView(
                allOf(withId(R.id.btn_signout),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        2),
                                0),
                        isDisplayed()));
        appCompatButton10.perform(click());

        ViewInteraction appCompatButton11 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        appCompatButton11.perform(scrollTo(), click());


    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
