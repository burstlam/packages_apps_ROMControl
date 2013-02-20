
package com.aokp.romcontrol.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;

import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.objects.EasyPair;
import com.aokp.romcontrol.util.Helpers;

import java.util.ArrayList;
import java.util.List;

public class StatusBarToggles extends AOKPPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "TogglesLayout";

    private static final String PREF_ENABLE_TOGGLES = "enabled_toggles";
    private static final String PREF_TOGGLES_PER_ROW = "toggles_per_row";
    private static final String PREF_TOGGLES_STYLE = "toggles_style";
    private static final String PREF_TOGGLE_FAV_CONTACT = "toggle_fav_contact";
    private static final String PREF_ENABLE_FASTTOGGLE = "enable_fast_toggle";
    private static final String PREF_NOTIFICATION_SHOW_WIFI_SSID = "notification_show_wifi_ssid";


    private final int PICK_CONTACT = 1;

    Preference mEnabledToggles;
    Preference mLayout;
    ListPreference mTogglesPerRow;
    ListPreference mTogglesStyle;
    Preference mFavContact;
    ListPreference mFastToggle;
    CheckBoxPreference mShowWifiName;

    BroadcastReceiver mReceiver;
    ArrayList<String> mToggles;

    static Bundle sToggles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra("toggle_bundle")) {
                    onTogglesUpdate(intent.getBundleExtra("toggle_bundle"));
                }
            }
        };
        mContext.registerReceiver(mReceiver,
                new IntentFilter("com.android.systemui.statusbar.toggles.ACTION_BROADCAST_TOGGLES"));
        requestAvailableToggles();
        setTitle(R.string.title_statusbar_toggles);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.prefs_statusbar_toggles);

        mEnabledToggles = findPreference(PREF_ENABLE_TOGGLES);

        mTogglesPerRow = (ListPreference) findPreference(PREF_TOGGLES_PER_ROW);
        mTogglesPerRow.setOnPreferenceChangeListener(this);
        mTogglesPerRow.setValue(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.QUICK_TOGGLES_PER_ROW, 3) + "");

        mTogglesStyle = (ListPreference) findPreference(PREF_TOGGLES_STYLE);
        mTogglesStyle.setOnPreferenceChangeListener(this);
        mTogglesStyle.setValue(String.valueOf(Settings.System.getInt(getActivity()
                .getContentResolver(),
                Settings.System.TOGGLES_STYLE, 0)));

        mLayout = findPreference("toggles");

        mFavContact = findPreference(PREF_TOGGLE_FAV_CONTACT);

        mFastToggle = (ListPreference) findPreference(PREF_ENABLE_FASTTOGGLE);
        mFastToggle.setOnPreferenceChangeListener(this);
        int statusFastToggle = Settings.System.getInt(getActivity().getContentResolver(),
            Settings.System.FAST_TOGGLE, 0);
        mFastToggle.setValue(String.valueOf(statusFastToggle));
        updateFastToggleSummary(statusFastToggle);


        mShowWifiName = (CheckBoxPreference) findPreference(PREF_NOTIFICATION_SHOW_WIFI_SSID);
        mShowWifiName.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 0) == 1);
        }


    static ArrayList<EasyPair<String, String>> buildToggleMap(Bundle toggleInfo) {
        ArrayList<String> _toggleIdents = toggleInfo.getStringArrayList("toggles");
        ArrayList<EasyPair<String, String>> _toggles = new ArrayList<EasyPair<String, String>>();
        for (String _ident : _toggleIdents) {
            _toggles.add(new EasyPair<String, String>(_ident, toggleInfo.getString(_ident)));
        }
        return _toggles;
    }

    private void onTogglesUpdate(Bundle toggleInfo) {
        mToggles = toggleInfo.getStringArrayList("toggles");
        sToggles = toggleInfo;
        if (mToggles.contains("FAVCONTACT")) {
            if (mFavContact != null) {
                mFavContact.setEnabled(true);
            }
        } else {
            if (mFavContact != null) {
                getPreferenceScreen().removePreference(mFavContact);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestAvailableToggles();
    }

    @Override
    public void onDestroy() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }

    private void requestAvailableToggles() {
        Intent request = new Intent("com.android.systemui.statusbar.toggles.ACTION_REQUEST_TOGGLES");
        mContext.sendBroadcast(request);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mTogglesPerRow) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.QUICK_TOGGLES_PER_ROW, val);
            return true;
        } else if (preference == mFastToggle) {
            int statusFastToggle = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(), 
                    Settings.System.FAST_TOGGLE, statusFastToggle);
            updateFastToggleSummary(statusFastToggle);
            return true;
        } else if (preference == mTogglesStyle) {
            int val = Integer.parseInt((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.TOGGLES_STYLE, val);
            mTogglesStyle.setValue((String) newValue);
            Helpers.restartSystemUI();
        }
        return true;
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mShowWifiName) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_SHOW_WIFI_SSID,
                    mShowWifiName.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mEnabledToggles) {
            if (mToggles == null || mToggles.isEmpty()) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            final ArrayList<String> userToggles = getEnabledToggles(mContext);
            final ArrayList<String> availableToggles = new ArrayList<String>();
            for (String t : mToggles) {
                availableToggles.add(t);
            }

            // final String[] finalArray = getResources().getStringArray(
            // R.array.available_toggles_entries);
            final String[] toggleValues = new String[availableToggles.size()];
            for (int i = 0; i < availableToggles.size(); i++) {
                toggleValues[i] = StatusBarToggles.lookupToggle(mContext, availableToggles.get(i));
            }

            final boolean checkedToggles[] = new boolean[availableToggles.size()];

            boolean anyChecked = false;
            for (int i = 0; i < checkedToggles.length; i++) {
                String selectedToggle = availableToggles.get(i);
                if (userToggles.contains(selectedToggle)) {
                    Log.d(TAG, "found toggle: " + selectedToggle);
                    checkedToggles[i] = true;
                    anyChecked = true;
                }
            }
            if (!anyChecked) {
                // no toggles are checked, wipe the setting to be sure
                Settings.System.putString(getContentResolver(), Settings.System.QUICK_TOGGLES, "");
            }

            builder.setTitle(R.string.toggles_display_dialog);
            builder.setCancelable(true);
            builder.setPositiveButton(R.string.toggles_display_close,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.setMultiChoiceItems(toggleValues, checkedToggles,
                    new OnMultiChoiceClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            String toggleKey = availableToggles.get(which);

                            if (isChecked)
                                StatusBarToggles.addToggle(getActivity(), toggleKey);
                            else
                                StatusBarToggles.removeToggle(getActivity(), toggleKey);

                            if ("FAVCONTACT".equals(toggleKey)) {
                                mFavContact.setEnabled(isChecked);
                            }
                        }
                    });

            AlertDialog d = builder.create();

            d.show();

            return true;
        } else if (preference == mLayout) {
            ArrangeTogglesFragment fragment = ArrangeTogglesFragment.newInstance(sToggles);
            fragment.show(getFragmentManager(), "arrange");
        }
        else if (preference == mFavContact) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, PICK_CONTACT);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);

    }
            
    private void updateFastToggleSummary(int value) {
        Resources res = getResources();

        if (value == 0) {
            mFastToggle.setSummary(res.getString(R.string.fast_toggle_off));
        } else if (value == 3) {
            mFastToggle.setSummary(res.getString(R.string.fast_toggle_always_summary));
        } else {
            String direction = res.getString(value == 2
                    ? R.string.fast_toggle_left
                    : R.string.fast_toggle_right);
            mFastToggle.setSummary(res.getString(R.string.toggle_enable_fasttoggle_summary, direction));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_CONTACT) {
                Uri contactData = data.getData();
                String[] projection = new String[] {
                        ContactsContract.Contacts.LOOKUP_KEY
                };
                String selection = ContactsContract.Contacts.DISPLAY_NAME + " IS NOT NULL";
                CursorLoader cursorLoader = new CursorLoader(getActivity().getBaseContext(),
                        contactData, projection, selection, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                if (cursor != null) {
                    try {
                        if (cursor.moveToFirst()) {
                            String lookup_key = cursor.getString(cursor
                                    .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                            Settings.System.putString(getActivity().getContentResolver(),
                                    Settings.System.QUICK_TOGGLE_FAV_CONTACT, lookup_key);
                        }
                    } finally {
                        cursor.close();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static synchronized void addToggle(Context context, String key) {
        ArrayList<String> enabledToggles = getEnabledToggles(context);
        if (enabledToggles.contains(key)) {
            enabledToggles.remove(key);
        }
        enabledToggles.add(key);
        setTogglesFromStringArray(context, enabledToggles);
    }

    static synchronized ArrayList<String> getEnabledToggles(Context context) {
        try {
            ArrayList<String> userEnabledToggles = new ArrayList<String>();
            String userToggles = Settings.System.getString(context.getContentResolver(),
                    Settings.System.QUICK_TOGGLES);

            String[] splitter = userToggles.split("\\|");
            for (String toggle : splitter) {
                userEnabledToggles.add(toggle);
            }
            return userEnabledToggles;
        } catch (Exception e) {
            if(sToggles != null && sToggles.containsKey("default_toggles")) {
                return sToggles.getStringArrayList("default_toggles");
            }
        }
        return new ArrayList<String>();
    }

    static synchronized void setTogglesFromStringArray(Context c, List<String> enabledToggles) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < enabledToggles.size(); i++) {
            final String _toggle = enabledToggles.get(i);
            if (_toggle.isEmpty()) {
                continue;
            }
            b.append(_toggle);
            b.append("|");
        }
        if (String.valueOf(b.charAt(b.length() - 1)).equals("!")) {
            b.deleteCharAt(b.length() - 1);
        }
        Log.d(TAG, "saving toggles:" + b.toString());
        Settings.System.putString(c.getContentResolver(), Settings.System.QUICK_TOGGLES,
                b.toString());
    }

    static synchronized void removeToggle(Context context, String key) {
        ArrayList<String> enabledToggles = getEnabledToggles(context);
        enabledToggles.remove(key);
        setTogglesFromStringArray(context, enabledToggles);
    }

    static String lookupToggle(Context c, String ident) {
        if (sToggles != null) {
            return sToggles.getString(ident.toUpperCase());
        }
        return ident;
    }

}
