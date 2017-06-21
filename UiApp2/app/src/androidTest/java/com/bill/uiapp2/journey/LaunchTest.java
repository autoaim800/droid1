package com.bill.uiapp2.journey;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.bill.uiapp2.HomeActivity;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.fail;


/**
 * what's the purpose of this file
 */
public class LaunchTest {
    @Rule
    public ActivityTestRule<HomeActivity> activityWatcher = new ActivityTestRule<>(HomeActivity.class, true);

    @Test
    public void testLaunchToHome(){
        fail("not impl");
    }
}
