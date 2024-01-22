package com.example.crm5ymas

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import data.Cliente

class ActivityClientInfo : AppCompatActivity() {
    lateinit var ruta:String
    lateinit var excelSheet:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_info)
        val cliente = intent.getSerializableExtra("cliente") as Cliente
        ruta = intent.getStringExtra("ruta")!!
        excelSheet = intent.getStringExtra("excelSheet")!!
        val textViewNombre = findViewById<TextView>(R.id.textViewNombreClienteInfo)
        val textViewContacto = findViewById<TextView>(R.id.textViewContactoInfo)
        val textViewTelefono = findViewById<TextView>(R.id.textViewTelefonoInfo)
        val textViewComentarios = findViewById<TextView>(R.id.textViewComentariosInfo)
        val textViewSeguimiento = findViewById<TextView>(R.id.textViewSeguimiento)
        val textViewMarcas = findViewById<TextView>(R.id.textViewMarcasManeja)
        val textViewProductos = findViewById<TextView>(R.id.textViewProductosManeja)
        val textViewConsumo = findViewById<TextView>(R.id.textViewConsumo)
        val textViewPeriodoConsumo = findViewById<TextView>(R.id.textViewPeriodoConsumo)
        val textViewUbicacion = findViewById<TextView>(R.id.textViewUbicacion)
        val textViewLinkMaps = findViewById<TextView>(R.id.textViewMapsInfo)
        val textViewLatitude = findViewById<TextView>(R.id.textViewLatitude)
        val textViewLongitud = findViewById<TextView>(R.id.textViewLongitude)
        val textViewLista = findViewById<TextView>(R.id.textViewLista)
        val buttonBack = findViewById<ImageButton>(R.id.imageButtonBackClientInfo)
        val textViewListaComentarios = findViewById<TextView>(R.id.textViewListaComentarios)
        val buttonEditContact = findViewById<ImageButton>(R.id.imageButtonEditContact)
        val buttonNewInteraction =
            findViewById<FloatingActionButton>(R.id.floatingActionNewInteraction)

        buttonEditContact.setOnClickListener{
            val segue = Intent(this, ActivityEditContact::class.java)
            segue.putExtra("excelSheet", excelSheet)
            segue.putExtra("ruta", ruta)
            segue.putExtra("cliente", cliente)
            startActivity(segue)
        }
        buttonBack.setOnClickListener {
            val segue = Intent(this, ActivityPrin::class.java)
            segue.putExtra("excelSheet", excelSheet)
            segue.putExtra("ruta", ruta)
            startActivity(segue)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            finish()
        }
        textViewNombre.setOnClickListener {
            try{
                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("Nombre negocio", textViewNombre.text.split("\n")[textViewNombre.text.split("\n").size-1])
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, "Nombre copiado al portapapeles", Toast.LENGTH_SHORT).show()
            }catch (ex: Exception){
                ex.printStackTrace()
            }

        }
        textViewContacto.setOnClickListener {
            try{
                val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("Contacto", textViewContacto.text.split("\n")[textViewContacto.text.split("\n").size-1])
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, "Contacto copiado al portapapeles", Toast.LENGTH_SHORT).show()
            }catch (ex: Exception){
                ex.printStackTrace()
            }

        }
        textViewTelefono.setOnClickListener {
            try {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "Telefono",
                    textViewTelefono.text.split("\n")[textViewTelefono.text.split("\n").size - 1]
                )
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, "Teléfono copiado al portapapeles", Toast.LENGTH_SHORT).show()
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }
        textViewUbicacion.setOnClickListener {
            try {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "Ubicacion",
                    textViewUbicacion.text.split("\n")[textViewUbicacion.text.split("\n").size - 1]
                )
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, "Ubicación copiado al portapapeles", Toast.LENGTH_SHORT).show()
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }
        textViewLinkMaps.setOnClickListener {
            try {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "Link maps",
                    textViewLinkMaps.text.split("\n")[textViewLinkMaps.text.split("\n").size - 1]
                )
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, "Link de maps copiado al portapapeles", Toast.LENGTH_SHORT)
                    .show()
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }
        textViewLatitude.setOnClickListener {
            try {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "Latitude",
                    textViewLatitude.text.split("\n")[textViewLatitude.text.split("\n").size - 1]
                )
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, "Latitud copiado al portapapeles", Toast.LENGTH_SHORT).show()
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }
        textViewLongitud.setOnClickListener {
            try {
                val clipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText(
                    "Longitude",
                    textViewLongitud.text.split("\n")[textViewLongitud.text.split("\n").size - 1]
                )
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(this, "Longitud", Toast.LENGTH_SHORT).show()
            }catch (ex: Exception){
                ex.printStackTrace()
            }
        }
        cliente.let {
            textViewNombre.setText(cliente.nombre)
            textViewContacto.setText("Contacto:\n"+cliente.contacto)
            textViewTelefono.setText("Telefono:\n"+cliente.telefono)
            textViewComentarios.setText("Comentario:\n"+cliente.comentarios)
            textViewSeguimiento.setText("Seguimiento:\n"+cliente.seguimiento)
            textViewMarcas.setText("Marcas que maneja:\n"+cliente.marcas)
            textViewProductos.setText("Productos que maneja:\n"+cliente.productos)
            textViewConsumo.setText("Consumo:\n"+cliente.consumo)
            textViewPeriodoConsumo.setText("Periodo de consumo:\n"+cliente.periodoCons)
            textViewUbicacion.setText("Ubicación:\n"+cliente.ubicacion)
            textViewLinkMaps.setText("Link de maps:\n"+cliente.mapsLink)
            textViewLatitude.setText("Latitud:\n"+cliente.latitud)
            textViewLongitud.setText("Longitud:\n"+cliente.longitud)
            textViewLista.setText("Lista:\n"+cliente.lista.toString())
            for(i in cliente.listaComentarios){
                textViewListaComentarios.setText(textViewListaComentarios.text.toString()+"\n"+"\n"+"Fecha:\n"+i.fecha+"\n"+"\n"+
                        "Tipo de contacto:\n"+i.tipoContacto+"\n"+"\n"+"Comentario:\n"+i.comentario+"\n"+"\n"+"Estado:\n"+i.estado+"\n"+"\n")
            }
        }

        buttonNewInteraction.setOnClickListener {
            val segue = Intent(this, ActivityQuiereComprar::class.java)
            segue.putExtra("id", cliente.id)
            segue.putExtra("nombre", cliente.nombre)
            segue.putExtra("lista", cliente.lista)
            segue.putExtra("ruta", ruta)
            segue.putExtra("excelSheet", excelSheet)
            startActivity(segue)
        }

    }

    override fun onBackPressed() {
        val segue = Intent(this, ActivityVerCliente::class.java)
        segue.putExtra("excelSheet", excelSheet)
        segue.putExtra("ruta", ruta)
        ActivityCompat.finishAffinity(this)
        startActivity(segue)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        finish()
    }
}