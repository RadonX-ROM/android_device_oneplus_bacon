/*
* Copyright (C) 2015 The AOSParadox Project
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*
*/
package org.omnirom.device;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.os.SystemProperties;

public class KeyDisabler implements OnPreferenceChangeListener {

    private static final String FILE = "/proc/touchpanel/keypad_enable";
    private static final String HW_BRIGHTNESS = "/sys/class/leds/button-backlight/brightness";

    public static boolean isSupported() {
        return Utils.fileWritable(FILE);
    }

    public static boolean isEnabled(Context context) {
        boolean enabled = Utils.getFileValueAsBoolean(FILE, false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getBoolean(DeviceSettings.KEY_DISABLER, enabled);
    }

    /**
     * Restore setting from SharedPreferences. (Write to kernel.)
     * @param context       The context to read the SharedPreferences from
     */
    public static void restore(Context context) {
        if (!isSupported()) {
            return;
        }

        boolean enabled = isEnabled(context);
        if(enabled) {
            Utils.writeValue(FILE, "0");
            Utils.writeValue(HW_BRIGHTNESS, "0");
            SystemProperties.set("qemu.hw.mainkeys", "0");
        } else {
            Utils.writeValue(FILE, "1");
            Utils.writeValue(HW_BRIGHTNESS, "50");
	}
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Boolean enabled = (Boolean) newValue;
        if(enabled) {
            Utils.writeValue(FILE, "0");
            Utils.writeValue(HW_BRIGHTNESS, "0");
            SystemProperties.set("qemu.hw.mainkeys", "0");
        } else {
            Utils.writeValue(FILE, "1");
            Utils.writeValue(HW_BRIGHTNESS, "50");
	}
        return true;
    }

}

