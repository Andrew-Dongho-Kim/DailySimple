package com.dd.android.dailysimple

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

private const val TAG = "Setting"
class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}