package com.example.crm5ymas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout

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