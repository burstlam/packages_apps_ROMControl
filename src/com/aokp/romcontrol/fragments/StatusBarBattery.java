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

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarBattery extends AOKPPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_BATT_ICON = "battery_icon_list";
    private static final String PREF_BATT_BAR = "battery_bar_list";
    private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
    private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
    private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";
    private static final String PREF_BATT_ANIMATE = "battery_bar_animate";

    ListPreference mBatteryIcon;
    ListPreference mBatteryBar;
    ListPreference mBatteryBarStyle;
    ListPreference mBatteryBarThickness;
    CheckBoxPreference mBatteryBarChargingAnimation;
    ColorPickerPreference mBatteryBarColor;
	ColorPickerPreference mBatteryTextColor;
    ColorPickerPreference mBatteryChargeTextColor;
	ColorPickerPreference mCmCirleRingColor;
    ColorPickerPreference mCmCirleRingColorCharge;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_statusbar_battery);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_statusbar_battery);

        int defaultColor;
        int intColor;
        String hexColor;

        mBatteryIcon = (ListPreference) findPreference(PREF_BATT_ICON);
        mBatteryIcon.setOnPreferenceChangeListener(this);
        mBatteryIcon.setValue((Settings.System.getInt(mContentRes,
                Settings.System.STATUSBAR_BATTERY_ICON, 0)) + "");

        mBatteryBar = (ListPreference) findPreference(PREF_BATT_BAR);
        mBatteryBar.setOnPreferenceChangeListener(this);
        mBatteryBar.setValue((Settings.System.getInt(mContentRes,
                        Settings.System.STATUSBAR_BATTERY_BAR, 0)) + "");

        mBatteryBarStyle = (ListPreference) findPreference(PREF_BATT_BAR_STYLE);
        mBatteryBarStyle.setOnPreferenceChangeListener(this);
        mBatteryBarStyle.setValue((Settings.System.getInt(mContentRes,
                Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0)) + "");

        mBatteryBarColor = (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
        mBatteryBarColor.setOnPreferenceChangeListener(this);
        defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_blue_light);
        intColor = Settings.System.getInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryBarColor.setSummary(hexColor);
        mBatteryBarColor.setNewPreviewColor(intColor);

        mBatteryBarChargingAnimation = (CheckBoxPreference) findPreference(PREF_BATT_ANIMATE);
        mBatteryBarChargingAnimation.setChecked(Settings.System.getInt(mContentRes,
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1);

        mBatteryBarThickness = (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
        mBatteryBarThickness.setOnPreferenceChangeListener(this);
        mBatteryBarThickness.setValue((Settings.System.getInt(mContentRes,
                Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1)) + "");

        mBatteryTextColor = (ColorPickerPreference)
				findPreference("battery_text_only_color");
        mBatteryTextColor.setOnPreferenceChangeListener(this);
        defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_blue_light);
        intColor = Settings.System.getInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_TEXT_COLOR, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryTextColor.setSummary(hexColor);
        mBatteryTextColor.setNewPreviewColor(intColor);

		mBatteryChargeTextColor = (ColorPickerPreference)
				findPreference("battery_charge_text_only_color");
        mBatteryChargeTextColor.setOnPreferenceChangeListener(this);
        defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_green_light);
        intColor = Settings.System.getInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_CHARGE_TEXT_COLOR, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryChargeTextColor.setSummary(hexColor);
        mBatteryChargeTextColor.setNewPreviewColor(intColor);

		mCmCirleRingColor = (ColorPickerPreference)
                findPreference("battery_cmcircle_ring_color");
        mCmCirleRingColor.setOnPreferenceChangeListener(this);
        defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_blue_light);
        intColor = Settings.System.getInt(mContentRes,
                    Settings.System.STATUSBAR_CMCIRLE_RING_COLOR, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCmCirleRingColor.setSummary(hexColor);
        mCmCirleRingColor.setNewPreviewColor(intColor);

        mCmCirleRingColorCharge = (ColorPickerPreference)
                findPreference("battery_cmcircle_ring_color_charge");
        mCmCirleRingColorCharge.setOnPreferenceChangeListener(this);
        defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_green_light);
        intColor = Settings.System.getInt(mContentRes,
                    Settings.System.STATUSBAR_CMCIRLE_RING_COLOR_CHARGE, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCmCirleRingColorCharge.setSummary(hexColor);
        mCmCirleRingColorCharge.setNewPreviewColor(intColor);

        if (Integer.parseInt(mBatteryBar.getValue()) == 0) {
            mBatteryBarStyle.setEnabled(false);
            mBatteryBarColor.setEnabled(false);
            mBatteryBarChargingAnimation.setEnabled(false);
            mBatteryBarThickness.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mBatteryBarChargingAnimation) {

            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBatteryIcon) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_ICON, val);
        } else if (preference == mBatteryBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, intHex);
            return true;

        } else if (preference == mBatteryBar) {

            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_BAR, val);
            if (val == 0) {
                mBatteryBarStyle.setEnabled(false);
                mBatteryBarColor.setEnabled(false);
                mBatteryBarChargingAnimation.setEnabled(false);
                mBatteryBarThickness.setEnabled(false);
            } else {
                mBatteryBarStyle.setEnabled(true);
                mBatteryBarColor.setEnabled(true);
                mBatteryBarChargingAnimation.setEnabled(true);
                mBatteryBarThickness.setEnabled(true);
            }
            return true;
        } else if (preference == mBatteryBarStyle) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, val);

        } else if (preference == mBatteryBarThickness) {

            int val = Integer.parseInt((String) newValue);
            return Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, val);

        } else if (preference == mBatteryTextColor) {

            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);

            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_TEXT_COLOR, intHex);
            return true;

        } else if (preference == mBatteryChargeTextColor) {

            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_BATTERY_CHARGE_TEXT_COLOR, intHex);
            return true;

        } else if (preference == mCmCirleRingColor) {

            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_CMCIRLE_RING_COLOR, intHex);
            return true;

        } else if (preference == mCmCirleRingColorCharge) {

            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String
                    .valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.STATUSBAR_CMCIRLE_RING_COLOR_CHARGE, intHex);
            return true;

        }
        return false;
    }

}
