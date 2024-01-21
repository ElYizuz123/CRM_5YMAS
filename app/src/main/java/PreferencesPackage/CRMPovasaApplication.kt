package PreferencesPackage

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class CRMPovasaApplication : Application(){
    //Declare the shared Preferences
    companion object{
        lateinit var preferences: Preferences
    }
    override fun onCreate() {
        super.onCreate()
        preferences = Preferences(applicationContext)
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                "location",
                "locaion",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}