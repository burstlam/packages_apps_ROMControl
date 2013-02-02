/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aokp.romcontrol.fragments;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aokp.romcontrol.R;
import com.aokp.romcontrol.AOKPPreferenceFragment;
import com.aokp.romcontrol.util.CMDProcessor;
import com.aokp.romcontrol.util.Helpers;
import com.aokp.romcontrol.fragments.Utils;

import java.io.*;
import java.util.*;

public class Applications {

    public static class BeerbongAppInfo {
        public String name = "";
        public String pack = "";
        public Drawable icon;
        public ApplicationInfo info;
        public int dpi;
    }
    
    private static class AppComparator implements Comparator {
        public int compare (Object o1, Object o2){
            BeerbongAppInfo a1 = (BeerbongAppInfo)o1;
            BeerbongAppInfo a2 = (BeerbongAppInfo)o2;
            return a1.name.compareTo(a2.name);
        }
    }
    
    private static final String BACKUP = "/storage/sdcard0/properties.conf";

    private static final String APPEND_CMD = "echo \"%s=%s\" >> /system/etc/burstlam/properties.conf";
    private static final String REPLACE_CMD = "busybox sed -i \"/%s/ c %<s=%s\" /system/etc/burstlam/properties.conf";
    private static final String PROP_EXISTS_CMD = "grep -q %s /system/etc/burstlam/properties.conf";
    private static final String REMOUNT_CMD = "busybox mount -o %s,remount -t yaffs2 /dev/block/mtdblock1 /system";
    
    private static final CMDProcessor cmd = new CMDProcessor();
    
    private static List<BeerbongAppInfo> appList = new ArrayList();
    private static int mLastDpi = 0;
    
    public static void addApplication(Context mContext, String packageName) {
        addApplication(mContext, findAppInfo(mContext, packageName), mLastDpi);
    }
    
    public static void addApplication(Context mContext, BeerbongAppInfo app, int dpi) {
    
        if (!mount("rw")) {
            throw new RuntimeException("Could not remount /system rw");
        }
        try {
            if (propExists(app.pack + ".dpi")) {
                cmd.su.runWaitFor(String.format(REPLACE_CMD, app.pack + ".dpi", String.valueOf(dpi)));
            } else {
                cmd.su.runWaitFor(String.format(APPEND_CMD, app.pack + ".dpi", String.valueOf(dpi)));
            }
            if (app.pack.equals("com.android.systemui")) {
                Utils.restartUI();
            } else {
                try {
                    IActivityManager am = ActivityManagerNative.getDefault();
                    am.forceStopPackage(app.pack, UserHandle.myUserId());
                } catch (android.os.RemoteException ex) {
                    // ignore
                }
            }
        } finally {
            mount("ro");
        }
        checkAutoBackup(mContext);
    }
    
    public static void removeApplication(Context mContext, String packageName) {
        if (!mount("rw")) {
            throw new RuntimeException("Could not remount /system rw");
        }
        try {
            if (propExists(packageName)) {
                cmd.su.runWaitFor(String.format(REPLACE_CMD, packageName + ".dpi", "0"));
            }
            if (packageName.equals("com.android.systemui")) {
                Utils.restartUI();
            } else {
                try {
                    IActivityManager am = ActivityManagerNative.getDefault();
                    am.forceStopPackage(packageName, UserHandle.myUserId());
                } catch (android.os.RemoteException ex) {
                    // ignore
                }
            }
        } finally {
            mount("ro");
        }
        checkAutoBackup(mContext);
    }
    
