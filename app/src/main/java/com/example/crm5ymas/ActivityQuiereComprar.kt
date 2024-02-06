package com.example.crm5ymas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView

class ActivityQuiereComprar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiere_comprar)
        val clientNombre= intent.getStringExtra("nombre")
        val clientId=intent.getIntExtra("id",0)
        val clientList = intent.getIntExtra("lista", 0)
        val ruta = intent.getStringExtra("ruta")
        val excelSheet = intent.getStringExtra("excelSheet")
        val buttonNoCompra = findViewById<ImageButton>(R.id.imageButtonSinPedido)
        val buttonCompra = findViewById<ImageButton>(R.id.imageButtonNuevoPedido)
        val textViewNombreCliente = findViewById<TextView>(R.id.textNombreClienteQ)
        val buttonBack = findViewById<ImageButton>(R.id.imageButtonBackQuiereComp)
        textViewNombreCliente.setText(clientNombre)
        buttonBack.setOnClickListener {
            val segue = Intent(this, ActivityPrin::class.java)
            segue.putExtra("excelSheet", excelSheet)
            segue.putExtra("ruta", ruta)
            startActivity(segue)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            finish()
        }
        buttonNoCompra.setOnClickListener{
            val segue = Intent(this, ActivityRazonNoCompra::class.java)
            segue.putExtra("id", clientId)
            segue.putExtra("ruta", ruta)
            segue.putExtra("excelSheet", excelSheet)
            startActivity(segue)
        }
        buttonCompra.setOnClickListener{
            val segue = Intent(this, ActivityCotizacion::class.java)
            segue.putExtra("id", clientId)
            segue.putExtra("lista", clientList)
            segue.putExtra("ruta", ruta)
            segue.putExtra("excelSheet", excelSheet)
            segue.putExtra("nombre", clientNombre)
            startActivity(segue)
        }
    }
}