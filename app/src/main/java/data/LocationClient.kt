package data

import android.location.Location
import kotlinx.coroutines.flow.Flow

//Interface for location background service
interface LocationClient {
    fun getLocationUpdates(interval: Long): Flow<Location>

    class LocationException(message: String): Exception()

}