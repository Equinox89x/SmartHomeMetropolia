package com.example.smarthomeproject


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.smarthomeproject.MyUtilities.isNetworkAvailable
import com.example.smarthomeproject.Repositories.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.smarthomeproject.Repositories.RepositoryStates.isUnset
import com.example.smarthomeproject.ViewModels.SmarthomeViewModel
import com.github.stephenvinouze.core.interfaces.RecognitionCallback
import com.github.stephenvinouze.core.managers.KontinuousRecognitionManager
import com.github.stephenvinouze.core.models.RecognitionStatus
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.math.roundToInt

private const val RECORD_AUDIO_REQUEST_CODE = 101
const val CHANNEL_ID = "1"

class MainFragment : Fragment(), SensorEventListener, RecognitionCallback {
    private lateinit var sm: SensorManager
    private var sLight: Sensor? = null
    private val _shutterRepo = ShutterRepositoryBluetooth()
    private var isDark: Boolean = false
    private lateinit var task2: TimerTask
    lateinit var mMap: GoogleMap
    lateinit var mTTS: TextToSpeech
    private val recognitionManager: KontinuousRecognitionManager by lazy {
        KontinuousRecognitionManager(context!!, MainActivity.ACTIVATION_KEYWORD, callback = this)
    }
    private val kitchenLightOn =
        LightRepository.service.postKitchenLight(LightRepository.Model.Kitchen("on"))
    private val bedroomLightOn =
        LightRepository.service.postBedroomLight(LightRepository.Model.Bedroom("on"))
    private val garageLightOn =
        LightRepository.service.postGarageLight(LightRepository.Model.Garage("on"))
    private val livingroomLightOn =
        LightRepository.service.postLivingroomLight(LightRepository.Model.Livingroom("on"))
    private val bedroomLightOff =
        LightRepository.service.postBedroomLight(LightRepository.Model.Bedroom("off"))
    private val garageLightOff =
        LightRepository.service.postGarageLight(LightRepository.Model.Garage("off"))
    private val livingroomLightOff =
        LightRepository.service.postLivingroomLight(LightRepository.Model.Livingroom("off"))
    private val kitchenLightOff =
        LightRepository.service.postKitchenLight(LightRepository.Model.Kitchen("off"))
    private val getAlarmRetro = AlarmRepository.service.getAlarm()
    private val getTemperatureRetro = TemperatureRepository.service.getTemperature()
    private val shutter1On =
        ShutterRepository.service.potShutterState(ShutterRepository.Model.Shutter("open"))
    private val shutter1Off =
        ShutterRepository.service.potShutterState(ShutterRepository.Model.Shutter("close"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val frag = inflater.inflate(R.layout.fragment_main, container, false)
        sm = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sLight = sm.getDefaultSensor(Sensor.TYPE_LIGHT)
        mTTS = TextToSpeech(
            (activity as MainActivity).applicationContext,
            TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR) {
                    mTTS.language = Locale.UK
                }
            })

        //region Click listeners
        frag.findViewById<FrameLayout>(R.id.frameLayoutHome)
            .setOnClickListener((activity as MainActivity))
        frag.findViewById<FrameLayout>(R.id.frameLayoutSettings)
            .setOnClickListener((activity as MainActivity))
        frag.findViewById<FrameLayout>(R.id.frameLayoutLights)
            .setOnClickListener((activity as MainActivity))
        frag.findViewById<FrameLayout>(R.id.frameLayoutShutters)
            .setOnClickListener((activity as MainActivity))
        frag.findViewById<FrameLayout>(R.id.frameLayoutTemperature).setOnClickListener {
            getTemperature()
            Toast.makeText(context!!, "Temperature refreshed", Toast.LENGTH_SHORT).show()
        }
        //endregion


        //region Google Maps & Livedata
        val mapFragment = SupportMapFragment.newInstance()
        mapFragment.getMapAsync(OnMapReadyCallback { googleMap ->
            mMap = googleMap
            mMap.uiSettings.isScrollGesturesEnabled = false
            mMap.uiSettings.isZoomGesturesEnabled = false
        })
        childFragmentManager.beginTransaction().replace(R.id.map, mapFragment).commit()

