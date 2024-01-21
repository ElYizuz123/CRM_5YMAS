package com.example.crmpovasa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentContainerView
import data.Cliente
import com.google.gson.Gson

class ActivityMaps : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val jsonClientes = intent.getStringExtra("jsonClientes")
        val fragmentViewer = findViewById<ConstraintLayout>(R.id.layoutFragmentMaps)
        if(jsonClientes!=null){
            fragmentViewer.removeAllViews()
            var fragment = FragmentMapaClientes.newInstance(jsonClientes)
            supportFragmentManager.beginTransaction().add(R.id.layoutFragmentMaps, fragment).commit()
        }
    }

}