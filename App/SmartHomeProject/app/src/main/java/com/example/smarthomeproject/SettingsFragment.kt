package com.example.smarthomeproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.CompoundButton
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.ImageView


const val SHARED_SETTINGS: String = "mysharedprefs"
const val USE_FAHRENHEIT: String = "temp_use_fahrenheit"
const val ENABLE_TTS : String = "enable_tts"
const val ENABLE_HINTS : String = "enable_hints"

class SettingsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val frag = inflater.inflate(R.layout.fragment_settings, container, false)

        sharedPreferences = activity!!.getSharedPreferences(SHARED_SETTINGS, MODE_PRIVATE)

        setupSwitch(frag.findViewById(R.id.switch1), USE_FAHRENHEIT)
        setupSwitch(frag.findViewById(R.id.switch3), ENABLE_HINTS)
        setupSwitch(frag.findViewById(R.id.switch2), ENABLE_TTS) {
            if (it) {
                // TODO MainFragment.startRecognition()
            } else {
                // TODO MainFragment.stopRecognition()
            }
        }


        frag.findViewById<ImageView>(R.id.btnBackSettings).setOnClickListener {
            //Had to be done this way, because the TTS needed a full fragment reset in order to update the setting.
            activity!!.supportFragmentManager.beginTransaction().remove(MainFragment()).commit()
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.fcontainer, MainFragment()).commit()
        }

        return frag
    }

    private fun setupSwitch(switch: Switch, shared_preferences_key: String) {
        return setupSwitch(switch, shared_preferences_key) {}
    }

    private fun setupSwitch(switch: Switch, shared_preferences_key: String, onCheckedChangeCallback: (Boolean) -> Unit){
        switch.isChecked = sharedPreferences.getBoolean(shared_preferences_key, false)
        switch.contentDescription = getString(R.string.settingOffOn, if (switch.isChecked) "on" else "off")
        switch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            val editor = sharedPreferences.edit()
            editor.putBoolean(shared_preferences_key, isChecked)
            editor.apply()
            onCheckedChangeCallback(isChecked)
        }
    }
}