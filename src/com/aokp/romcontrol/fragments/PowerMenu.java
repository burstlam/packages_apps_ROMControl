
package com.aokp.romcontrol.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
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
/*    private static final String PREF_NAVBAR_HIDE = "show_navbar_hide";*/
    private static final String PREF_EXPANDED_DESKTOP_TOGGLE = "power_menu_expanded_desktop";
    private static final String EXPANDED_DESKTOP_STYLE = "expanded_desktop_style";
    private static final String PREF_VOLUME_STATE_TOGGLE = "show_volume_state_toggle";
    private static final String PREF_REBOOT_KEYGUARD = "show_reboot_keyguard";

    //SwitchPreference mShowPowerSaver;
    SwitchPreference mShowPowerOff;
    SwitchPreference mShowScreenShot;
    SwitchPreference mShowTorchToggle;
    SwitchPreference mShowAirplaneToggle;
/*    SwitchPreference mShowNavBarHide;*/
    SwitchPreference mExpandedDesktopPref;
    ListPreference mExpandedDesktopSbPref;
    SwitchPreference mShowVolumeStateToggle;
    SwitchPreference mShowRebootKeyguard;

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

/*        mShowNavBarHide = (SwitchPreference) findPreference(PREF_NAVBAR_HIDE);
        mShowNavBarHide.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE, true));
        mShowNavBarHide.setOnPreferenceChangeListener(this);*/

        mShowVolumeStateToggle = (SwitchPreference) findPreference(PREF_VOLUME_STATE_TOGGLE);
        mShowVolumeStateToggle.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_VOLUME_STATE_TOGGLE, true));
        mShowVolumeStateToggle.setOnPreferenceChangeListener(this);

        mExpandedDesktopPref = (SwitchPreference) findPreference(PREF_EXPANDED_DESKTOP_TOGGLE);
        mExpandedDesktopPref.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_EXPANDED_DESKTOP_TOGGLE, false));
        mExpandedDesktopPref.setOnPreferenceChangeListener(this);

        PreferenceScreen prefSet = getPreferenceScreen();
        mExpandedDesktopSbPref = (ListPreference) prefSet.findPreference(EXPANDED_DESKTOP_STYLE);
        mExpandedDesktopSbPref.setOnPreferenceChangeListener(this);
        int expandedDesktopValue = Settings.System.getInt(mContentRes,
                Settings.System.EXPANDED_DESKTOP_STYLE, 0);
        mExpandedDesktopSbPref.setValue(String.valueOf(expandedDesktopValue));

        mShowRebootKeyguard = (SwitchPreference) findPreference(PREF_REBOOT_KEYGUARD);
        mShowRebootKeyguard.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD, true));
        mShowRebootKeyguard.setOnPreferenceChangeListener(this);

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
        /*
        } else if (preference == mShowNavBarHide) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_NAVBAR_HIDE,
                    (Boolean) value);
            return true;*/
        } else if (preference == mShowVolumeStateToggle) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_VOLUME_STATE_TOGGLE,
                    (Boolean) value);
            return true;
        } else if (preference == mExpandedDesktopPref) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_EXPANDED_DESKTOP_TOGGLE,
                    (Boolean) value);
            return true;
        } else if (preference == mShowRebootKeyguard) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.POWER_DIALOG_SHOW_REBOOT_KEYGUARD,
                    (Boolean) value);
            return true;
        } else if (preference == mExpandedDesktopSbPref) {
            int expandedDesktopValue = Integer.valueOf((String) value);
            int index = mExpandedDesktopSbPref.findIndexOfValue((String) value); 
            if (expandedDesktopValue == 0) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.EXPANDED_DESKTOP_STATE, 0);
            } else {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.EXPANDED_DESKTOP_STATE, 1);
            }
            Settings.System.putInt(getContentResolver(),
                    Settings.System.EXPANDED_DESKTOP_STYLE, expandedDesktopValue);
            mExpandedDesktopPref.setSummary(mExpandedDesktopSbPref.getEntries()[index]);
            return true; 
        }
        return false;
    }

}
