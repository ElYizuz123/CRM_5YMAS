package data

import java.io.Serializable

data class Comment(
    var fecha: String,
    var tipoContacto: String,
    var comentario: String,
    var estado: String,
): Serializable
