package data

import java.io.Serializable

data class Producto(
    var id: Int,
    var sku: String,
    var desc: String,
    var peso: Int,
    var precio: Double,
    var grupo: Int
): Serializable
