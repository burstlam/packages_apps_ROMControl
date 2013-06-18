
package com.aokp.romcontrol.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.WindowManagerGlobal;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.R.xml;

public class PowerMenu extends AOKPPreferenceFragment implements
                OnPreferenceChangeListener{
    private static final String TAG = "PowerMenu";

    //private static final String PREF_POWER_SAVER = "show_power_saver";
    private static final String PREF_POWER_OFF = "show_power_off";
    private static final String PREF_SCREENSHOT = "show_screenshot";
    private static final String PREF_TORCH_TOGGLE = "show_torch_toggle";
    private static final String PREF_AIRPLANE_TOGGLE = "show_airplane_toggle";
    private static final String PREF_NAVBAR_HIDE = "show_navbar_hide";
    private static final String KEY_EXPANDED_DESKTOP = "power_menu_expanded_desktop";
    private static final String PREF_VOLUME_STATE_TOGGLE = "show_volume_state_toggle";
    private static final String PREF_REBOOT_KEYGUARD = "show_reboot_keyguard";
    private static final String KEY_PROFILES = "power_menu_profiles";

    //SwitchPreference mShowPowerSaver;
    SwitchPreference mShowPowerOff;
    SwitchPreference mShowScreenShot;
    SwitchPreference mShowTorchToggle;
    SwitchPreference mShowAirplaneToggle;
    SwitchPreference mShowNavBarHide;
    ListPreference mExpandedDesktopPref;
    SwitchPreference mShowVolumeStateToggle;
    SwitchPreference mShowRebootKeyguard;
    private ListPreference mProfilesPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_powermenu);
        /*
        mShowPowerSaver = (SwitchPreference) findPreference(PREF_POWER_SAVER);
        int powerSaverVal = 0;
        try {
            powerSaverVal = Settings.Secure.getInt(getActivity()
                    .getContentResolver(), Settings.Secure.POWER_SAVER_MODE);
        } catch (SettingNotFoundException e) {
            mShowPowerSaver.setEnabled(false);
            mShowPowerSaver
                    .setSummary("You need to enable power saver before you can see it in the power menu.");
        }
        mShowPowerSaver.setChecked(powerSaverVal != 0);
        mShowPowerSaver.setOnPreferenceChangeListener(this); */

        mShowPowerOff = (SwitchPreference) findPreference(PREF_POWER_OFF);
        mShowPowerOff.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_POWER_OFF, true));
        mShowPowerOff.setOnPreferenceChangeListener(this);

        mShowTorchToggle = (SwitchPreference) findPreference(PREF_TORCH_TOGGLE);
        mShowTorchToggle.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE, false));
        mShowTorchToggle.setOnPreferenceChangeListener(this);

        mShowScreenShot = (SwitchPreference) findPreference(PREF_SCREENSHOT);
        mShowScreenShot.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, false));
        mShowScreenShot.setOnPreferenceChangeListener(this);

        mShowAirplaneToggle = (SwitchPreference) findPreference(PREF_AIRPLANE_TOGGLE);
        mShowAirplaneToggle.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_AIRPLANE_TOGGLE, true));
        mShowAirplaneToggle.setOnPreferenceChangeListener(this);

        mShowNavBarHide = (SwitchPreference) findPreference(PREF_NAVBAR_HIDE);
        mShowNavBarHide.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE, true));
        mShowNavBarHide.setOnPreferenceChangeListener(this);

        mShowVolumeStateToggle = (SwitchPreference) findPreference(PREF_VOLUME_STATE_TOGGLE);
        mShowVolumeStateToggle.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_VOLUME_STATE_TOGGLE, true));
        mShowVolumeStateToggle.setOnPreferenceChangeListener(this);

        mExpandedDesktopPref = (ListPreference) findPreference(KEY_EXPANDED_DESKTOP);
        mExpandedDesktopPref.setOnPreferenceChangeListener(this);
        int expandedDesktopValue = Settings.System.getInt(getContentResolver(),
                        Settings.System.EXPANDED_DESKTOP_STYLE, 0);
        mExpandedDesktopPref.setValue(String.valueOf(expandedDesktopValue));
        mExpandedDesktopPref.setSummary(mExpandedDesktopPref.getEntries()[expandedDesktopValue]);

        // Hide no-op "Status bar visible" mode on devices without navbar
        // WindowManager already respects the default config value and the
        // show NavBar mod from us
        try {
            if (!WindowManagerGlobal.getWindowManagerService().hasNavigationBar()) {
                mExpandedDesktopPref.setEntries(R.array.expanded_desktop_entries_no_navbar);
                mExpandedDesktopPref.setEntryValues(R.array.expanded_desktop_values_no_navbar);
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Error getting navigation bar status");
        }

        mShowRebootKeyguard = (SwitchPreference) findPreference(PREF_REBOOT_KEYGUARD);
        mShowRebootKeyguard.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD, true));
        mShowRebootKeyguard.setOnPreferenceChangeListener(this);

        mProfilesPref = (ListPreference) findPreference(KEY_PROFILES);
        mProfilesPref.setOnPreferenceChangeListener(this);
        int mProfileShow = Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_PROFILES_ENABLED, 1);
        mProfilesPref.setValue(String.valueOf(mProfileShow));
        mProfilesPref.setSummary(mProfilesPref.getEntries()[mProfileShow]);

        // Only enable if System Profiles are also enabled
        boolean enabled = Settings.System.getInt(getContentResolver(),
                Settings.System.SYSTEM_PROFILES_ENABLED, 1) == 1;
        mProfilesPref.setEnabled(enabled);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        if (preference == mShowScreenShot) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_SCREENSHOT,
                     (Boolean) value);
            return true;
        /*
        } else if (preference == mShowPowerSaver) {
            Settings.System.putInt(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_POWER_SAVER,
                     (Boolean) value ? 1 : 0 );
            return true; */
        } else if (preference == mShowPowerOff) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_POWER_OFF,
                    (Boolean) value);
            return true;
        } else if (preference == mShowTorchToggle) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE,
                    (Boolean) value);
            return true;
        } else if (preference == mShowAirplaneToggle) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_AIRPLANE_TOGGLE,
                    (Boolean) value);
            return true;
        } else if (preference == mShowNavBarHide) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE,
                    (Boolean) value);
            return true;
        } else if (preference == mShowVolumeStateToggle) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_VOLUME_STATE_TOGGLE,
                    (Boolean) value);
            return true;
        } else if (preference == mShowRebootKeyguard) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD,
                    (Boolean) value);
            return true;
        } else if (preference == mExpandedDesktopPref) {
            int expandedDesktopValue = Integer.valueOf((String) value);
            int index = mExpandedDesktopPref.findIndexOfValue((String) value);
            if (expandedDesktopValue == 0) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 0);
            // Disable expanded desktop if enabled
                Settings.System.putInt(getContentResolver(),
                        Settings.System.EXPANDED_DESKTOP_STATE, 0);
            } else {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.POWER_MENU_EXPANDED_DESKTOP_ENABLED, 1);
            }
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_DESKTOP_STYLE, expandedDesktopValue);
            mExpandedDesktopPref.setSummary(mExpandedDesktopPref.getEntries()[index]);
            return true;
            } else if (preference == mProfilesPref) {
                int mProfileShow = Integer.valueOf((String) value);
                int index = mProfilesPref.findIndexOfValue((String) value);
                Settings.System.putInt(getContentResolver(),
                        Settings.System.POWER_MENU_PROFILES_ENABLED, mProfileShow);
                mProfilesPref.setSummary(mProfilesPref.getEntries()[index]);
                return true;
        }
        return false;
    }

}
