package com.example.crmpovasa

import PreferencesPackage.CRMPovasaApplication
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import data.Cliente
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Locale

class ActivityEditContact : AppCompatActivity() {
    lateinit var editTextNombre : EditText
    lateinit var editTextContacto: EditText
    lateinit var editTextTelefono: EditText
    lateinit var editTextConsumo : EditText
    lateinit var spinnerPeriodoConsumo: Spinner
    lateinit var spinnerLista: Spinner
    lateinit var editTextUbicacion: EditText
    lateinit var textViewLinkMaps: TextView
    lateinit var textViewCoordenadas: TextView
    lateinit var buttonRecharge: FloatingActionButton
    lateinit var progressBar: ProgressBar
    lateinit var buttonGuardarCambios : Button
    lateinit var geocoder : Geocoder
    lateinit var ruta: String
    lateinit var excelSheet: String
    lateinit var cliente : Cliente
    private val location = LocationService()
    var segCont=0
    var coords=""
    var contCords=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)
        editTextNombre = findViewById(R.id.editTextTextEditNombre)
        editTextContacto = findViewById(R.id.editTextTextEditContacto)
        editTextTelefono = findViewById(R.id.editTextEditTelefono)
        editTextConsumo = findViewById(R.id.editTextTextEditConsumo)
        spinnerPeriodoConsumo = findViewById(R.id.spinnerEditPeriodoConsumo)
        spinnerLista = findViewById(R.id.spinnerEditLista)
        editTextUbicacion = findViewById(R.id.editTextTextEditUbicacion)
        textViewLinkMaps = findViewById(R.id.textViewEditLinkMaps)
        textViewCoordenadas = findViewById(R.id.textViewEditCoordenadas)
        buttonRecharge = findViewById(R.id.floatingActionButtonRecordinate)
        progressBar = findViewById(R.id.progressBarEditCliente)
        buttonGuardarCambios = findViewById(R.id.buttonEditarCliente)
        val buttonBack = findViewById<ImageButton>(R.id.imageButtonBackEditCliente)
        val consumo=listOf("Semanal","Quincenal","Mensual")
        val adaptadorConsumo= ArrayAdapter(this,R.layout.spinner_adapter,consumo)
        adaptadorConsumo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriodoConsumo.adapter=adaptadorConsumo
        val lista = listOf("1","2","3")
        val adaptadorLista = ArrayAdapter(this, R.layout.spinner_adapter, lista)
        adaptadorLista.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLista.adapter=adaptadorLista
        ruta = intent.getStringExtra("ruta")!!
        excelSheet = intent.getStringExtra("excelSheet")!!
        geocoder = Geocoder(this, Locale.getDefault())
        cargarDatos()
        buttonRecharge.setOnClickListener {
            recargarCoordenadas()
        }
        buttonBack.setOnClickListener{
            val segue = Intent(this,ActivityClientInfo::class.java)
            segue.putExtra("cliente", cliente)
            segue.putExtra("ruta", ruta)
            segue.putExtra("excelSheet", excelSheet)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(segue)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            finish()
        }
        buttonGuardarCambios.setOnClickListener {
            if(editTextNombre.text.isNotEmpty()){
                updateDB()
                updateClient()
                val segue = Intent(this,ActivityClientInfo::class.java)
                segue.putExtra("cliente", cliente)
                segue.putExtra("ruta", ruta)
                segue.putExtra("excelSheet", excelSheet)
                ActivityCompat.finishAffinity(this)
                startActivity(segue)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                finish()
            }
        }
    }

    private fun updateClient() {
        cliente.nombre=editTextNombre.text.toString()
        cliente.contacto=editTextContacto.text.toString()
        cliente.telefono=editTextTelefono.text.toString()
        cliente.consumo=editTextConsumo.text.toString()
        cliente.periodoCons=spinnerPeriodoConsumo.selectedItem.toString()
        cliente.lista=spinnerLista.selectedItem.toString().toInt()
        cliente.ubicacion=editTextUbicacion.text.toString()
        cliente.mapsLink = textViewLinkMaps.text.toString()
        cliente.latitud=textViewCoordenadas.text.toString().split(",")[0]
        cliente.longitud=textViewCoordenadas.text.toString().split(",")[1]
    }

    private fun updateDB() {
        try {
            progressBar.visibility= View.VISIBLE
            if(CRMPovasaApplication.preferences.getPath()!=null){
                val file = File(CRMPovasaApplication.preferences.getPath())
                if(file.exists()){
                    val workbook : Workbook = WorkbookFactory.create(
                        FileInputStream(
                            CRMPovasaApplication.preferences.getPath())
                    )
                    val sheet: Sheet = workbook.getSheet(excelSheet)
                    val rowNumber = cliente.id
                    var row: Row = sheet.getRow(rowNumber)
                    val cellStyle = workbook.createCellStyle()
                    cellStyle.wrapText=true
                    cellStyle.verticalAlignment= VerticalAlignment.CENTER
                    cellStyle.alignment= HorizontalAlignment.CENTER
                    cellStyle.borderTop = BorderStyle.THIN
                    cellStyle.borderBottom= BorderStyle.THIN
                    cellStyle.borderLeft= BorderStyle.THIN
                    cellStyle.borderRight= BorderStyle.THIN
                    var cell = row.createCell(0)
                    cell.cellStyle=cellStyle
                    //Llenado de datos
                    cell.setCellValue(editTextNombre.text.toString())
                    cell=row.createCell(1)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextContacto.text.toString())
                    cell=row.createCell(2)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextTelefono.text.toString())
                    cell=row.createCell(7)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextConsumo.text.toString())
                    cell=row.createCell(8)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(spinnerPeriodoConsumo.selectedItem.toString())
                    cell=row.createCell(9)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextUbicacion.text.toString())
                    cell=row.createCell(10)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(textViewLinkMaps.text.toString())
                    cell=row.createCell(11)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(textViewCoordenadas.text.toString().split(",")[1])
                    cell=row.createCell(12)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(textViewCoordenadas.text.toString().split(",")[0])
                    cell=row.createCell(13)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(spinnerLista.selectedItem.toString())

                    //Guardado de datos
                    val outputStream = FileOutputStream(CRMPovasaApplication.preferences.getPath())
                    workbook.write(outputStream)
                    outputStream.close()
                    workbook.close()
                }
                else{
                    Toast.makeText(this, "El archivo no existe", Toast.LENGTH_LONG).show()
                }
            }
        }catch (ex: Exception){
            Toast.makeText(this, "Error al editar el cliente", Toast.LENGTH_LONG).show()
            ex.printStackTrace()
        }

    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
        }
    }
    var comp=false

    private fun recargarCoordenadas() {
        fun isLocationPermissionGranted()=
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION
            )== PackageManager.PERMISSION_GRANTED
        comp=true
        val coroutineScope = lifecycleScope
        try{
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

            coroutineScope.launch(Dispatchers.IO){
                while(comp){
                    val result = location.getUserLocation(this@ActivityEditContact)
                    withContext(Dispatchers.Main){
                        segCont++
                        if(segCont>=120){
                            comp=false
                            Toast.makeText(this@ActivityEditContact, "Coordenadas calibradas", Toast.LENGTH_SHORT).show()
                        }
                        if (result == null) {
                            textViewCoordenadas.text = "0,0"
                            if(isLocationPermissionGranted()){
                                Toast.makeText(this@ActivityEditContact, "Error al obtener las coordenadas", Toast.LENGTH_SHORT).show()
                                comp=false
                            }
                            else{
                                Toast.makeText(this@ActivityEditContact, "Es necesario activar los permisos para acceder a la ubicación", Toast.LENGTH_LONG).show()
                                comp=false
                            }
                        } else {
                            if(coords.equals(""+result?.latitude + "," + result?.longitude)){
                                contCords++
                                if(contCords>=10){
                                    comp=false
                                    Toast.makeText(this@ActivityEditContact, "Coordenadas calibradas", Toast.LENGTH_SHORT).show()
                                }
                            }
                            else{
                                contCords=0
                                coords=""+result?.latitude + "," + result?.longitude
                            }
                            textViewCoordenadas.text = "" + result.latitude + "," + result.longitude
                            textViewLinkMaps.text = "https://www.google.com/maps/place/" + textViewCoordenadas.text
                            val adress =
                                geocoder.getFromLocation(result.latitude, result.longitude, 1)?.get(0)
                                    ?.getAddressLine(0).toString()
                            if (adress == null) {
                                editTextUbicacion.setText("Sin domicilio disponible")

                            } else {
                                editTextUbicacion.setText(
                                    geocoder.getFromLocation(
                                        result.latitude,
                                        result.longitude,
                                        1
                                    )?.get(0)?.getAddressLine(0).toString()
                                )
                            }
                        }
                    }
                    Thread.sleep(1000)
                }

            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }
    }
    override fun onPause(){
        super.onPause()
        if(comp){
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            comp=false
        }
    }

    private fun cargarDatos() {
        cliente = intent.getSerializableExtra("cliente") as Cliente
        editTextNombre.setText(cliente.nombre)
        editTextContacto.setText(cliente.contacto)
        editTextTelefono.setText(cliente.telefono)
        editTextConsumo.setText(cliente.consumo)
        when(cliente.periodoCons){
            "Semanal" ->{spinnerPeriodoConsumo.setSelection(0)}
            "Quincenal" ->{spinnerPeriodoConsumo.setSelection(1)}
            "Mensual" ->{spinnerPeriodoConsumo.setSelection(2)}
        }
        when(cliente.lista.toString()){
            "1" ->{spinnerLista.setSelection(0)}
            "2" ->{spinnerLista.setSelection(1)}
            "3" ->{spinnerLista.setSelection(2)}
        }
        editTextUbicacion.setText(cliente.ubicacion)
        textViewLinkMaps.setText(cliente.mapsLink)
        textViewCoordenadas.setText(cliente.latitud+","+cliente.longitud)
    }
}