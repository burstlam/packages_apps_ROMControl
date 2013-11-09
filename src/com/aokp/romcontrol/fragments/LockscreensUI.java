package com.aokp.romcontrol.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
import android.util.Log;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.ROMControlActivity;
import com.aokp.romcontrol.util.Helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LockscreensUI extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "LockscreensUI";
    private static final boolean DEBUG = true;

    private static final String PREF_LOCKSCREEN_AUTO_ROTATE = "lockscreen_auto_rotate";
    private static final String PREF_LOCKSCREEN_ALL_WIDGETS = "lockscreen_all_widgets";
    private static final String PREF_LOCKSCREEN_BATTERY = "lockscreen_battery";
    private static final String PREF_LOCKSCREEN_CAMERA_WIDGET = "lockscreen_camera_widget";
    private static final String PREF_LOCKSCREEN_GLOW_TORCH = "lockscreen_glow_torch";
    private static final String PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS = "lockscreen_hide_initial_page_hints";
    private static final String PREF_LOCKSCREEN_MINIMIZE_CHALLENGE = "lockscreen_minimize_challenge";
    private static final String PREF_LOCKSCREEN_MUSIC_CONTROLS = "lockscreen_music_controls";
    private static final String PREF_LOCKSCREEN_USE_CAROUSEL = "lockscreen_use_widget_container_carousel";
    private static final String PREF_LOCKSCREEN_UNLIMITED_WIDGETS = "lockscreen_unlimited_widgets";
    private static final String PREF_VOLUME_ROCKER_WAKE = "volume_rocker_wake";

    private static final String PREF_LOCKSCREEN_SECURITY = "lockscreen_security";
    private static final String PREF_LOCK_CLOCK = "lock_clock";
    private static final String PREF_LOCKSCREEN = "lockscreen";

    CheckBoxPreference mCameraWidget;
    CheckBoxPreference mLockscreenAllWidgets;
    CheckBoxPreference mLockscreenAutoRotate;
    CheckBoxPreference mLockscreenBattery;
    CheckBoxPreference mLockscreenCarousel;
    CheckBoxPreference mLockscreenHideInitialPageHints;
    CheckBoxPreference mLockscreenUnlimitedWidgets;
    CheckBoxPreference mLockMinimizeChallange;
    CheckBoxPreference mLockVolControl;
    CheckBoxPreference mLockVolumeWake;
    ListPreference mLockScreenGlowTorch;

    Preference mLockscreenSecurity;
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
        PreferenceScreen prefs = getPreferenceScreen();
        setTitle(R.string.title_lockscreens);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_lockscreens_ui);

        mLockscreenSecurity = findPreference(PREF_LOCKSCREEN_SECURITY);
        mLockScreen = findPreference(PREF_LOCKSCREEN);

        // Dont display the lock clock preference if its not installed
        removePreferenceIfPackageNotInstalled(findPreference(PREF_LOCK_CLOCK));

        mCameraWidget = (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_CAMERA_WIDGET);
        mCameraWidget.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_CAMERA_WIDGET_SHOW, false));

        mLockscreenAutoRotate = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_AUTO_ROTATE);
        mLockscreenAutoRotate.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_AUTO_ROTATE, false));

        mLockscreenAllWidgets = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_ALL_WIDGETS);
        mLockscreenAllWidgets.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_ALL_WIDGETS, false));

        mLockscreenBattery = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_BATTERY);
        mLockscreenBattery.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_BATTERY, false));

        mLockscreenCarousel = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_USE_CAROUSEL);
        mLockscreenCarousel.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL, false));

        mLockscreenHideInitialPageHints = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS);
        mLockscreenHideInitialPageHints.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS, false));

        mLockMinimizeChallange = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_MINIMIZE_CHALLENGE);
        mLockMinimizeChallange.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_MINIMIZE_LOCKSCREEN_CHALLENGE, false));

        mLockscreenUnlimitedWidgets = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_UNLIMITED_WIDGETS);
        mLockscreenUnlimitedWidgets.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_UNLIMITED_WIDGETS, false));

        mLockVolControl = (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_MUSIC_CONTROLS);
        mLockVolControl.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.VOLUME_MUSIC_CONTROLS, false));

        mLockVolumeWake = (CheckBoxPreference) findPreference(PREF_VOLUME_ROCKER_WAKE);
        mLockVolumeWake.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.VOLUME_WAKE_SCREEN, false));

        mLockScreenGlowTorch = (ListPreference) findPreference(PREF_LOCKSCREEN_GLOW_TORCH);
        int glowValue = Settings.System.getInt(mContentRes,
                    Settings.System.LOCKSCREEN_GLOW_TORCH, 0);
        mLockScreenGlowTorch.setValue(String.valueOf(glowValue));
        mLockScreenGlowTorch.setOnPreferenceChangeListener(this);

        if (isSW600DPScreen(mContext)) {
            Settings.System.putBoolean(mContentRes,
                        Settings.System.LOCKSCREEN_CAMERA_WIDGET_SHOW, false);
            Settings.System.putBoolean(mContentRes,
                        Settings.System.LOCKSCREEN_MINIMIZE_LOCKSCREEN_CHALLENGE, false);
            prefs.removePreference(mCameraWidget);
            prefs.removePreference(mLockMinimizeChallange);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mCameraWidget) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_CAMERA_WIDGET_SHOW,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenAllWidgets) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_ALL_WIDGETS,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenAutoRotate) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_AUTO_ROTATE,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenBattery) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_BATTERY,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenCarousel) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenHideInitialPageHints) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenUnlimitedWidgets) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_UNLIMITED_WIDGETS,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockMinimizeChallange) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_UNLIMITED_WIDGETS,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockVolControl) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.VOLUME_MUSIC_CONTROLS,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockVolumeWake) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.VOLUME_WAKE_SCREEN,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLockScreenGlowTorch) {
            int glowValue= Integer.valueOf((String) newValue);
            Settings.System.putInt(mContentRes,
                    Settings.System.LOCKSCREEN_GLOW_TORCH, glowValue);
            return true;
        }
        return false;
    }

    private boolean removePreferenceIfPackageNotInstalled(Preference preference) {
        String intentUri = ((PreferenceScreen) preference).getIntent().toUri(1);
        Pattern pattern = Pattern.compile("component=([^/]+)/");
        Matcher matcher = pattern.matcher(intentUri);

        String packageName = matcher.find() ? matcher.group(1) : null;
        if (packageName != null) {
            try {
                getPackageManager().getPackageInfo(packageName, 0);
            } catch (NameNotFoundException e) {
                Log.e(TAG, "package " + packageName + " not installed, hiding preference.");
                getPreferenceScreen().removePreference(preference);
                return true;
            }
        }
        return false;
    }

}
