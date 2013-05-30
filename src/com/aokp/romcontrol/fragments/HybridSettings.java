package com.aokp.romcontrol.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.util.ExtendedPropertiesUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aokp.romcontrol.R;
import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.util.Helpers;

public class HybridSettings extends AOKPPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final CharSequence PREF_FORCE_DUAL_PANEL = "force_dualpanel";
    private static final CharSequence PREF_USER_MODE_UI = "user_mode_ui";
    private static final CharSequence PREF_HIDE_EXTRAS = "hide_extras";
    //private static final CharSequence PREF_SHOW_OVERFLOW = "show_overflow";

    // PER APP DPI
    PreferenceScreen mDpiScreen;
    CheckBoxPreference mAutoBackup;
    Preference mBackup;
    Preference mRestore;
    Preference mAppsDpi;
    ListPreference mAppsUimode;
    ListPreference mUimode;
    CheckBoxPreference mDualpane;
    ListPreference mUserModeUI;
    CheckBoxPreference mHideExtras;
    //CheckBoxPreference mShowActionOverflow;
    Preference mLcdDensity;

    private Context mContext;
    private int mAppDpiProgress; 
    private static ContentResolver mContentResolver;

    int newDensityValue;
    DensityChanger densityFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        addPreferencesFromResource(R.xml.hybrid_settings);

        mDpiScreen = (PreferenceScreen) findPreference("system_dpi");

        mAppsDpi = findPreference("apps_dpi");

        mLcdDensity = findPreference("lcd_density_setup");
        String currentProperty = SystemProperties.get("ro.sf.lcd_density");
        try {
            newDensityValue = Integer.parseInt(currentProperty);
        } catch (Exception e) {
            getPreferenceScreen().removePreference(mLcdDensity);
        }

        mLcdDensity.setSummary(getResources().getString(R.string.current_lcd_density) + currentProperty);
        
        mAppsUimode = (ListPreference) findPreference("apps_ui_mode");
        int prop = ExtendedPropertiesUtils
                .getActualProperty("com.android.systemui.layout");
        int aprop = ExtendedPropertiesUtils
                .getActualProperty(ExtendedPropertiesUtils.BEERBONG_PREFIX + "user_default_layout");
        if (aprop == 0) {
            aprop = prop;
        }
        mAppsUimode.setValue(String.valueOf(aprop));
        mAppsUimode.setSummary(mAppsUimode.getEntry());
        mAppsUimode.setOnPreferenceChangeListener(this);

        mAutoBackup = (CheckBoxPreference) findPreference("dpi_groups_auto_backup");
        mBackup = findPreference("dpi_groups_backup");
        mRestore = findPreference("dpi_groups_restore");

        boolean isAutoBackup = mContext.getSharedPreferences(Applications.PREFS_NAME, 0)
                .getBoolean(Applications.PROPERTY_AUTO_BACKUP, false);

        mAutoBackup.setChecked(isAutoBackup);

        mRestore.setEnabled(Applications.backupExists());

        mDualpane = (CheckBoxPreference) findPreference(PREF_FORCE_DUAL_PANEL);
        mDualpane.setChecked(Settings.System.getBoolean(mContentResolver,
                        Settings.System.FORCE_DUAL_PANEL, getResources().getBoolean(
                        com.android.internal.R.bool.preferences_prefer_dual_pane)));

        mHideExtras = (CheckBoxPreference) findPreference(PREF_HIDE_EXTRAS);
        mHideExtras.setChecked(Settings.System.getBoolean(mContentResolver,
                        Settings.System.HIDE_EXTRAS_SYSTEM_BAR, false));

        mUserModeUI = (ListPreference) findPreference(PREF_USER_MODE_UI);
        int uiMode = Settings.System.getInt(mContentResolver, Settings.System.CURRENT_UI_MODE, 0);
        mUserModeUI.setValue(Integer.toString(Settings.System.getInt(mContentResolver,
                Settings.System.USER_UI_MODE, uiMode)));
        mUserModeUI.setOnPreferenceChangeListener(this);

        //mShowActionOverflow = (CheckBoxPreference) findPreference(PREF_SHOW_OVERFLOW);
        //mShowActionOverflow.setChecked(Settings.System.getBoolean(mContentResolver,
        //                Settings.System.UI_FORCE_OVERFLOW_BUTTON, false));

        updateSummaries();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mBackup) {
            Applications.backup(mContext);
        } else if (preference == mRestore) {
            Applications.restore(mContext);
            Utils.reboot(mContext);
        } else if (preference == mAutoBackup) {
            SharedPreferences settings = mContext.getSharedPreferences(
                    Applications.PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Applications.PROPERTY_AUTO_BACKUP,
                    ((CheckBoxPreference) preference).isChecked());
            editor.commit();
        } else if (preference == mDualpane) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.FORCE_DUAL_PANEL,
                    ((TwoStatePreference) preference).isChecked());
            return true;
        } else if (preference == mHideExtras) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.HIDE_EXTRAS_SYSTEM_BAR,
                    ((TwoStatePreference) preference).isChecked());
            return true;
        //} else if (preference == mShowActionOverflow) {
        //    boolean enabled = mShowActionOverflow.isChecked();
        //    Settings.System.putBoolean(mContentResolver, Settings.System.UI_FORCE_OVERFLOW_BUTTON,
        //            enabled);
            // Show toast appropriately
        //    if (enabled) {
        //        Toast.makeText(getActivity(), R.string.show_overflow_toast_enable,
        //                Toast.LENGTH_LONG).show();
        //    } else {
        //        Toast.makeText(getActivity(), R.string.show_overflow_toast_disable,
        //                Toast.LENGTH_LONG).show();
        //    }
        //    return true;
        } else if (preference == mAppsDpi) {
            showAppsDpiDialog();
        } else if (preference == mLcdDensity) {
            ((PreferenceActivity) getActivity())
                    .startPreferenceFragment(new DensityChanger(), true);
            return true;
        }
        updateSummaries();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String key = preference.getKey();
        if (preference == mUserModeUI) {
            int val = Integer.valueOf((String) newValue);
            Settings.System.putInt(mContentResolver,
                    Settings.System.USER_UI_MODE, val);

            if (val == 1){
                Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 1);
            }

            if (val == 1){
                Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_HIDDEN_NOW, false);
            }
 
            if (val == 1){
                Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_AUTO_EXPAND_HIDDEN, false);
            }
             
            mHideExtras.setEnabled(val == 1 ? true : false);
            if (val != 1){
                mHideExtras.setChecked(false);
                Settings.System.putBoolean(mContentResolver,
                    Settings.System.HIDE_EXTRAS_SYSTEM_BAR, false);
            }
            Helpers.restartSystemUI();
        } else if ("apps_ui_mode".equals(key)) {
            String layout = (String) newValue;
            Applications.addAppsLayout(mContext, layout);
        }
        updateSummaries();
        return false;
    }

    private void updateSummaries() {
        int dpi = ExtendedPropertiesUtils
                .getActualProperty("com.android.systemui.dpi");

        dpi = ExtendedPropertiesUtils
                .getActualProperty(ExtendedPropertiesUtils.BEERBONG_PREFIX + "user_default_dpi");
        if (dpi == 0) {
            dpi = ExtendedPropertiesUtils.getActualProperty("com.android.systemui.dpi");
        }
        mAppsDpi.setSummary(getResources().getString(
                R.string.apps_dpi_summary)
                + " " + dpi);

        int layout = ExtendedPropertiesUtils
                .getActualProperty("com.android.systemui.layout");
        int alayout = ExtendedPropertiesUtils
                .getActualProperty(ExtendedPropertiesUtils.BEERBONG_PREFIX + "user_default_layout");
        if (alayout == 0) {
            alayout = layout;
        }
        int index = mAppsUimode.findIndexOfValue(String.valueOf(alayout));
        mAppsUimode.setSummary(mAppsUimode.getEntries()[index]);

        mRestore.setEnabled(Applications.backupExists());
    }

    private void showAppsDpiDialog() {
        Resources res = getResources();
        String cancel = res.getString(R.string.cancel);
        String ok = res.getString(R.string.ok);
        String title = res.getString(R.string.apps_dpi_custom_title);
        int savedProgress = ExtendedPropertiesUtils
                .getActualProperty(ExtendedPropertiesUtils.BEERBONG_PREFIX + "user_default_dpi");
        if (savedProgress == 0) {
            savedProgress = ExtendedPropertiesUtils.getActualProperty("com.android.systemui.dpi");
        }
        savedProgress = (savedProgress - 120) / 5;

        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View alphaDialog = factory.inflate(R.layout.seekbar_dpi_dialog, null);
        SeekBar seekbar = (SeekBar) alphaDialog.findViewById(R.id.seek_bar);
        final TextView seektext = (TextView) alphaDialog
                .findViewById(R.id.seek_text);
        OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress,
                    boolean fromUser) {
                mAppDpiProgress = 120 + (seekbar.getProgress() * 5);
                seektext.setText(String.valueOf(mAppDpiProgress));
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {
            }
        };
        seektext.setText(String.valueOf(120 + (savedProgress * 5)));
        seekbar.setMax(72);
        seekbar.setProgress(savedProgress);
        seekbar.setOnSeekBarChangeListener(seekBarChangeListener);
        new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setView(alphaDialog)
                .setNegativeButton(cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // nothing
                            }
                        })
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Applications.addProperty(mContext,
                                ExtendedPropertiesUtils.BEERBONG_PREFIX + "user_default_dpi",
                                mAppDpiProgress, false);
                        Applications.addProperty(mContext,
                                ExtendedPropertiesUtils.BEERBONG_PREFIX + "system_default_dpi",
                                mAppDpiProgress, false);
                    }
                }).create().show();
    }
}
