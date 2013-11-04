package com.aokp.romcontrol.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.util.Helpers;
import com.aokp.romcontrol.util.ShortcutPickerHelper;
import com.aokp.romcontrol.widgets.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class StatusBarNotifications extends AOKPPreferenceFragment implements OnPreferenceChangeListener {

    private static final CharSequence PREF_STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final CharSequence PREF_NOTIFICATION_WALLPAPER = "notification_wallpaper";
    private static final CharSequence PREF_NOTIFICATION_WALLPAPER_ALPHA =
            "notification_wallpaper_alpha";
    private static final CharSequence PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final CharSequence PREF_VIBRATE_NOTIF_EXPAND = "vibrate_notif_expand";
    private static final CharSequence PREF_STATUSBAR_BRIGHTNESS = "statusbar_brightness_slider";
    private static final CharSequence PREF_NOTIFICATION_VIBRATE = "notification";

    private static final int REQUEST_PICK_WALLPAPER = 201;

    private static final String WALLPAPER_NAME = "notification_wallpaper.jpg";


    private static final String STATUS_BAR_CARRIER_LABEL = "status_bar_carrier_label";
    private static final String STATUS_BAR_CARRIER_COLOR = "status_bar_carrier_color";
    private static final CharSequence  PREF_NOTIFICATION_BEHAVIOUR = "notifications_behaviour";
    private static final CharSequence KEY_STATUS_BAR_ICON_OPACITY = "status_bar_icon_opacity";
    private static final String PREF_NOTIFICATION_ALPHA = "notification_alpha";
    private static final CharSequence STATUS_BAR_BEHAVIOR = "status_bar_behavior";
    private static final String STATUS_BAR_QUICK_PEEK = "status_bar_quick_peek";


    CheckBoxPreference mStatusBarNotifCount;
    Preference mNotificationWallpaper;
    Preference mWallpaperAlpha;
    Preference mCustomLabel;
    CheckBoxPreference mVibrateOnExpand;
    CheckBoxPreference mStatusbarSliderPreference;
    CheckBoxPreference mStatusBarHide;
    String mCustomLabelText = null;


    CheckBoxPreference mStatusBarCarrierLabel;
    ColorPickerPreference mCarrierColorPicker;
    ListPreference mNotificationsBehavior;
    SeekBarPreference mNotifAlpha;
    ListPreference mStatusBarBeh;
    CheckBoxPreference mStatusBarQuickPeek;
    ListPreference mStatusBarIconOpacity;

    private int mUiMode;
    private int mSeekbarProgress;
    private static int mBarBehavior;


    private static ContentResolver mContentResolver;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.title_statusbar_notifications);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_statusbar_notifications);
        mContentResolver = getContentResolver();

        int defaultColor;
        int intColor;
        String hexColor;

        PreferenceScreen prefs = getPreferenceScreen();
        mNotificationsBehavior = (ListPreference) findPreference(PREF_NOTIFICATION_BEHAVIOUR);
        int CurrentBehavior = Settings.System.getInt(getContentResolver(),
                Settings.System.NOTIFICATIONS_BEHAVIOUR, 0);
        mNotificationsBehavior.setValue(String.valueOf(CurrentBehavior));
        mNotificationsBehavior.setSummary(mNotificationsBehavior.getEntry());
        mNotificationsBehavior.setOnPreferenceChangeListener(this);

        mStatusBarNotifCount = (CheckBoxPreference) findPreference(PREF_STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.STATUSBAR_NOTIF_COUNT, false));

        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);
        updateCustomLabelTextSummary();

        mStatusbarSliderPreference = (CheckBoxPreference) findPreference(PREF_STATUSBAR_BRIGHTNESS);
        mStatusbarSliderPreference.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.STATUSBAR_BRIGHTNESS_SLIDER, true));

        mNotificationWallpaper = findPreference(PREF_NOTIFICATION_WALLPAPER);

        mWallpaperAlpha = (Preference) findPreference(PREF_NOTIFICATION_WALLPAPER_ALPHA);

        mVibrateOnExpand = (CheckBoxPreference) findPreference(PREF_VIBRATE_NOTIF_EXPAND);
        mVibrateOnExpand.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.VIBRATE_NOTIF_EXPAND, true));
        if (!hasVibration) {
            ((PreferenceGroup) findPreference(PREF_NOTIFICATION_VIBRATE))
                    .removePreference(mVibrateOnExpand);
        }

        mStatusBarBeh = (ListPreference) findPreference(STATUS_BAR_BEHAVIOR);
        int mBarBehavior = Settings.System.getInt(mContentResolver,
                Settings.System.HIDE_STATUSBAR, 0);
        mStatusBarBeh.setValue(Integer.toString(Settings.System.getInt(mContentResolver,
                Settings.System.HIDE_STATUSBAR, mBarBehavior)));
        updateStatusBarBehaviorSummary(mBarBehavior);
        mStatusBarBeh.setOnPreferenceChangeListener(this);

        mStatusBarIconOpacity = (ListPreference) findPreference(KEY_STATUS_BAR_ICON_OPACITY);
        int iconOpacity = Settings.System.getInt(mContentResolver,
                Settings.System.STATUS_BAR_NOTIF_ICON_OPACITY, 140);
        mStatusBarIconOpacity.setValue(String.valueOf(iconOpacity));
        mStatusBarIconOpacity.setOnPreferenceChangeListener(this);

        mStatusBarQuickPeek = (CheckBoxPreference) findPreference(STATUS_BAR_QUICK_PEEK);
        mStatusBarQuickPeek.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUSBAR_PEEK, 0) == 1));

        mStatusBarCarrierLabel = (CheckBoxPreference) findPreference(STATUS_BAR_CARRIER_LABEL);
        mStatusBarCarrierLabel.setChecked(Settings.System.getBoolean(mContentResolver,
                Settings.System.STATUS_BAR_CARRIER, true));

        mCarrierColorPicker = (ColorPickerPreference) findPreference(STATUS_BAR_CARRIER_COLOR);
        mCarrierColorPicker.setOnPreferenceChangeListener(this);
        defaultColor = getResources().getColor(
                com.android.internal.R.color.holo_blue_light);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_COLOR, defaultColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCarrierColorPicker.setSummary(hexColor);
        mCarrierColorPicker.setNewPreviewColor(intColor);

        float notifTransparency;
        try{
            notifTransparency = Settings.System.getFloat(getActivity().getContentResolver(), Settings.System.NOTIF_ALPHA);
        }catch (Exception e) {
            notifTransparency = 0;
            Settings.System.putFloat(getActivity().getContentResolver(), Settings.System.NOTIF_ALPHA, 0);
        }
        mNotifAlpha = (SeekBarPreference) findPreference(PREF_NOTIFICATION_ALPHA);
        mNotifAlpha.setInitValue((int) (notifTransparency * 100));
        mNotifAlpha.setProperty(Settings.System.NOTIF_ALPHA);
        mNotifAlpha.setOnPreferenceChangeListener(this);

        mUiMode = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.CURRENT_UI_MODE, 0);

        if (mUiMode == 1) {
            mStatusbarSliderPreference.setEnabled(false);
            mStatusBarQuickPeek.setEnabled(false);
            mNotificationWallpaper.setEnabled(false);
            mStatusbarSliderPreference.setSummary(R.string.enable_phone_or_phablet);
            mStatusBarQuickPeek.setSummary(R.string.enable_phone_or_phablet);
            mStatusBarHide.setSummary(R.string.enable_phone_or_phablet);
            mNotificationWallpaper.setSummary(R.string.enable_phone_or_phablet);
        }
        findWallpaperStatus();
    }

    private void updateStatusBarBehaviorSummary(int value) {
        switch (value) {
            case 0:
                mStatusBarBeh.setSummary(getResources().getString(R.string.statusbar_show_summary));
                break;
            case 1:
                mStatusBarBeh.setSummary(getResources().getString(R.string.statusbar_hide_summary));
                break;
            case 2:
                mStatusBarBeh.setSummary(getResources().getString(R.string.statusbar_auto_rem_summary));
                break;
            case 3:
                mStatusBarBeh.setSummary(getResources().getString(R.string.statusbar_auto_all_summary));
                break;
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        if (preference == mStatusBarNotifCount) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.STATUSBAR_NOTIF_COUNT,
                    ((TwoStatePreference) preference).isChecked());
            return true;
        } else if (preference == mNotificationWallpaper) {
            File wallpaper = new File(mContext.getFilesDir(), WALLPAPER_NAME);
            if (wallpaper.exists()) {
                buildWallpaperAlert();
            } else {
                prepareAndSetWallpaper();
            }
            return true;
        } else if (preference == mWallpaperAlpha) {
            Resources res = getActivity().getResources();
            String cancel = res.getString(R.string.cancel);
            String ok = res.getString(R.string.ok);
            String title = res.getString(R.string.alpha_dialog_title);
            float savedProgress = Settings.System.getFloat(mContentResolver,
                    Settings.System.NOTIF_WALLPAPER_ALPHA, 1.0f);

            LayoutInflater factory = LayoutInflater.from(getActivity());
            View alphaDialog = factory.inflate(R.layout.seekbar_dialog, null);
            SeekBar seekbar = (SeekBar) alphaDialog.findViewById(R.id.seek_bar);
            SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekbar,
                                              int progress, boolean fromUser) {
                    mSeekbarProgress = seekbar.getProgress();
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
                            float val = (float) mSeekbarProgress / 100;
                            Settings.System.putFloat(mContentResolver,
                                    Settings.System.NOTIF_WALLPAPER_ALPHA, val);
                            Helpers.restartSystemUI();
                        }
                    })
                    .create()
                    .show();
            return true;
        } else if (preference == mStatusbarSliderPreference) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.STATUSBAR_BRIGHTNESS_SLIDER,
                    isCheckBoxPrefernceChecked(preference));
            return true;
        } else if (preference == mCustomLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText mView to get user input
            final EditText input = new EditText(getActivity());
            final InputFilter[] filter = new InputFilter[1];
            filter[0] = new InputFilter.LengthFilter(40);

            input.setFilters(filter);
            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
            alert.setView(input);
            alert.setPositiveButton(getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String value = input.getText().toString();
                            Settings.System.putString(mContentResolver,
                                    Settings.System.CUSTOM_CARRIER_LABEL, value);
                            updateCustomLabelTextSummary();
                            Intent i = new Intent();
                            i.setAction("com.aokp.romcontrol.LABEL_CHANGED");
                            mContext.sendBroadcast(i);
                        }
                    });
            alert.setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });
            alert.show();
        } else if (preference == mVibrateOnExpand) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.VIBRATE_NOTIF_EXPAND,
                    ((TwoStatePreference) preference).isChecked());
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mStatusBarQuickPeek) {
            Settings.System.putBoolean(mContentResolver,
                    Settings.System.STATUSBAR_PEEK,
                    ((TwoStatePreference) preference).isChecked());
            return true;
        } else if (preference == mStatusBarCarrierLabel) {
            boolean checked = mStatusBarCarrierLabel.isChecked();
            Settings.System.putBoolean(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER, checked ? true : false);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mCarrierColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_COLOR, intHex);
            return true;
        } else if (preference == mNotificationsBehavior) {
            String val = (String) newValue;
            Settings.System.putInt(getContentResolver(),
                Settings.System.NOTIFICATIONS_BEHAVIOUR,
            Integer.valueOf(val));
            int index = mNotificationsBehavior.findIndexOfValue(val);
            mNotificationsBehavior.setSummary(mNotificationsBehavior.getEntries()[index]);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mNotifAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIF_ALPHA, valNav / 100);
            return true;
        } else if (preference == mStatusBarBeh) {
            int mBarBehavior = Integer.valueOf((String) newValue);
            int index = mStatusBarBeh.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HIDE_STATUSBAR, mBarBehavior);
            mStatusBarBeh.setSummary(mStatusBarBeh.getEntries()[index]);
            updateStatusBarBehaviorSummary(mBarBehavior);
            Helpers.restartSystemUI();
            return true;
        } else if (preference == mStatusBarIconOpacity) {
            int iconOpacity = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_NOTIF_ICON_OPACITY, iconOpacity);
            return true;
        } else if (preference == mCarrierColorPicker) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CARRIER_COLOR, intHex);
            return true;
        }
        return false;
    }

    private Uri getNotificationExternalUri() {
        File dir = mContext.getExternalCacheDir();
        File wallpaper = new File(dir, WALLPAPER_NAME);
        return Uri.fromFile(wallpaper);
    }

    public void findWallpaperStatus() {
        File wallpaper = new File(mContext.getFilesDir(), WALLPAPER_NAME);
        if (mUiMode != 1 && wallpaper.exists()) {
            mWallpaperAlpha.setEnabled(true);
            mWallpaperAlpha.setSummary(null);
        } else {
            mWallpaperAlpha.setEnabled(false);
            mWallpaperAlpha.setSummary(R.string.enable_noti_wallpaper);
        }
    }

    private void prepareAndSetWallpaper() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = getActivity().getWallpaperDesiredMinimumWidth();
        int height = getActivity().getWallpaperDesiredMinimumHeight();
        float spotlightX = (float)display.getWidth() / width;
        float spotlightY = (float)display.getHeight() / height;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("aspectX", width);
        intent.putExtra("aspectY", height);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("spotlightX", spotlightX);
        intent.putExtra("spotlightY", spotlightY);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                getNotificationExternalUri());
        intent.putExtra("outputFormat",
                Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
    }

    private void resetWallpaper() {
        mContext.deleteFile(WALLPAPER_NAME);
        findWallpaperStatus();
        Helpers.restartSystemUI();
    }




    private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(mContentResolver,
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null || mCustomLabelText.isEmpty()) {
            mCustomLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
    }

    private void buildWallpaperAlert() {
        Drawable myWall = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.notification_wallpaper_dialog);
        builder.setPositiveButton(R.string.notification_wallpaper_pick,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prepareAndSetWallpaper();
                    }
                });
        builder.setNegativeButton(R.string.notification_wallpaper_reset,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        resetWallpaper();
                        dialog.dismiss();
                    }
                });
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View layout = inflater.inflate(R.layout.dialog_shade_wallpaper, null);
        ImageView wallView = (ImageView) layout.findViewById(R.id.shade_wallpaper_preview);
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        wallView.setLayoutParams(new LinearLayout.LayoutParams(size.x / 2, size.y / 2));
        File wallpaper = new File(mContext.getFilesDir(), WALLPAPER_NAME);
        myWall = new BitmapDrawable(mContext.getResources(), wallpaper.getAbsolutePath());
        wallView.setImageDrawable(myWall);
        builder.setView(layout);
        builder.show();
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_WALLPAPER) {
                FileOutputStream wallpaperStream = null;
                try {
                    wallpaperStream = mContext.openFileOutput(WALLPAPER_NAME,
                            Context.MODE_WORLD_READABLE);
                    Uri selectedImageUri = getNotificationExternalUri();
                    Bitmap bitmap = BitmapFactory.decodeFile(
                            selectedImageUri.getPath());
                    bitmap.compress(Bitmap.CompressFormat.PNG,
                            100,
                            wallpaperStream);
                } catch (FileNotFoundException e) {
                    return; // NOOOOO
                } finally {
                    try {
                        if (wallpaperStream != null) {
                            wallpaperStream.close();
                        }
                    } catch (IOException e) {
                        // let it go
                    }
                }
                findWallpaperStatus();
                buildWallpaperAlert();
                Helpers.restartSystemUI();
            }
        }
    }


}
