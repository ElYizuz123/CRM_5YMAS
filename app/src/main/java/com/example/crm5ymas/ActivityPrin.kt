package com.example.crm5ymas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ActivityPrin : AppCompatActivity() {
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prin)
        val textRuta = findViewById<TextView>(R.id.textNombreClienteQ)
        val buttonNuevoCliente = findViewById<FloatingActionButton>(R.id.floatingActionButtonNuevoCliente)
        val buttonVerCliente = findViewById<FloatingActionButton>(R.id.floatingActionButtonVisitaCliente)
        progressBar = findViewById(R.id.progressBarActivityPrin)
        val buttonBack = findViewById<ImageButton>(R.id.imageButtonBackPrin)
        val excelSheet = intent.getStringExtra("excelSheet")
        val ruta = intent.getStringExtra("ruta")
        textRuta.setText(ruta)
        buttonNuevoCliente.setOnClickListener{
            val segue = Intent(this, ActivityNuevoCliente::class.java)
            segue.putExtra("excelSheet", excelSheet)
            segue.putExtra("ruta", ruta)
            startActivity(segue)
        }
        buttonVerCliente.setOnClickListener {
            progressBar.visibility= View.VISIBLE
            val segue = Intent(this, ActivityVerCliente::class.java)
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            segue.putExtra("excelSheet", excelSheet)
            segue.putExtra("ruta", ruta)
            startActivity(segue)
        }
        buttonBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            finish()
        }

    }
    override fun onResume() {
        super.onResume()
        progressBar.visibility= View.INVISIBLE
    }
}