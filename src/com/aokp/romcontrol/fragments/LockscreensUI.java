package com.aokp.romcontrol.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.ROMControlActivity;
import com.aokp.romcontrol.util.Helpers;

public class LockscreensUI extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "LockscreensUI";
    private static final boolean DEBUG = true;

    private static final String PREF_LOCKSCREEN_SECURITY = "lockscreen_security";
    private static final String PREF_LOCK_CLOCK = "lock_clock";
    private static final String PREF_LOCKSCREEN = "lockscreen";

    Preference mLockscreenSecurity;
    Preference mLockClock;
    Preference mLockScreen;

    private boolean mIsScreenLarge;
    private Activity mActivity;
    private ContentResolver mResolver;
    private int seekbarProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mResolver = mActivity.getContentResolver();
        setTitle(R.string.title_lockscreens);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_lockscreens_ui);

        mLockscreenSecurity = findPreference(PREF_LOCKSCREEN_SECURITY);
        mLockClock = findPreference(PREF_LOCK_CLOCK);
        mLockScreen = findPreference(PREF_LOCKSCREEN);
        //setHasOptionsMenu(true);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
         return false;
    }

}
