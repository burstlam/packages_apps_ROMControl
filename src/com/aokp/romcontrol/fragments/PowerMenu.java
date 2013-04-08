
package com.aokp.romcontrol.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.R.xml;

public class PowerMenu extends AOKPPreferenceFragment implements
    OnPreferenceChangeListener{

    //private static final String PREF_POWER_SAVER = "show_power_saver";
    private static final String PREF_POWER_OFF = "show_power_off";
    private static final String PREF_SCREENSHOT = "show_screenshot";
    private static final String PREF_TORCH_TOGGLE = "show_torch_toggle";
    private static final String PREF_AIRPLANE_TOGGLE = "show_airplane_toggle";
    private static final String PREF_NAVBAR_HIDE = "show_navbar_hide";
    private static final String PREF_EXPANDED_DESKTOP_TOGGLE = "power_menu_expanded_desktop";
    private static final String EXPANDED_DESKTOP_STYLE = "expanded_desktop_style";
    private static final String PIE_RESTART = "pie_restart_launcher";
    private static final String PREF_VOLUME_STATE_TOGGLE = "show_volume_state_toggle";
    private static final String PREF_REBOOT_KEYGUARD = "show_reboot_keyguard";

    //CheckBoxPreference mShowPowerSaver;
    CheckBoxPreference mShowPowerOff;
    CheckBoxPreference mShowScreenShot;
    CheckBoxPreference mShowTorchToggle;
    CheckBoxPreference mShowAirplaneToggle;
    CheckBoxPreference mShowNavBarHide;
    CheckBoxPreference mExpandedDesktopPref;
    ListPreference mExpandedDesktopSbPref;
    private CheckBoxPreference mPieRestart;
    CheckBoxPreference mShowVolumeStateToggle;
    CheckBoxPreference mShowRebootKeyguard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_powermenu);
        /*
        mShowPowerSaver = (CheckBoxPreference) findPreference(PREF_POWER_SAVER);
        int powerSaverVal = 0;
        try {
            powerSaverVal = Settings.Secure.getInt(getActivity()
                    .getContentResolver(), Settings.Secure.POWER_SAVER_MODE);
        } catch (SettingNotFoundException e) {
            mShowPowerSaver.setEnabled(false);
            mShowPowerSaver
                    .setSummary("You need to enable power saver before you can see it in the power menu.");
        }
        mShowPowerSaver.setChecked(powerSaverVal != 0); */

        mShowPowerOff = (CheckBoxPreference) findPreference(PREF_POWER_OFF);
        mShowPowerOff.setChecked(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_POWER_OFF, 1) == 1);

        mShowTorchToggle = (CheckBoxPreference) findPreference(PREF_TORCH_TOGGLE);
        mShowTorchToggle.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE, false));

        mShowScreenShot = (CheckBoxPreference) findPreference(PREF_SCREENSHOT);
        mShowScreenShot.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_SCREENSHOT, false));

        mShowAirplaneToggle = (CheckBoxPreference) findPreference(PREF_AIRPLANE_TOGGLE);
        mShowAirplaneToggle.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_AIRPLANE_TOGGLE, true));

        mShowNavBarHide = (CheckBoxPreference) findPreference(PREF_NAVBAR_HIDE);
        mShowNavBarHide.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE, true));

        mShowVolumeStateToggle = (CheckBoxPreference) findPreference(PREF_VOLUME_STATE_TOGGLE);
        mShowVolumeStateToggle.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_VOLUME_STATE_TOGGLE, true));

        mExpandedDesktopPref = (CheckBoxPreference) findPreference(PREF_EXPANDED_DESKTOP_TOGGLE);
        mExpandedDesktopPref.setChecked(Settings.System.getInt(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_EXPANDED_DESKTOP_TOGGLE, 
                0) == 1);

        PreferenceScreen prefSet = getPreferenceScreen();
        mExpandedDesktopSbPref = (ListPreference) prefSet.findPreference(EXPANDED_DESKTOP_STYLE);
        mExpandedDesktopSbPref.setOnPreferenceChangeListener(this);
        int expandedDesktopValue = Settings.System.getInt(getContentResolver(), Settings.System.EXPANDED_DESKTOP_STYLE, 0);
        mExpandedDesktopSbPref.setValue(String.valueOf(expandedDesktopValue));
        updateExpandedDesktopSummary(expandedDesktopValue);

        mPieRestart = (CheckBoxPreference) prefSet.findPreference(PIE_RESTART);
        mPieRestart.setChecked(Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.EXPANDED_DESKTOP_RESTART_LAUNCHER, 0) == 1);

        mShowRebootKeyguard = (CheckBoxPreference) findPreference(PREF_REBOOT_KEYGUARD);
        mShowRebootKeyguard.setChecked(Settings.System.getBoolean(getActivity()
                .getContentResolver(), Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD, true));

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mExpandedDesktopSbPref) {
            int expandedDesktopValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_DESKTOP_STYLE, expandedDesktopValue);
            updateExpandedDesktopSummary(expandedDesktopValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mShowScreenShot) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_SCREENSHOT,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        /*
        } else if (preference == mShowPowerSaver) {
            Settings.System.putInt(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_POWER_SAVER,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true; */
        } else if (preference == mShowPowerOff) {
            Settings.System.putInt(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_POWER_OFF,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mShowTorchToggle) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_TORCH_TOGGLE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mShowAirplaneToggle) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_AIRPLANE_TOGGLE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mShowNavBarHide) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mShowVolumeStateToggle) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_VOLUME_STATE_TOGGLE,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        } else if (preference == mExpandedDesktopPref) {
            Settings.System.putInt(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_EXPANDED_DESKTOP_TOGGLE,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mPieRestart) {
            Settings.System.putInt(mContentRes,
                    Settings.System.EXPANDED_DESKTOP_RESTART_LAUNCHER, 
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mShowRebootKeyguard) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateExpandedDesktopSummary(int value) {
        Resources res = getResources();

        if (value == 1) {
            String statusBarPresent = res.getString(R.string.expanded_desktop_summary_status_bar);
            mExpandedDesktopPref.setSummary(res.getString(R.string.summary_expanded_desktop, statusBarPresent));
        } else if (value == 2) {
            String statusBarPresent = res.getString(R.string.expanded_desktop_summary_no_status_bar);
            mExpandedDesktopPref.setSummary(res.getString(R.string.summary_expanded_desktop, statusBarPresent));
        }
    }
}
