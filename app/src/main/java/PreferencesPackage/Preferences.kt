package PreferencesPackage

import android.content.Context
import java.time.LocalDate

//We configure the preferences
class Preferences(val context: Context){
    val PATH_ARCHIVO_CLIENTES="pathClientes"
    val CORDS_NUEVO_CLIENTE="clientCords"
    val DISTANCE_TRACKING = "distanceTracking"
    val PATH_CLIENTES="pathArchivo"
    val CORDS="cords"
    val ADRESS="adress"
    val DISTANCE="distance"
    val DATE = "date"
    val WORKINGHOURS = "hours"
    val storage = context.getSharedPreferences(PATH_ARCHIVO_CLIENTES,0)
    val storageCords = context.getSharedPreferences(CORDS_NUEVO_CLIENTE, 0)
    val storageDistanceInfo = context.getSharedPreferences(DISTANCE_TRACKING, 0)

    //Indicator for tracking

    fun getDistance():Float{
        if(storageDistanceInfo.getFloat(DISTANCE,0f)!=null){
            return storageDistanceInfo.getFloat(DISTANCE, 0f)!!
        }
        else{
            return(0f)
        }
    }
    fun getDate():String{
        if(storageDistanceInfo.getString(DATE,"")!=null){
            return storageDistanceInfo.getString(DATE, "")!!
        }
        else{
            return("")
        }
    }
    fun getWorkingHours():Float{
        if(storageDistanceInfo.getFloat(WORKINGHOURS,0f)!=null){
            return storageDistanceInfo.getFloat(WORKINGHOURS, 0f)!!
        }
        else{
            return(0f)
        }
    }
    fun saveDistance(distance: Float, date: String, hours: Float){
        storageDistanceInfo.edit().putFloat(DISTANCE, distance).apply()
        storageDistanceInfo.edit().putString(DATE, date).apply()
        storageDistanceInfo.edit().putFloat(WORKINGHOURS, hours).apply()
    }
    fun saveLocation(cords: String, adress: String){
        storageCords.edit().putString(CORDS, cords).apply()
        storageCords.edit().putString(ADRESS, adress).apply()
    }
    fun getCords():String{
        if(storageCords.getString(CORDS, "")!=null){
            return storageCords.getString(CORDS, "")!!
        }
        else{
            return("")
        }
    }
    fun getAdress(): String{
        if(storageCords.getString(ADRESS, "")!=null){
            return storageCords.getString(ADRESS, "")!!
        }
        else{
            return("")
        }
    }

    fun savePath(path:String){
        storage.edit().putString(PATH_CLIENTES, path).apply()
    }
    fun getPath():String{
        if(storage.getString(PATH_CLIENTES, "")!=null){
            return storage.getString(PATH_CLIENTES, "")!!
        }
        else{
            return("")
        }
    }
}