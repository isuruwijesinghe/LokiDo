package com.lokido.isuru.lokido;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.github.omadahealth.lollipin.lib.managers.LockManager;
import com.robotium.solo.Solo;

public class AbstractTest extends ActivityInstrumentationTestCase2<Drawer> {

    protected static final String PASSWORD_PREFERENCE_KEY = "PASSCODE";
    protected static final String PASSWORD_ALGORITHM_PREFERENCE_KEY = "ALGORITHM";
    private static final String LAST_ACTIVE_MILLIS_PREFERENCE_KEY = "LAST_ACTIVE_MILLIS";
    protected static final String ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY = "ONLY_BACKGROUND_TIMEOUT_PREFERENCE_KEY";

    protected Solo solo;

    public AbstractTest() {
        super(Drawer.class);
    }

    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }


    @Override
    public void tearDown() throws Exception {
        removeAllPrefs();
        solo.finishOpenedActivities();
    }

    /**
     * Click on the specified view id
     *
     * @param id
     */
    protected void clickOnView(int id) {
        solo.sleep(300);
        solo.clickOnView(getView(id));
    }

    /**
     * Get the view for the specified id
     *
     * @param id
     * @return
     */
    protected View getView(int id) {
        return solo.getView(id);
    }

    protected void removeAllPrefs() {
        LockManager.getInstance().getAppLock().disableAndRemoveConfiguration();
    }

    protected void setMillis(long millis) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        System.out.println(getActivity());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(LAST_ACTIVE_MILLIS_PREFERENCE_KEY, millis);
        editor.apply();
    }
}
