package com.aokp.romcontrol.fragments;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.util.Helpers;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarSignal extends AOKPPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String STATUS_BAR_SHOW_TRAFFIC = "status_bar_show_traffic";
    private static final String STATUS_BAR_TRAFFIC_COLOR = "status_bar_traffic_color";
    private static final String STATUS_BAR_TRAFFIC_AUTOHIDE = "status_bar_traffic_autohide";

    ListPreference mDbmStyletyle;
    ListPreference mWifiStyle;
    ColorPickerPreference mColorPicker;
    ColorPickerPreference mWifiColorPicker;
    CheckBoxPreference mHideSignal;
    CheckBoxPreference mAltSignal;
    CheckBoxPreference mShow4gForLte;
    CheckBoxPreference mStatusBarShowTraffic;
    CheckBoxPreference mStatusBarTraffic_autohide;
    ColorPickerPreference mTrafficColorPicker; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_statusbar_signal);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_statusbar_signal);

        PreferenceScreen prefs = getPreferenceScreen();
        int defaultColor;
        int intColor;
        String hexColor;

        mDbmStyletyle = (ListPreference) findPreference("signal_style");
        mDbmStyletyle.setOnPreferenceChangeListener(this);
        mDbmStyletyle.setValue(Integer.toString(Settings.System.getInt(mContentRes,
                Settings.System.STATUSBAR_SIGNAL_TEXT, 0)));

        mColorPicker = (ColorPickerPreference) findPreference("signal_color");
        mColorPicker.setOnPreferenceChangeListener(this);

        mWifiStyle = (ListPreference) findPreference("wifi_signal_style");
        mWifiStyle.setOnPreferenceChangeListener(this);
        mWifiStyle.setValue(Integer.toString(Settings.System.getInt(mContentRes,
                Settings.System.STATUSBAR_WIFI_SIGNAL_TEXT, 0)));

        mWifiColorPicker = (ColorPickerPreference) findPreference("wifi_signal_color");
        mWifiColorPicker.setOnPreferenceChangeListener(this);

        mHideSignal = (CheckBoxPreference) findPreference("hide_signal");
        mHideSignal.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.STATUSBAR_HIDE_SIGNAL_BARS, false));

        mAltSignal = (CheckBoxPreference) findPreference("alt_signal");
        mAltSignal.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.STATUSBAR_SIGNAL_CLUSTER_ALT, false));

        boolean check4gByDefault = mContext.getResources().getBoolean(
                com.android.internal.R.bool.config_show4GForLTE);
        mShow4gForLte = (CheckBoxPreference)findPreference("show_4g_for_lte");
        mShow4gForLte.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.STATUSBAR_SIGNAL_SHOW_4G_FOR_LTE, check4gByDefault));

        mStatusBarShowTraffic = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_TRAFFIC);
        mStatusBarShowTraffic.setChecked((Settings.System.getInt(mContentRes,
                            Settings.System.STATUS_BAR_SHOW_TRAFFIC, 0) == 1));

        mStatusBarTraffic_autohide = (CheckBoxPreference) findPreference(STATUS_BAR_TRAFFIC_AUTOHIDE);
        mStatusBarTraffic_autohide.setChecked((Settings.System.getInt(mContentRes,
                Settings.System.STATUS_BAR_TRAFFIC_AUTOHIDE, 0) == 1));

        mTrafficColorPicker = (ColorPickerPreference) findPreference(STATUS_BAR_TRAFFIC_COLOR);
        mTrafficColorPicker.setOnPreferenceChangeListener(this);
        defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_blue_light);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_TRAFFIC_COLOR, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTrafficColorPicker.setSummary(hexColor);
        mTrafficColorPicker.setNewPreviewColor(intColor);

        if (Integer.parseInt(mDbmStyletyle.getValue()) == 0) {
            mColorPicker.setEnabled(false);
            mColorPicker.setSummary(R.string.enable_signal_text);
        }

        if (Integer.parseInt(mWifiStyle.getValue()) == 0) {
            mWifiColorPicker.setEnabled(false);
            mWifiColorPicker.setSummary(R.string.enable_wifi_text);
        }

        if (!hasPhoneAbility(mContext)) {
            prefs.removePreference(mDbmStyletyle);
            prefs.removePreference(mColorPicker);
            prefs.removePreference(mHideSignal);
            prefs.removePreference(mAltSignal);
            prefs.removePreference(mShow4gForLte);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        boolean value;
        if (preference == mHideSignal) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.STATUSBAR_HIDE_SIGNAL_BARS, mHideSignal.isChecked());

            return true;
        } else if (preference == mAltSignal) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.STATUSBAR_SIGNAL_CLUSTER_ALT, mAltSignal.isChecked());
            return true;
        } else if (preference == mShow4gForLte) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.STATUSBAR_SIGNAL_SHOW_4G_FOR_LTE, mShow4gForLte.isChecked());
            return true;
        } else if (preference == mStatusBarShowTraffic) {
            value = mStatusBarShowTraffic.isChecked();
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUS_BAR_SHOW_TRAFFIC, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarTraffic_autohide) {
            value = mStatusBarTraffic_autohide.isChecked();
            Settings.System.putInt(mContentRes,
                Settings.System.STATUS_BAR_TRAFFIC_AUTOHIDE, value ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mDbmStyletyle) {

            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_SIGNAL_TEXT, val);
            mColorPicker.setEnabled(val == 0 ? false : true);
            if (val == 0) {
                mColorPicker.setSummary(R.string.enable_signal_text);
            } else {
                mColorPicker.setSummary(null);
            }
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_SIGNAL_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mWifiStyle) {

            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_WIFI_SIGNAL_TEXT, val);
            mWifiColorPicker.setEnabled(val == 0 ? false : true);
            if (val == 0) {
                mWifiColorPicker.setSummary(R.string.enable_wifi_text);
            } else {
                mWifiColorPicker.setSummary(null);
            }
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mWifiColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_WIFI_SIGNAL_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mTrafficColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUS_BAR_TRAFFIC_COLOR, intHex);
            return true;
        }
        return false;
    }
}
