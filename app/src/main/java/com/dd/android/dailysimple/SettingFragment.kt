package com.dd.android.dailysimple

import com.dd.android.dailysimple.R
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.dd.android.dailysimple.databinding.FragmentSettingsBinding

private const val TAG = "Setting"
class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
    }
}