    public static BeerbongAppInfo[] getApplicationList(Context mContext, int dpi) {
    
        mLastDpi = dpi;
    
        Properties properties = null;
    
        try {
            properties = new Properties();
            properties.load(new FileInputStream("/system/etc/burstlam/properties.conf"));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        
        String sdpi = String.valueOf(dpi);
        
        List<BeerbongAppInfo> items = new ArrayList();
        
        Iterator it = properties.keySet().iterator();
        while (it.hasNext()) {
            String packageName = (String)it.next();
            String currentdpi = properties.getProperty(packageName);
            if (packageName.endsWith(".dpi") && sdpi.equals(currentdpi)) {
                BeerbongAppInfo bAppInfo = findAppInfo(mContext, packageName);

                if (bAppInfo == null) {
                    removeApplication(mContext, packageName.substring(0, packageName.lastIndexOf(".dpi")));
                } else {
                    items.add(bAppInfo);
                }
            }
        }
        
        Collections.sort(items, new AppComparator());
        
        return items.toArray(new BeerbongAppInfo[items.size()]);
    }
    
    public static BeerbongAppInfo[] getApplicationList(Context mContext) {
    
        Properties properties = null;
    
        try {
            properties = new Properties();
            properties.load(new FileInputStream("/system/etc/burstlam/properties.conf"));
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        
        PackageManager pm = mContext.getPackageManager();

        List<ApplicationInfo> mPackageList = pm.getInstalledApplications(0);
        BeerbongAppInfo[] items = new BeerbongAppInfo[mPackageList == null ? 0 : mPackageList.size()];
        
        appList.clear();

        for(int i=0; mPackageList != null && i<mPackageList.size(); i++) {
            ApplicationInfo app = mPackageList.get(i);
            items[i] = new BeerbongAppInfo();
            items[i].name = (String)pm.getApplicationLabel(app);
            items[i].icon = pm.getApplicationIcon(app);
            items[i].pack = app.packageName;
            items[i].info = app;
            items[i].dpi = properties.getProperty(app.packageName) == null ? 0 : Integer.parseInt(properties.getProperty(app.packageName));
            appList.add(items[i]);
        }
        Arrays.sort(items, new AppComparator());
        return items;
    }
    
    public static void backup(Context mContext) {
        Utils.execute(new String[] {
            "cd /data/data/com.aokp.romcontrol",
            "mkdir files",
            "chmod 777 files",
            "cp /system/etc/burstlam/properties.conf " + BACKUP,
            "chmod 644 " + BACKUP
        }, 0);
        Toast.makeText(mContext, R.string.dpi_groups_backup_done, Toast.LENGTH_SHORT).show();
    }
    
    public static void restore(Context mContext) {
        Utils.execute(new String[] {
            Utils.MOUNT_SYSTEM_RW,
            "cp " + BACKUP + " /system/etc/burstlam/properties.conf",
            "chmod 644 /system/etc/burstlam/properties.conf",
            Utils.MOUNT_SYSTEM_RO
        }, 0);
        Toast.makeText(mContext, R.string.dpi_groups_restore_done, Toast.LENGTH_SHORT).show();
    }
    
    public static boolean backupExists() {
        return new File(BACKUP).exists();
    }
    
    private static void checkAutoBackup(Context mContext) {
        boolean isAutoBackup = mContext.getSharedPreferences(DpiGroups.PREFS_NAME, 0).getBoolean(DpiGroups.PROPERTY_AUTO_BACKUP, false);
        if (isAutoBackup) {
            backup(mContext);
        }
    }
    private static boolean mount(String read_value) {
        return cmd.su.runWaitFor(String.format(REMOUNT_CMD, read_value)).success();
    }
    private static boolean propExists(String prop) {
        return cmd.su.runWaitFor(String.format(PROP_EXISTS_CMD, prop)).success();
    }
    private static BeerbongAppInfo findAppInfo(Context mContext, String packageName) {
        if (packageName.endsWith(".dpi")) {
            packageName = packageName.substring(0, packageName.lastIndexOf(".dpi"));
        }
        if (appList.size() == 0) {
            getApplicationList(mContext);
        }
        for (int i=0;i<appList.size();i++) {
            BeerbongAppInfo app = appList.get(i);
            if (app.pack.equals(packageName)) return app;
        }
        return null;
    }
    private static String read() throws Exception {
        StringBuffer sb = new StringBuffer();

        FileInputStream fstream = new FileInputStream("/system/etc/burstlam/properties.conf");

        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = br.readLine();

        while (line != null) {

            sb.append(line + "\n");
            line = br.readLine();

        }
        return sb.toString();
    }
}
