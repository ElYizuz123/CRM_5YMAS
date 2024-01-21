package com.example.crmpovasa

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import data.Cliente

class FragmentMapaClientes : Fragment() {
    private var jsonClientes: String? = null
    lateinit var listaClientes: List<Cliente>
    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        for(i in listaClientes){
            if(i.latitud!=null&&i.longitud!=null&&i.latitud!=""&&i.longitud!=""){
                val sydney = LatLng(i.latitud.toDouble(), i.longitud.toDouble())
                googleMap.addMarker(MarkerOptions().position(sydney).title(i.nombre))
            }
        }
        if(listaClientes.size>0){
            for (i in 0..listaClientes.size-1){
                if(listaClientes[i].latitud!=null&&listaClientes[i].longitud!=null&&listaClientes[i].latitud!=""&&listaClientes[i].longitud!=""){
                    val referencia = LatLng(listaClientes[0].latitud.toDouble(), listaClientes[0].longitud.toDouble())
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(referencia))
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(referencia, 15f),
                        2000,
                        null
                    )
                    break
                }
            }
        }
        if(isLocationPermissionGranted()){
            googleMap.isMyLocationEnabled =true
        }
        else{
            Toast.makeText(requireContext(), "La localización no esta disponible", Toast.LENGTH_SHORT).show()
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jsonClientes = it.getString("json")
        }
        listaClientes= Gson().fromJson(jsonClientes, Array<Cliente>::class.java).toList()
        println(jsonClientes)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mapa_clientes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    companion object {

        fun newInstance(jsonClientes: String) =
            FragmentMapaClientes().apply {
                arguments = Bundle().apply {
                    putSerializable("json", jsonClientes)
                }
            }
    }
    private fun isLocationPermissionGranted()=
        ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED
}