package com.aokp.romcontrol.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Rect;
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
import android.provider.CalendarContract.Calendars;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.ROMControlActivity;
import com.aokp.romcontrol.util.Helpers;
import net.margaritov.preference.colorpicker.ColorPickerPreference;
import net.margaritov.preference.colorpicker.ColorPickerView;
import com.android.internal.widget.multiwaveview.GlowPadView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LockscreensUI extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "Lockscreens";
    public static final int REQUEST_PICK_WALLPAPER = 199;
    public static final int SELECT_WALLPAPER = 3;

    private static final int LOCKSCREEN_BACKGROUND_COLOR_FILL = 0;
    private static final int LOCKSCREEN_BACKGROUND_CUSTOM_IMAGE = 1;
    private static final int LOCKSCREEN_BACKGROUND_DEFAULT_WALLPAPER = 2;

    private static final boolean DEBUG = true;

    private static final String PREF_VOLUME_ROCKER_WAKE = "volume_rocker_wake";
    private static final String PREF_LOCKSCREEN_AUTO_ROTATE = "lockscreen_auto_rotate";
    private static final String PREF_LOCKSCREEN_ALL_WIDGETS = "lockscreen_all_widgets";
    private static final String PREF_LOCKSCREEN_UNLIMITED_WIDGETS = "lockscreen_unlimited_widgets";
    private static final String KEY_LOCKSCREEN_CAMERA_WIDGET = "lockscreen_camera_widget";
    private static final String PREF_LOCKSCREEN_BATTERY = "lockscreen_battery";
    private static final String PREF_LOCKSCREEN_TEXT_COLOR = "lockscreen_text_color";
    private static final String KEY_LOCK_CLOCK = "lock_clock";
    private static final String PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS = "lockscreen_hide_initial_page_hints";
    private static final String PREF_LOCKSCREEN_USE_CAROUSEL = "lockscreen_use_widget_container_carousel";
    private static final String KEY_LOCKSCREEN_MAXIMIZE_WIDGETS = "lockscreen_maximize_widgets";
    private static final String KEY_LOCKSCREEN_BACKGROUND_ALPHA = "lockscreen_background_alpha";
    public static final String KEY_BACKGROUND_PREF = "lockscreen_background";
    private static final String LOCKSCREEN_TRANSPARENT_PREF = "pref_lockscreen_transparent";
    public static final String LOCKSCREEN_GLOW_TORCH = "lockscreen_glow_torch";

    //lockscreen Style
    private static final String LOCKSCREEN_STYLE_PREF = "pref_lockscreen_picker";
    private static final String LOCKSCREEN_CUSTOM_APP_ONE = "pref_lockscreen_custom_app_one";
    private static final String LOCKSCREEN_CUSTOM_APP_TWO = "pref_lockscreen_custom_app_two";
    private static final String LOCKSCREEN_CUSTOM_APP_THREE = "pref_lockscreen_custom_app_three";

    private static final String CATEGORY_ICS = "pref_category_ics_style";
    private static final String CATEGORY_CUSTOM_APP_ONE = "pref_lockscreen_category_custom_app_one";
    private static final String CATEGORY_CUSTOM_APP_TWO = "pref_lockscreen_category_custom_app_two";
    private static final String CATEGORY_CUSTOM_APP_THREE = "pref_lockscreen_category_custom_app_three";

    private static final int LOCK_STYLE_JB = 0;
    private static final int LOCK_STYLE_ICS = 1;
    private static final int LOCK_STYLE_GB = 2;
    private static final int LOCK_STYLE_ECLAIR = 3;

    private static final int LOCK_STYLE_DEFAULT = LOCK_STYLE_JB;

    private ListPreference mLockscreenStyle;
    private PreferenceCategory mCatIcs;
    private PreferenceScreen mPrefSet;

    private PreferenceCategory mCatAppOne;
    private PreferenceCategory mCatAppTwo;
    private PreferenceCategory mCatAppThree;
    private ListPreference[] mCustApp;
    private int mWhichApp = -1;

    private int mMaxCustomApps;

    private int mCurrLockscreen;

    private File mWallpaperImage;
    private File mWallpaperTemporary;
    private ListPreference mCustomBackground;
    private Preference mWallpaperAlpha;
    CheckBoxPreference mLockTransparent;

    Preference mLockscreens;

    CheckBoxPreference mVolumeRockerWake;
    CheckBoxPreference mLockscreenBattery;
    CheckBoxPreference mLockscreenAllWidgets;
    CheckBoxPreference mLockscreenUnlimitedWidgets;
    ColorPickerPreference mLockscreenTextColor;
    CheckBoxPreference mLockscreenAutoRotate;

    CheckBoxPreference mLockscreenHideInitialPageHints;
    CheckBoxPreference mMaximizeWidgets;
    CheckBoxPreference mLockscreenUseCarousel;
    CheckBoxPreference mCameraWidget;
    ListPreference mLockScreenGlowTorch;

    private boolean mIsScreenLarge;
    private Activity mActivity;
    private ContentResolver mResolver;
    private int seekbarProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mResolver = mActivity.getContentResolver();
        mIsScreenLarge = Helpers.isTablet(getActivity());
        setTitle(R.string.title_lockscreens);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_lockscreens_ui);
        mPrefSet = getPreferenceScreen();

        mCatIcs = (PreferenceCategory) mPrefSet.findPreference(CATEGORY_ICS);

        mCurrLockscreen = Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_STYLE , LOCK_STYLE_DEFAULT);

        mCatAppOne = (PreferenceCategory) mPrefSet.findPreference(
                CATEGORY_CUSTOM_APP_ONE);
        mCatAppTwo = (PreferenceCategory) mPrefSet.findPreference(
                CATEGORY_CUSTOM_APP_TWO);
        mCatAppThree = (PreferenceCategory) mPrefSet.findPreference(
                CATEGORY_CUSTOM_APP_THREE);

        mCustApp = new ListPreference[3];
        mCustApp[0] = (ListPreference) mPrefSet.findPreference(
                LOCKSCREEN_CUSTOM_APP_ONE);
        mCustApp[0].setOnPreferenceChangeListener(this);
        mCustApp[0].setLayoutResource(R.layout.app_preference);

        mCustApp[1] = (ListPreference) mPrefSet.findPreference(
                LOCKSCREEN_CUSTOM_APP_TWO);
        mCustApp[1].setOnPreferenceChangeListener(this);
        mCustApp[1].setLayoutResource(R.layout.app_preference);

        mCustApp[2] = (ListPreference) mPrefSet.findPreference(
                LOCKSCREEN_CUSTOM_APP_THREE);
        mCustApp[2].setOnPreferenceChangeListener(this);
        mCustApp[2].setLayoutResource(R.layout.app_preference);

        mPrefSet.removePreference(mCatIcs);
        mPrefSet.removePreference(mCatAppOne);
        mPrefSet.removePreference(mCatAppTwo);
        mPrefSet.removePreference(mCatAppThree);

        mMaximizeWidgets = (CheckBoxPreference)findPreference(KEY_LOCKSCREEN_MAXIMIZE_WIDGETS);
        if (Helpers.isTablet(getActivity())) {
            getPreferenceScreen().removePreference(mMaximizeWidgets);
            mMaximizeWidgets = null;
        } else {
            mMaximizeWidgets.setOnPreferenceChangeListener(this);
        }

        // Dont display the lock clock preference if its not installed
        removePreferenceIfPackageNotInstalled(findPreference(KEY_LOCK_CLOCK));

        mVolumeRockerWake = (CheckBoxPreference) findPreference(PREF_VOLUME_ROCKER_WAKE);
        mVolumeRockerWake.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.VOLUME_WAKE_SCREEN, false));

        mLockscreenAutoRotate = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_AUTO_ROTATE);
        mLockscreenAutoRotate.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_AUTO_ROTATE, false));

        mLockscreenBattery = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_BATTERY);
        mLockscreenBattery.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_BATTERY, false));

        mLockscreenAllWidgets = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_ALL_WIDGETS);
        mLockscreenAllWidgets.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_ALL_WIDGETS, false));

        mLockscreenUnlimitedWidgets = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_UNLIMITED_WIDGETS);
        mLockscreenUnlimitedWidgets.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_UNLIMITED_WIDGETS, false));

        mCameraWidget = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_CAMERA_WIDGET);
        mCameraWidget.setChecked(Settings.System.getInt(mContentRes,
                Settings.System.KG_CAMERA_WIDGET, 0) == 1);

        mLockscreenTextColor = (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_TEXT_COLOR);
        mLockscreenTextColor.setOnPreferenceChangeListener(this);

        mLockscreenHideInitialPageHints = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS);
        mLockscreenHideInitialPageHints.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS, false));

        mLockscreenUseCarousel = (CheckBoxPreference)findPreference(PREF_LOCKSCREEN_USE_CAROUSEL);
        mLockscreenUseCarousel.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL, false));

        mCustomBackground = (ListPreference) findPreference(KEY_BACKGROUND_PREF);
        mCustomBackground.setOnPreferenceChangeListener(this);
        updateCustomBackgroundSummary();

        mWallpaperImage = new File(getActivity().getFilesDir() + "/lockwallpaper");
        mWallpaperTemporary = new File(getActivity().getCacheDir() + "/lockwallpaper.tmp");

        mWallpaperAlpha = (Preference) findPreference(KEY_LOCKSCREEN_BACKGROUND_ALPHA);

        mLockTransparent = (CheckBoxPreference) findPreference(LOCKSCREEN_TRANSPARENT_PREF);
        mLockTransparent.setChecked(Settings.System.getBoolean(mContentRes,
                Settings.System.LOCKSCREEN_TRANSPARENT, false));

        mLockScreenGlowTorch = (ListPreference) findPreference(LOCKSCREEN_GLOW_TORCH);
        int glowValue = Settings.System.getInt(mContentRes, 
                    Settings.System.LOCKSCREEN_GLOW_TORCH, 0);
        mLockScreenGlowTorch.setValue(String.valueOf(glowValue));
        mLockScreenGlowTorch.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    private void updateCustomBackgroundSummary() {
        int resId;
        String value = Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_BACKGROUND);
        if (value == null) {
            resId = R.string.lockscreen_background_default_wallpaper;
            mCustomBackground.setValueIndex(2);
        } else if (value.isEmpty()) {
            resId = R.string.lockscreen_background_custom_image;
            mCustomBackground.setValueIndex(1);
        } else {
            resId = R.string.lockscreen_background_color_fill;
            mCustomBackground.setValueIndex(0);
        }
        mCustomBackground.setSummary(getResources().getString(resId));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICK_WALLPAPER) {
            if (resultCode == Activity.RESULT_OK) {
                if (mWallpaperTemporary.exists()) {
                    mWallpaperTemporary.renameTo(mWallpaperImage);
                }
                mWallpaperImage.setReadable(true, false);
                Toast.makeText(mActivity, getResources().getString(R.string.
                        lockscreen_background_result_successful), Toast.LENGTH_LONG).show();
                Settings.System.putString(mContentRes,
                        Settings.System.LOCKSCREEN_BACKGROUND,"");
                updateCustomBackgroundSummary();
            } else {
                if (mWallpaperTemporary.exists()) {
                    mWallpaperTemporary.delete();
                }
                Toast.makeText(mActivity, getResources().getString(R.string.
                        lockscreen_background_result_not_successful), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMaximizeWidgets != null) {
            mMaximizeWidgets.setChecked(Settings.System.getInt(mContentRes,
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, 0) == 1);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mVolumeRockerWake) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.VOLUME_WAKE_SCREEN,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenAllWidgets) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_ALL_WIDGETS,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mCameraWidget) {
            Settings.System.putInt(mContentRes,
                    Settings.System.KG_CAMERA_WIDGET, 
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mLockscreenUnlimitedWidgets) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_UNLIMITED_WIDGETS,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenBattery) {
            Settings.System.putInt(mContentRes,
                    Settings.System.LOCKSCREEN_BATTERY,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mLockscreenAutoRotate) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_AUTO_ROTATE,
                    ((CheckBoxPreference) preference).isChecked());
            return true;
        } else if (preference == mLockscreenHideInitialPageHints) {
            Settings.System.putInt(mContentRes,
                    Settings.System.LOCKSCREEN_HIDE_INITIAL_PAGE_HINTS,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mLockscreenUseCarousel) {
            Settings.System.putInt(mContentRes,
                    Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL,
                    ((CheckBoxPreference)preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mWallpaperAlpha) {
            Resources res = getActivity().getResources();
            String cancel = res.getString(R.string.cancel);
            String ok = res.getString(R.string.ok);
            String title = res.getString(R.string.alpha_dialog_title);
            float savedProgress = Settings.System.getFloat(getActivity()
                        .getContentResolver(), Settings.System.LOCKSCREEN_BACKGROUND_ALPHA, 1.0f);

            LayoutInflater factory = LayoutInflater.from(getActivity());
            final View alphaDialog = factory.inflate(R.layout.seekbar_dialog, null);
            SeekBar seekbar = (SeekBar) alphaDialog.findViewById(R.id.seek_bar);
            OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekbar, int progress, boolean fromUser) {
                    seekbarProgress = seekbar.getProgress();
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekbar) {
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekbar) {
                }
            };
            seekbar.setProgress((int) (savedProgress * 100));
            seekbar.setMax(100);
            seekbar.setOnSeekBarChangeListener(seekBarChangeListener);
            new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setView(alphaDialog)
                    .setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // nothing
                }
            })
            .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    float val = ((float) seekbarProgress / 100);
                    Settings.System.putFloat(mContentRes,
                        Settings.System.LOCKSCREEN_BACKGROUND_ALPHA, val);
                }
            })
            .create()
            .show();
            return true;
        } else if (preference == mLockTransparent) {
            Settings.System.putBoolean(mContentRes,
                    Settings.System.LOCKSCREEN_TRANSPARENT,
                    ((CheckBoxPreference)preference).isChecked());
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean handled = false;
        if (preference == mCustomBackground) {
            int selection = mCustomBackground.findIndexOfValue(newValue.toString());
            return handleBackgroundSelection(selection);
        } else if (preference == mLockscreenTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mContentRes,
                    Settings.System.LOCKSCREEN_CUSTOM_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mMaximizeWidgets) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mContentRes,
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, value ? 1 : 0);
            return true;
        } else if (preference == mLockScreenGlowTorch) {
            int glowValue= Integer.valueOf((String) newValue);
            Settings.System.putInt(mContentRes,
                    Settings.System.LOCKSCREEN_GLOW_TORCH, glowValue);
            return true;
        } else if (preference == mLockscreenStyle) {
            int value = Integer.parseInt((String) newValue);
            switch (value) {
                //case LOCK_STYLE_ICS:
                // mPrefSet.removePreference(mCatAppTwo);
                // mPrefSet.addPreference(mCatIcs);
                // mPrefSet.addPreference(mCatAppOne);
                // mPrefSet.addPreference(mCatAppThree);
                // break;
                case LOCK_STYLE_ECLAIR:
                    //fallthrough
                case LOCK_STYLE_GB:
                    if (getResources().getBoolean(R.bool.config_disableOldLocks)) {
                        Toast.makeText(mActivity, getResources().getString(R.string.
                            pref_lockscreen_style_unavailable), Toast.LENGTH_LONG).show();
                        return true;
                    }
                default:
                    //mPrefSet.removePreference(mCatIcs);
                    //mPrefSet.removePreference(mCatAppOne);
                    //mPrefSet.removePreference(mCatAppTwo);
                    //mPrefSet.removePreference(mCatAppThree);
            }
            Settings.System.putInt(mContentRes, 
                    Settings.System.LOCKSCREEN_STYLE, value);
            mCurrLockscreen = value;
            return true;
        //} else if (preference == mCustApp[0]) {
        // return processPick(0, newValue);
        //} else if (preference == mCustApp[1]) {
        // return processPick(1, newValue);
        //} else if (preference == mCustApp[2]) {
        // return processPick(2, newValue);
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.lockscreens, menu);
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
    private boolean handleBackgroundSelection(int selection) {
        if (selection == LOCKSCREEN_BACKGROUND_COLOR_FILL) {
            final ColorPickerView colorView = new ColorPickerView(getActivity());
            int currentColor = Settings.System.getInt(mContentRes,
                    Settings.System.LOCKSCREEN_BACKGROUND, -1);

            if (currentColor != -1) {
                colorView.setColor(currentColor);
            }
            colorView.setAlphaSliderVisible(true);

            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.lockscreen_custom_background_dialog_title)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(mContentRes,
                                    Settings.System.LOCKSCREEN_BACKGROUND, colorView.getColor());
                            updateCustomBackgroundSummary();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setView(colorView)
                    .show();
        } else if (selection == LOCKSCREEN_BACKGROUND_CUSTOM_IMAGE) {
            final Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("scaleUpIfNeeded", false);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

            final Display display = getActivity().getWindowManager().getDefaultDisplay();
            final Rect rect = new Rect();
            final Window window = getActivity().getWindow();

            window.getDecorView().getWindowVisibleDisplayFrame(rect);

            int statusBarHeight = rect.top;
            int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
            int titleBarHeight = contentViewTop - statusBarHeight;
            boolean isPortrait = getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT;

            int width = display.getWidth();
            int height = display.getHeight() - titleBarHeight;

            intent.putExtra("aspectX", isPortrait ? width : height);
            intent.putExtra("aspectY", isPortrait ? height : width);

            try {
                mWallpaperTemporary.createNewFile();
                mWallpaperTemporary.setWritable(true, false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mWallpaperTemporary));
                intent.putExtra("return-data", false);
                getActivity().startActivityFromFragment(this, intent, REQUEST_PICK_WALLPAPER);
            } catch (IOException e) {
                // Do nothing here
            } catch (ActivityNotFoundException e) {
                // Do nothing here
            }
        } else if (selection == LOCKSCREEN_BACKGROUND_DEFAULT_WALLPAPER) {
            Settings.System.putString(mContentRes,
                    Settings.System.LOCKSCREEN_BACKGROUND, null);
            updateCustomBackgroundSummary();
            return true;
        }

        return false;
    }
}
