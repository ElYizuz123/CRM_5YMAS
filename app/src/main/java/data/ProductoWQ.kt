package data

import java.io.Serializable

data class ProductoWQ(
    var id: Int,
    var sku: String,
    var desc: String,
    var peso: Int,
    var precio: Double,
    var cantidad: Int
): Serializable