        val ump = ViewModelProviders.of(this).get(SmarthomeViewModel::class.java)
        ump.getHomeInfo().observe(this, Observer { it ->
            if (it != null) {
                frag.findViewById<TextView>(R.id.lblHome).text =
                    getString(R.string.dbname, HomeAdapter(it).items.HouseName)
                doAsync {
                    try {

                        val coder = Geocoder(context!!)
                        val address = coder.getFromLocationName(HomeAdapter(it).items.Street, 1);
                        val location = address.get(0);
                        val p1 = LatLng(location.latitude, location.longitude)
                        val housename = HomeAdapter(it).items.HouseName
                        uiThread {
                            mMap.addMarker(MarkerOptions().position(p1).title("Marker in " + housename))
                            frag.findViewById<TextView>(R.id.lblHome).contentDescription =
                                getString(R.string.dbname, housename)
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p1, 8f))

                        }
                    } catch (ex: Exception) {
                        Log.e("Error", ex.toString())
                    }
                }
                isUnset = false
            } else {
                frag.findViewById<TextView>(R.id.lblHome).text = getString(R.string.dbname, "unset")
                isUnset = true
            }
        })
        //endregion

        getTemperature()
        getAlarm()

        if ((Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                (activity as MainActivity),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            ActivityCompat.requestPermissions(
                (activity as MainActivity),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                0
            )
        }

        //region Electronic Home Manager Assistent (EHMA)
        val isTTS = activity!!.getSharedPreferences(SHARED_SETTINGS, Context.MODE_PRIVATE)
            .getBoolean(ENABLE_TTS, true)
        val showhints = activity!!.getSharedPreferences(SHARED_SETTINGS, Context.MODE_PRIVATE)
            .getBoolean(ENABLE_HINTS, true)
        if (isTTS) {
            startRecognition()
            if (showhints) {
                val builder = AlertDialog.Builder(context!!)
                builder.setTitle("Voice Commands")
                builder.setMessage("Say \"Hey Emma\" followed by \"What can I say\" to know the possible voice commands.")
                builder.setPositiveButton("OK") { dialog, which -> true }
                builder.show()
                mTTS.speak(
                    "Say, \"Hey Emma\" followed by,\"What can I say\" to know the possible voice commands.",
                    TextToSpeech.QUEUE_ADD,
                    null,
                    null
                )
                while (mTTS.isSpeaking) {
                }
            }
        } else {
            stopRecognition()
            recognitionManager.cancelRecognition()
            recognitionManager.destroyRecognizer()
        }
        //endregion

        return frag
    }

    //region Speech recognizer
    private fun startRecognition() {
        recognitionManager.startRecognition()
    }

    private fun stopRecognition() {
        recognitionManager.stopRecognition()
    }

    override fun onBeginningOfSpeech() {
//        Log.i("Recognition","onBeginningOfSpeech")
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i("Recognition", "onBufferReceived: $buffer")
    }

    override fun onEndOfSpeech() {
//        Log.i("Recognition","onEndOfSpeech")
    }

    private fun getErrorText(errorCode: Int): String = when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "No match"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
        SpeechRecognizer.ERROR_SERVER -> "Error from server"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
        else -> "Didn't understand, please try again."
    }

    override fun onError(errorCode: Int) {
        val errorMessage = getErrorText(errorCode)
        Log.i("Recognition", "onError: $errorMessage")
        Toast.makeText(context!!, errorMessage, Toast.LENGTH_LONG).show()
    }

    override fun onEvent(eventType: Int, params: Bundle) {
    }

    override fun onReadyForSpeech(params: Bundle) {
    }

    override fun onRmsChanged(rmsdB: Float) {
    }

    override fun onPrepared(status: RecognitionStatus) {
        when (status) {
            RecognitionStatus.SUCCESS -> {
                Log.i("Recognition", "onPrepared: Success")
            }
            RecognitionStatus.FAILURE,
            RecognitionStatus.UNAVAILABLE -> {
                Log.i("Recognition", "onPrepared: Failure or unavailable")
                AlertDialog.Builder(context!!)
                    .setTitle("Speech Recognizer unavailable")
                    .setMessage("Your device does not support Speech Recognition. Sorry!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }

    override fun onPartialResults(results: List<String>) {}

    override fun onResults(results: List<String>, scores: FloatArray?) {
        val text = results.joinToString(separator = "\n")
        Log.i("Recognition", "onResults : $text")
        Toast.makeText(context!!, text, Toast.LENGTH_LONG).show()
        if (text.contains("turn the kitchen lights on")) {
            kitchenLightOn.clone().enqueue(callbackKitchen)
            RepositoryStates.isKitchenLedOn = true
            mTTS.speak("Turned on the kitchen light for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("turn the bedroom lights on")) {
            bedroomLightOn.clone().enqueue(callbackBedroom)
            RepositoryStates.isBedroomLedOn = true
            mTTS.speak("Turned on the bedroom light for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("turn the garage lights on")) {
            garageLightOn.clone().enqueue(callbackGarage)
            RepositoryStates.isGarageLedOn = true
            mTTS.speak("Turned on the garage light for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("turn the living room lights on")) {
            livingroomLightOn.clone().enqueue(callbackLivingroom)
            RepositoryStates.isLivingroomLedOn = true
            mTTS.speak(
                "Turned on the living room light for you.",
                TextToSpeech.QUEUE_FLUSH,
                null
            )
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("turn the kitchen lights off")) {
            kitchenLightOff.clone().enqueue(callbackKitchen)
            RepositoryStates.isKitchenLedOn = false
            mTTS.speak("Turned off the kitchen light for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("turn the bedroom lights off")) {
            bedroomLightOff.clone().enqueue(callbackBedroom)
            RepositoryStates.isBedroomLedOn = false
            mTTS.speak("Turned off the bedroom light for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("turn the garage lights off")) {
            garageLightOff.clone().enqueue(callbackGarage)
            RepositoryStates.isGarageLedOn = false
            mTTS.speak("Turned off the garage light for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("turn the living room lights off")) {
            livingroomLightOff.clone().enqueue(callbackLivingroom)
            RepositoryStates.isLivingroomLedOn = false
            mTTS.speak(
                "Turned off the living room light for you.",
                TextToSpeech.QUEUE_FLUSH,
                null
            )
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("turn all lights off")) {
            kitchenLightOff.clone().enqueue(callbackKitchen)
            RepositoryStates.isKitchenLedOn = false
            bedroomLightOff.clone().enqueue(callbackBedroom)
            RepositoryStates.isBedroomLedOn = false
            garageLightOff.clone().enqueue(callbackGarage)
            RepositoryStates.isGarageLedOn = false
            livingroomLightOff.clone().enqueue(callbackLivingroom)
            RepositoryStates.isLivingroomLedOn = false
            mTTS.speak("Turned off the lights for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }
        }

        if (text.contains("turn all lights on")) {
            kitchenLightOn.clone().enqueue(callbackKitchen)
            RepositoryStates.isKitchenLedOn = true
            bedroomLightOn.clone().enqueue(callbackBedroom)
            RepositoryStates.isBedroomLedOn = true
            garageLightOn.clone().enqueue(callbackGarage)
            RepositoryStates.isGarageLedOn = true
            livingroomLightOn.clone().enqueue(callbackLivingroom)
            RepositoryStates.isLivingroomLedOn = true
            mTTS.speak("Turned on the lights for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("what can I say")) {
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle("Voice Commands")
            builder.setMessage("Open/Close the shutters\nTurn all lights on/off\nTurn bedroom lights on/off\nTurn garage lights on/off\nTurn livingroom lights on/off\nTurn kitchen lights on/off\nWhat can I say\nRefresh items")
            builder.setPositiveButton("OK") { dialog, which -> true }
            builder.show()
            mTTS.speak("These are the commands you can use.", TextToSpeech.QUEUE_ADD, null, null)
            while (mTTS.isSpeaking()) {
            }
        }

        if (text.contains("refresh items")) {
            getTemperature()
            mTTS.speak("I refreshed the items for you.", TextToSpeech.QUEUE_ADD, null, null)
            while (mTTS.isSpeaking()) {
            }
        }

        if (text.contains("open the shutters")) {
            shutter1On.clone().enqueue(callbackShutter)
            mTTS.speak("Opened the shutters for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }

        }

        if (text.contains("close the shutters")) {
            shutter1Off.clone().enqueue(callbackShutter)
            mTTS.speak("Closed the shutters for you.", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }
        }

        if (text.contains("thanks for your help")) {
            stopRecognition()
            mTTS.speak("You're welcome!", TextToSpeech.QUEUE_FLUSH, null)
            while (mTTS.isSpeaking()) {
            }
        }
    }

    override fun onKeywordDetected() {
        Log.i("Recognition", "keyword detected !!!")
        Toast.makeText(context!!, "Listening... ", Toast.LENGTH_LONG).show()
    }
    //endregion

    //region Temperature
    val callbackTemperature = object : Callback<TemperatureRepository.Model.Temperature> {
        override fun onFailure(call: Call<TemperatureRepository.Model.Temperature>, t: Throwable) {
        }

        override fun onResponse(
            call: Call<TemperatureRepository.Model.Temperature>,
            response: Response<TemperatureRepository.Model.Temperature>
        ) {
            if (response.isSuccessful) {
                val info: TemperatureRepository.Model.Temperature = response.body()!!
                val temp = info.temperature!!.toFloat().roundToInt()
                var tempF = ""
                var tempUnit = ""
                val fahrenheit =
                    activity!!.getSharedPreferences(SHARED_SETTINGS, Context.MODE_PRIVATE)
                        .getBoolean(USE_FAHRENHEIT, false)
                if (!fahrenheit) {
                    tempF = temp.toString()
                    tempUnit = "°C"
                } else {
                    tempF = celsiusToFahrenheit(temp.toFloat()).toString()
                    tempUnit = "°F"
                }
                if (lblTemperature != null) {
                    lblTemperature.text = getString(R.string.temperature, tempF, tempUnit)
                    lblTemperature.contentDescription =
                        getString(R.string.accessTemperature, temp.toString(), tempUnit)
                }
            }
        }
    }

    private fun getTemperature() {
        doAsync {
            if (isNetworkAvailable(context!!)) {
                try {
                    getTemperatureRetro.clone().enqueue(callbackTemperature)
                } catch (e: IOException) {
                    Log.e("Request", "Error ", e)
                }
            }
        }
    }

    private fun celsiusToFahrenheit(cel: Float): Int {
        return ((cel * 9 / 5) + 32).roundToInt()
    }
//endregion

    //region Alarm
    val callbackAlarm = object : Callback<AlarmRepository.Model.Alarm> {
        override fun onFailure(call: Call<AlarmRepository.Model.Alarm>, t: Throwable) {
        }

        override fun onResponse(
            call: Call<AlarmRepository.Model.Alarm>,
            response: Response<AlarmRepository.Model.Alarm>
        ) {
            if (response.isSuccessful) {
                val info: AlarmRepository.Model.Alarm = response.body()!!
                val alarmstatus = info.alarm

                val notificationIntent = Intent(context!!, MainActivity::class.java)
                val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context!!).run {
                    // Add the intent, which inflates the back stack
                    addNextIntentWithParentStack(notificationIntent)
                    // Get the PendingIntent containing the entire back stack
                    getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
                }
                if (lblAlarm != null) {
                    if (alarmstatus == "triggered") {
                        frameLayoutAlarm.background =
                            getDrawable(context!!, R.drawable.ic_ripple_red)
                        lblAlarm.text = getString(R.string.alarmTriggered, alarmstatus)
                        lblAlarm.setTextColor(resources.getColor(R.color.colorWhite))
                        lblAlarm.contentDescription = getString(R.string.accessAlarmTriggered)
                        createNotificationChannel()
                        val notif = NotificationCompat.Builder(
                            (activity as AppCompatActivity).applicationContext,
                            CHANNEL_ID
                        )
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle("Alarm")
                            .setContentText("Your alarm has been triggered!")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(resultPendingIntent)
                            .build()
                        NotificationManagerCompat.from((activity as AppCompatActivity).applicationContext)
                            .notify(1, notif)
                    } else {
                        frameLayoutAlarm.background =
                            getDrawable(context!!, R.drawable.ic_ripple_green)
                        lblAlarm.text = getString(R.string.alarmUntriggered, alarmstatus)
                        lblAlarm.contentDescription = getString(R.string.accessAlarmUntriggered)
                    }
                }
            }
        }
    }

    private fun getAlarm() {
        doAsync {
            val timer = Timer()
            task2 = object : TimerTask() {
                override fun run() {
                    if (isNetworkAvailable(context!!)) {
                        try {
                            getAlarmRetro.clone().enqueue(callbackAlarm)
                        } catch (e: IOException) {
                            Log.e("Request", "Error ", e)
                        }
                    }
                }
            }
            timer.schedule(task2, 0, 5500)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "test",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "desc Test" }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getActivity()?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
//endregion

    //region AMBIENT LIGHT SENSOR (Internal sensor #1)
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (RepositoryStates.manualIntervention == false) { //manualIntervention should be changed to a shared preferences bool
            if (event.sensor.type == Sensor.TYPE_LIGHT) {
                val lightSensorReading = event.values[0]
                if (lightSensorReading >= 40) {
                    if (isDark) {
                        if (isNetworkAvailable(context!!)) {
                            kitchenLightOff.clone().enqueue(callbackKitchen)
                            RepositoryStates.isKitchenLedOn = false
                            bedroomLightOff.clone().enqueue(callbackBedroom)
                            RepositoryStates.isBedroomLedOn = false
                            garageLightOff.clone().enqueue(callbackGarage)
                            RepositoryStates.isGarageLedOn = false
                            livingroomLightOff.clone().enqueue(callbackLivingroom)
                            RepositoryStates.isLivingroomLedOn = false
                        }
                        Log.d("Lights", "Turned off because value is " + lightSensorReading)
                    }
                    isDark = false
                }

                if (lightSensorReading < 40) {
                    if (!isDark) {
                        if (isNetworkAvailable(context!!)) {
                            kitchenLightOn.clone().enqueue(callbackKitchen)
                            RepositoryStates.isKitchenLedOn = true
                            bedroomLightOn.clone().enqueue(callbackBedroom)
                            RepositoryStates.isBedroomLedOn = true
                            garageLightOn.clone().enqueue(callbackGarage)
                            RepositoryStates.isGarageLedOn = true
                            livingroomLightOn.clone().enqueue(callbackLivingroom)
                            RepositoryStates.isLivingroomLedOn = true
                        }
                        Log.d("Lights", "Turned on because value is " + lightSensorReading)
                    }
                    isDark = true
                }

            }
        }
    }
//endregion

    //region lifecycle functions
    override fun onPause() {
        // Be sure to unregister the sensor when the activity pauses.
        super.onPause()
        sm.unregisterListener(this)
        task2.cancel()
        stopRecognition()
        recognitionManager.cancelRecognition()
        recognitionManager.destroyRecognizer()
    }

    override fun onResume() {
        // Register a listener for the sensor.
        super.onResume()
        sLight?.also { light ->
            sm.registerListener(this, light, 2000)
        }
        val isTTS = activity!!.getSharedPreferences(SHARED_SETTINGS, Context.MODE_PRIVATE)
            .getBoolean(ENABLE_TTS, true)
        if (isTTS) {
            startRecognition()
        } else {
            stopRecognition()
            recognitionManager.cancelRecognition()
            recognitionManager.destroyRecognizer()
        }
        RepositoryStates.manualIntervention = false
    }
//endregion
}