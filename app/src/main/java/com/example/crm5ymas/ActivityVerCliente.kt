package com.example.crm5ymas

import Adapters.AdapterRecyclerClientes
import PreferencesPackage.CRMPovasaApplication.Companion.preferences
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import data.Cliente
import data.Comment
import data.Cords
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.lang.Math.abs

class ActivityVerCliente : AppCompatActivity() {
    private val coroutineScope = lifecycleScope
    private var comp=true
    lateinit var recyclerViewClientes: RecyclerView
    private val location = LocationService()
    var excelSheet=""
    var ruta=""
    lateinit var listaClientes:ArrayList<Cliente>
    lateinit var orderedList: List<Cliente>
    lateinit var cordsList: MutableList<Cords>
    lateinit var orderedClientList : ArrayList<Cliente>
    lateinit var adapterRecycler : AdapterRecyclerClientes
    lateinit var editTextFiltroClientes: EditText
    lateinit var filterList: MutableList<Cliente>
    lateinit var orderedAbsoluteList: MutableList<Cliente>
    var cordAnt=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_cliente)
        val buttonMaps = findViewById<FloatingActionButton>(R.id.floatingActionButtonMaps)
        editTextFiltroClientes=findViewById(R.id.editTextTextFiltroClientes)
        recyclerViewClientes = findViewById(R.id.recyclerViewClientes)
        val buttonBack = findViewById<ImageButton>(R.id.imageButtonBackVerCliente)
        listaClientes= ArrayList()
        excelSheet= intent.getStringExtra("excelSheet")!!
        ruta = intent.getStringExtra("ruta")!!
        readDB()
        orderedList=listaClientes
        filterList=orderedList.toMutableList()
        cordsList = MutableList(listaClientes.size){Cords(0.0,0)}
        orderedClientList=ArrayList()
        adapterRecycler=AdapterRecyclerClientes(this@ActivityVerCliente, orderedList , ruta, excelSheet)
        recyclerViewClientes.apply {
            layoutManager=LinearLayoutManager(this@ActivityVerCliente)
            adapter=adapterRecycler
        }
        buttonMaps.setOnClickListener {
            val gson = Gson()
            val json = gson.toJson(orderedAbsoluteList)
            val segue = Intent(this, ActivityMaps::class.java)
            segue.putExtra("jsonClientes", json)
            startActivity(segue)
        }
        setOrder()
        buttonBack.setOnClickListener {
            val segue = Intent(this, ActivityPrin::class.java)
            segue.putExtra("excelSheet", excelSheet)
            segue.putExtra("ruta", ruta)
            startActivity(segue)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            finish()
        }
        editTextFiltroClientes.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                try{
                    coroutineScope.cancel()
                    comp=false
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    orderedClientList.clear()
                    for(i in orderedAbsoluteList){
                        val search= i.nombre.lowercase().trim()+i.id+i.latitud+i.longitud+i.lista+i.comentarios.lowercase().trim()+
                                i.consumo.lowercase().trim()+i.contacto.lowercase().trim()+i.mapsLink.lowercase().trim()+
                                i.marcas.lowercase().trim()+i.periodoCons.lowercase().trim()+i.telefono.lowercase().trim()+
                                i.productos.lowercase().trim()+i.seguimiento.lowercase().trim()+i.ubicacion.lowercase().trim()
                        if(search.contains(editTextFiltroClientes.text.toString().lowercase().trim())){
                            orderedClientList.add(i)
                        }
                    }
                    filterList=orderedClientList.toMutableList()
                    println(orderedAbsoluteList.size)
                    recyclerViewClientes.adapter=AdapterRecyclerClientes(this@ActivityVerCliente, filterList , ruta, excelSheet)
                }catch (ex: Exception){
                    ex.printStackTrace()
                }

            }

        })
    }

    private fun setOrder() {
        obtenerUbicacion()
        var first=true
        coroutineScope.launch (Dispatchers.IO){
            while(comp){
                val result = location.getUserLocation(this@ActivityVerCliente)
                if(result!=null){
                    if((""+result?.latitude+","+result?.longitude)!=cordAnt){
                        cordAnt=""+result?.latitude+","+result?.longitude
                        for(i in 0..listaClientes.size-1){
                            var diffLatitude=99999.9
                            var diffLongitude=99999.9
                            if(listaClientes[i].latitud!=""){
                                diffLatitude = abs(listaClientes[i].latitud.toDouble()-result.latitude)
                            }
                            else{
                                diffLatitude=99999.9
                            }
                            if(listaClientes[i].longitud!=""){
                                diffLongitude = abs(listaClientes[i].longitud.toDouble()-result.longitude)
                            }
                            else{
                                diffLongitude=99999.9
                            }
                            cordsList[i]=Cords(abs(diffLatitude+diffLongitude),i)
                        }
                        cordsList.sortBy { it.diff }
                        orderedClientList.clear()
                        for(i in 0..cordsList.size-1){
                            orderedClientList.add(listaClientes[cordsList[i].ind])
                        }
                        orderedList=orderedClientList
                        orderedAbsoluteList=orderedList.toMutableList()
                        withContext(Dispatchers.Main) {
                            adapterRecycler.updateList(orderedList)
                        }

                    }
                }
                if(first){
                    recyclerViewClientes.smoothScrollToPosition(0)
                    first=false
                }
                Thread.sleep(1000)
            }
        }
    }

    private fun isLocationPermissionGranted()=
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION
        )== PackageManager.PERMISSION_GRANTED

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
        }
    }
    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion() {
        if(isLocationPermissionGranted()) {
            val locationRequest = LocationRequest.create().apply {
                interval = 5000
                fastestInterval = 2000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            if (isLocationPermissionGranted()) {

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null
                )

            }
        }
    }

    private fun readDB() {
        try {
            if(preferences.getPath()!=null){
                val file = File(preferences.getPath())
                if(file.exists()){
                    val workbook : Workbook = WorkbookFactory.create(FileInputStream(preferences.getPath()))
                    val sheet: Sheet = workbook.getSheet(excelSheet)
                    var rowNumber=1
                    while(sheet?.getRow(rowNumber)?.getCell(0)?.stringCellValue!=null&&sheet.getRow(rowNumber).getCell(0).stringCellValue!=""){
                        var row=sheet.getRow(rowNumber)
                        var cliente = Cliente(0,"","","","","","","","","",
                        "","","","",0, ArrayList()
                        )
                        for(i in 0..13){
                            var cell=row.getCell(i)
                            if(cell!=null){
                                when(i){
                                    0->{
                                        cliente.id=rowNumber
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.nombre=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.nombre=cell.stringCellValue
                                        }
                                    }
                                    1->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.contacto=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.contacto=cell.stringCellValue
                                        }
                                    }
                                    2->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.telefono=cell.numericCellValue.toLong().toString()
                                        }
                                        else{
                                            cliente.telefono=cell.stringCellValue
                                        }
                                    }
                                    3->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.comentarios=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.comentarios=cell.stringCellValue
                                        }
                                    }
                                    4->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.seguimiento=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.seguimiento=cell.stringCellValue
                                        }
                                    }
                                    5->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.marcas=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.marcas=cell.stringCellValue
                                        }
                                    }
                                    6->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.productos=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.productos=cell.stringCellValue
                                        }
                                    }
                                    7->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.consumo=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.consumo=cell.stringCellValue
                                        }
                                    }
                                    8->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.periodoCons=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.periodoCons=cell.stringCellValue
                                        }
                                    }
                                    9->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.ubicacion=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.ubicacion=cell.stringCellValue
                                        }
                                    }
                                    10->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.mapsLink=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.mapsLink=cell.stringCellValue
                                        }
                                    }
                                    11->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.latitud=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.latitud=cell.stringCellValue
                                        }
                                    }
                                    12->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.longitud=cell.numericCellValue.toString()
                                        }
                                        else{
                                            cliente.longitud=cell.stringCellValue
                                        }

                                    }
                                    13->{
                                        if(cell.cellType==CellType.NUMERIC){
                                            cliente.lista=cell.numericCellValue.toInt()
                                        }
                                        else{
                                            if(cell.stringCellValue!=""){
                                                cliente.lista=cell.stringCellValue.toInt()
                                            }
                                        }
                                    }
                                }
                            }

                        }
                        var cell=row?.getCell(14)
                        var cont=14
                        while(cell!=null){
                            cell=row?.getCell(cont)
                            println(cont)
                            if(cell==null){
                                break
                            }
                            if(cell?.cellType==CellType.STRING){
                                if(cell?.stringCellValue==""){
                                    break
                                }
                            }
                            else{
                                if(cell.numericCellValue==0.0){
                                    break
                                }
                            }
                            var comentario = Comment("","","","")
                            for(i in 0..3){
                                cell=row.getCell(cont+i)
                                when(i){
                                    0->{if(cell.cellType==CellType.STRING){comentario.fecha=cell.stringCellValue}
                                    else{comentario.fecha=cell.numericCellValue.toString()}}
                                    1->{if(cell.cellType==CellType.STRING){comentario.tipoContacto=cell.stringCellValue}
                                    else{comentario.tipoContacto=cell.numericCellValue.toString()}}
                                    2->{if(cell.cellType==CellType.STRING){comentario.comentario=cell.stringCellValue}
                                    else{comentario.comentario=cell.numericCellValue.toString()}}
                                    3->{if(cell.cellType==CellType.STRING){comentario.estado=cell.stringCellValue}
                                    else{comentario.estado=cell.numericCellValue.toString()}}
                                }
                            }
                            cliente.listaComentarios.add(comentario)
                            cont+=4
                        }
                        listaClientes.add(cliente)
                        rowNumber++
                    }
                }else{
                    Toast.makeText(this, "Seleccione un archivo válido", Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(this, "Seleccione un archivo", Toast.LENGTH_LONG).show()
            }
        }catch (ex: Exception){
            Toast.makeText(this, "Seleccione un archivo válido", Toast.LENGTH_LONG).show()
            ex.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        comp=false
        //coroutineScope.cancel()
    }

    override fun onBackPressed() {
        val segue = Intent(this, ActivityPrin::class.java)
        segue.putExtra("excelSheet", excelSheet)
        segue.putExtra("ruta", ruta)
        startActivity(segue)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        finish()
    }


}