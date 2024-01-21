package data

import java.io.Serializable

data class Cliente(
    var id : Int,
    var nombre: String,
    var contacto: String,
    var telefono: String,
    var comentarios: String,
    var seguimiento: String,
    var marcas: String,
    var productos: String,
    var consumo: String,
    var periodoCons: String,
    var ubicacion: String,
    var mapsLink: String,
    var latitud: String,
    var longitud: String,
    var lista: Int,
    var listaComentarios: ArrayList<Comment>,
) : Serializable

