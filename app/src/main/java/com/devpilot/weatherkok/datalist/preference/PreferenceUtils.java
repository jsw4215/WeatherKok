package com.devpilot.weatherkok.datalist.preference;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

public class PreferenceUtils extends PreferenceChangeEvent {
    private static final String TAG = PreferenceUtils.class.getSimpleName();


    /**
     * Constructs a new <code>PreferenceChangeEvent</code> instance.
     *
     * @param node     The Preferences node that emitted the event.
     * @param key      The key of the preference that was changed.
     * @param newValue The new value of the preference, or <tt>null</tt>
     */
    public PreferenceUtils(Preferences node, String key, String newValue) {
        super(node, key, newValue);
    }
}
