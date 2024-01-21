package data

import PreferencesPackage.CRMPovasaApplication.Companion.preferences
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.crmpovasa.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationService: Service() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var distancias = mutableListOf<Distancia>()
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
        }
    }
    private val serviceScope = CoroutineScope(SupervisorJob()+Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }
    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radioTierra = 6371.0 // Radio de la Tierra en kilómetros

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distancia = radioTierra * c

        return distancia
    }
    //Here we start tracking
    @SuppressLint("MissingPermission")
    private fun start(){
        //Here we know if its nesessary to save
        if(readList("distancias.dat", this)!=null){
            distancias=readList("distancias.dat", this)!!.toMutableList()
            println(distancias)
        }
        var comp=false
        distancias.forEach {dist ->
            if(dist.fecha== preferences.getDate()){
                dist.distancia= preferences.getDistance()
                dist.horasTrabajo= preferences.getWorkingHours()
                if(preferences.getDate()!=LocalDate.now().toString()&& preferences.getDate()!=""){
                    preferences.saveDistance(0f, "", 0f)
                }
                comp=true
            }
        }
        if(!comp){
            if(preferences.getDate()!=LocalDate.now().toString()&& preferences.getDate()!=""){
                distancias.add(Distancia(preferences.getDistance(), preferences.getWorkingHours(), preferences.getDate()))
                preferences.saveDistance(0f, "", 0f)
            }

        }
        saveList(distancias.toList(), "distancias.dat", this)
        var wait=0
        var distance = 0.0
        val formatNum = DecimalFormat("#.###")
        var prevLat = 0.0
        var prevLong = 0.0
        var prevHour = LocalTime.now()
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Realizando seguimiento...")
            .setContentText("Distancia: 0 km")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //This is the function that works on every location

        locationClient.getLocationUpdates(3000L)
            .catch { e->e.printStackTrace() }
                //Here we have the distance
            .onEach {location ->
                if(wait<10){
                    wait++
                }
                else{
                    if(preferences.getDate()!=LocalDate.now().toString()){
                        distancias.add(Distancia(preferences.getDistance(), preferences.getWorkingHours(), preferences.getDate()))
                        preferences.saveDistance(0f, "", 0f)
                    }
                    var hourDifference = ChronoUnit.HOURS.between(prevHour, LocalTime.now()).toFloat()
                    var minuteDifference = ChronoUnit.MINUTES.between(prevHour, LocalTime.now()).toFloat()
                    var secondsDifference = ChronoUnit.SECONDS.between(prevHour, LocalTime.now()).toFloat()
                    prevHour= LocalTime.now()
                    if(prevLat!=0.0 && prevLong!=0.0){
                        distance=calculateDistance(prevLat, prevLong, location.latitude, location.longitude)
                        prevLat=location.latitude
                        prevLong=location.longitude
                    }
                    else{
                        prevLat=location.latitude
                        prevLong=location.longitude
                    }
                    preferences.saveDistance(distance.toFloat()+preferences.getDistance(), LocalDate.now().toString(),
                        abs((hourDifference+minuteDifference/60+secondsDifference/3600)+ preferences.getWorkingHours())
                    )
                    val updatedNotification = notification.setContentText(
                        "Distancia: (${formatNum.format(preferences.getDistance())} Km)"
                    )
                    println("Ubicacion: (${distance})")
                    println("Fecha: ${preferences.getDate()}")
                    println("Tiempo: ${(preferences.getWorkingHours()).toDouble()}")
                    notificationManager.notify(1, updatedNotification.build())
                }
            }
            .launchIn(serviceScope)
        startForeground(1, notification.build())
    }

    //Here we stop tracking
    private fun stop(){
        if(readList("distancias.dat", this)!=null){
            distancias=readList("distancias.dat", this)!!.toMutableList()
        }
        var comp=false
        distancias.forEach {dist ->
            if(dist.fecha== preferences.getDate()){
                dist.distancia= preferences.getDistance()
                dist.horasTrabajo= preferences.getWorkingHours()
                comp=true
            }
        }
        if(!comp){
            distancias.add(Distancia(preferences.getDistance(), preferences.getWorkingHours(), preferences.getDate()))
        }
        if(preferences.getDate()!=""){

        }
        if(preferences.getDate()!=""){
            saveList(distancias.toList(), "distancias.dat", this)
        }
        try{
            stopForeground(true)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            stopSelf()
        }catch (ex: Exception){
            ex.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object{
        const val ACTION_START= "ACTION_START"
        const val ACTION_STOP= "ACTION_STOP"
    }

    //Functions for save and read the list of distances
    fun saveList(objetos: List<Distancia>, nombreArchivo: String, context: Context) {
        try {
            val fileOutputStream = context.openFileOutput(nombreArchivo, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(objetos)
            objectOutputStream.close()
            fileOutputStream.close()
            println("Guardado correctamente")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun readList(nombreArchivo: String, context: Context): List<Distancia>? {
        var objetos: List<Distancia>? = null
        try {
            val fileInputStream = context.openFileInput(nombreArchivo)
            val objectInputStream = ObjectInputStream(fileInputStream)
            objetos = objectInputStream.readObject() as List<Distancia>
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return objetos
    }


}