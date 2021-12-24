package com.example.smarthomeproject

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.smarthomeproject.Repositories.RepositoryStates

//import com.example.smarthomeproject.Repositories.AlarmRepository





class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        RepositoryStates.setActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(
            R.id.fcontainer,
            MainFragment()
        ).commit()
    }

    override fun onClick(v: View?) {
        when (v?.getId()) {

            R.id.frameLayoutHome -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fcontainer,
                    DBFragment()
                ).addToBackStack(null).commit()
            }
            R.id.frameLayoutSettings -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fcontainer,
                    SettingsFragment()
                ).addToBackStack(null).commit()
            }
            R.id.frameLayoutLights -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fcontainer,
                    LightsListFragment()
                ).addToBackStack(null).commit()
            }
            R.id.frameLayoutShutters -> {
                supportFragmentManager.beginTransaction().replace(
                    R.id.fcontainer,
                    ShuttersListFragment()
                ).addToBackStack(null).commit()
            }
            else -> {
            }
        }
    }

    companion object{
        const val ACTIVATION_KEYWORD = "Hey Emma"
    }
}

