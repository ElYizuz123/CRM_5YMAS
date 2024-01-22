package com.example.crm5ymas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.suspendCancellableCoroutine

class LocationService {
    @SuppressLint("MissingPermission")
    suspend fun getUserLocation(context: Context): Location?{
        val fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context)
        if(!isLocationPermissionGranted(context)){
            return null
        }
        else{
            val locationManager=context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if(!isGPSEnabled){
                return null
            }
            return suspendCancellableCoroutine {cont->
                fusedLocationProviderClient.lastLocation.apply {
                    if(isComplete){
                        if(isSuccessful){
                            cont.resume(result){}
                        }else{
                            cont.resume(null){}
                        }
                        return@suspendCancellableCoroutine
                    }
                    addOnSuccessListener {
                        cont.resume(it){}
                    }
                    addOnFailureListener{
                        cont.resume(null){}
                    }
                    addOnCanceledListener {
                        cont.resume(null){}
                    }
                }
            }
        }

    }
    private fun isLocationPermissionGranted(context: Context)=
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED
}