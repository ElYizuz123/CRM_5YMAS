package com.example.crmpovasa

import Adapters.AdapterNuevoCliente
import PreferencesPackage.CRMPovasaApplication
import PreferencesPackage.CRMPovasaApplication.Companion.preferences
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Locale


class ActivityNuevoCliente : AppCompatActivity() {
    lateinit var listaMarcas: ArrayList<String>
    lateinit var listaProductos: ArrayList<String>
    lateinit var editTextUbicacion: EditText
    lateinit var textViewCoordenadas: TextView
    lateinit var textViewLinkMaps: TextView
    lateinit var editTextNombreCliente : EditText
    lateinit var editTextContacto: EditText
    lateinit var editTextTelefono: EditText
    lateinit var editTextComentarios: EditText
    lateinit var editTextSeguimiento: EditText
    lateinit var editTextConsumo: EditText
    lateinit var spinnerPeriodoConsumo: Spinner
    lateinit var spinnerLista: Spinner
    lateinit var geocoder : Geocoder
    lateinit var progressBar: ProgressBar
    lateinit var buttonSaveCords: ImageButton
    var rowNumber=0
    var excelSheet=""
    var coords=""
    var contCords=0
    var segCont=0
    var readCords=false
    private val location = LocationService()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_cliente)
        //Declaración de variables
        buttonSaveCords = findViewById(R.id.imageButtonSaveCords)
        editTextNombreCliente = findViewById(R.id.editTextTextEditNombre)
        editTextContacto = findViewById(R.id.editTextTextEditContacto)
        editTextTelefono = findViewById(R.id.editTextEditTelefono)
        editTextComentarios = findViewById(R.id.editTextTextMultiLineComentarios)
        editTextSeguimiento = findViewById(R.id.editTextTextMultiLineSeguimiento)
        progressBar = findViewById(R.id.progressBarNuevoCliente)
        val editTextMarca = findViewById<EditText>(R.id.editTextTextEditNuevaMarca)
        val buttonPlusMarca = findViewById<FloatingActionButton>(R.id.floatingActionButtonNuevaMarca)
        val recyclerViewMarcas = findViewById<RecyclerView>(R.id.recyclerViewMarcasManeja)
        val editTextProductoManeja =  findViewById<EditText>(R.id.editTextTextNuevoProductoNC)
        val buttonPlusProducto = findViewById<FloatingActionButton>(R.id.floatingActionButtonNuevoProductoManeja)
        val recyclerViewProductos = findViewById<RecyclerView>(R.id.recyclerViewProductosManeja)
        val buttonBack = findViewById<ImageButton>(R.id.imageButtonBackNuevoCliente)
        val ruta = intent.getStringExtra("ruta")
        editTextConsumo = findViewById(R.id.editTextTextEditConsumo)
        spinnerPeriodoConsumo = findViewById(R.id.spinnerEditPeriodoConsumo)
        spinnerLista = findViewById(R.id.spinnerEditLista)
        editTextUbicacion = findViewById(R.id.editTextTextEditUbicacion)
        textViewLinkMaps = findViewById(R.id.textViewEditLinkMaps)
        textViewCoordenadas = findViewById(R.id.textViewEditCoordenadas)
        val buttonRegistrarCliente = findViewById<Button>(R.id.buttonEditarCliente)
        val consumo=listOf("Semanal","Quincenal","Mensual")
        val adaptadorConsumo= ArrayAdapter(this,R.layout.spinner_adapter,consumo)
        adaptadorConsumo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriodoConsumo.adapter=adaptadorConsumo
        val lista = listOf("1","2","3")
        val adaptadorLista = ArrayAdapter(this, R.layout.spinner_adapter, lista)
        adaptadorLista.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLista.adapter=adaptadorLista
        listaMarcas =ArrayList()
        listaProductos =ArrayList()
        geocoder = Geocoder(this, Locale.getDefault())
        excelSheet= intent.getStringExtra("excelSheet")!!
        if(preferences.getCords()!=""){
            readCords=true
        }

        //Declaración de funcionres
        obtenerUbicacion()
        buttonBack.setOnClickListener{
            val segue = Intent(this, ActivityPrin::class.java)
            segue.putExtra("excelSheet", excelSheet)
            segue.putExtra("ruta", ruta)
            startActivity(segue)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            finish()
        }
        buttonSaveCords.setOnClickListener {
            if(editTextUbicacion.text.isNotEmpty()&&textViewCoordenadas.text.isNotEmpty()){
                if(preferences.getCords()==""){
                    preferences.saveLocation(textViewCoordenadas.text.toString(),editTextUbicacion.text.toString())
                    readCords=true
                    Toast.makeText(this, "Coordenadas fijadas", Toast.LENGTH_SHORT).show()
                }
                else{
                    preferences.saveLocation("", "")
                    Toast.makeText(this, "Coordenadas borradas", Toast.LENGTH_SHORT).show()
                    readCords=false
                }
            }
        }
        textViewLinkMaps.setOnClickListener {
            val clipboardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData= ClipData.newPlainText("Link maps", textViewLinkMaps.text)
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(this, "Texto copiado al portapapeles", Toast.LENGTH_SHORT).show()
        }
        editTextMarca.setOnEditorActionListener{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                buttonPlusMarca.performClick()
                true
            } else {
                false
            }
        }
        editTextProductoManeja.setOnEditorActionListener{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                buttonPlusProducto.performClick()
                true
            } else {
                false
            }
        }
        buttonPlusMarca.setOnClickListener {
            if(editTextMarca.text.isNotEmpty()){
                listaMarcas.add(editTextMarca.text.toString())
                recyclerViewMarcas.layoutManager = LinearLayoutManager(this)
                recyclerViewMarcas.adapter = AdapterNuevoCliente(this, listaMarcas)
                editTextMarca.text.clear()
            }
        }
        buttonPlusProducto.setOnClickListener {
            if(editTextProductoManeja.text.isNotEmpty()){
                listaProductos.add(editTextProductoManeja.text.toString())
                recyclerViewProductos.layoutManager = LinearLayoutManager(this)
                recyclerViewProductos.adapter = AdapterNuevoCliente(this, listaProductos)
                editTextProductoManeja.text.clear()
            }
        }
        buttonRegistrarCliente.setOnClickListener {
            if(editTextNombreCliente.text.isNotEmpty()){
                updateDB()
                preferences.saveLocation("","")
                val segue = Intent(this, ActivityQuiereComprar::class.java)
                segue.putExtra("ruta", ruta)
                segue.putExtra("excelSheet", excelSheet)
                segue.putExtra("id", rowNumber)
                segue.putExtra("nombre", editTextNombreCliente.text.toString())
                segue.putExtra("lista", spinnerLista.selectedItem.toString().toInt())
                startActivity(segue)
            }
            else{
                Toast.makeText(this, "Es necesario ingresar el nombre del cliente", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun updateDB() {
        try {
            progressBar.visibility= View.VISIBLE
            if(preferences.getPath()!=null){
                val file = File(preferences.getPath())
                if(file.exists()){
                    val workbook : Workbook = WorkbookFactory.create(FileInputStream(preferences.getPath()))
                    val sheet: Sheet = workbook.getSheet(excelSheet)
                    rowNumber = 0
                    var row: Row? = sheet.getRow(rowNumber)
                    var cell: Cell?=row?.getCell(0)
                    if(cell?.stringCellValue!=null){
                        while(cell?.stringCellValue!=""){
                            rowNumber++
                            row=sheet?.getRow(rowNumber)
                            cell = row?.getCell(0)
                            if(row==null){
                                row=sheet.createRow(rowNumber)
                            }
                            if(cell?.stringCellValue==null){
                                cell=row?.createCell(rowNumber)
                            }
                        }
                    }
                    else{
                        cell=row?.createCell(rowNumber)
                    }
                    val cellStyle = workbook.createCellStyle()
                    cellStyle.wrapText=true
                    cellStyle.verticalAlignment=VerticalAlignment.CENTER
                    cellStyle.alignment=HorizontalAlignment.CENTER
                    cellStyle.borderTop = BorderStyle.THIN
                    cellStyle.borderBottom= BorderStyle.THIN
                    cellStyle.borderLeft= BorderStyle.THIN
                    cellStyle.borderRight= BorderStyle.THIN
                    cellStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex())
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    row=sheet.createRow(rowNumber)
                    cell = row.createCell(0)
                    cell.cellStyle=cellStyle
                    //Llenado de datos
                    cell.setCellValue(editTextNombreCliente.text.toString())
                    cell=row.createCell(1)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextContacto.text.toString())
                    cell=row.createCell(2)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextTelefono.text.toString())
                    cell=row.createCell(3)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextComentarios.text.toString())
                    cell=row.createCell(4)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextSeguimiento.text.toString())
                    cell=row.createCell(5)
                    cell.cellStyle=cellStyle
                    var texto=""
                    for(i in listaMarcas){
                        texto+=("• "+i+"\n")
                    }
                    cell.setCellValue(texto)
                    cell=row.createCell(6)
                    cell.cellStyle=cellStyle
                    texto=""
                    for(i in listaProductos){
                        texto+=("• "+i+"\n")
                    }
                    cell.setCellValue(texto)
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
                    cell.setCellValue(textViewCoordenadas.text.toString().split(",")[0])
                    cell=row.createCell(12)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(textViewCoordenadas.text.toString().split(",")[1])
                    cell=row.createCell(13)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(spinnerLista.selectedItem.toString())

                    //Guardado de datos
                    val outputStream = FileOutputStream(preferences.getPath())
                    workbook.write(outputStream)
                    outputStream.close()
                    workbook.close()
                }
                else{
                    Toast.makeText(this, "El archivo no existe", Toast.LENGTH_LONG).show()
                }
            }
        }catch (ex: Exception){
            Toast.makeText(this, "Inserte un archivo válido", Toast.LENGTH_LONG).show()
            ex.printStackTrace()
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
    private var comp=true
    private val coroutineScope = lifecycleScope
    @SuppressLint("MissingPermission")
    private fun obtenerUbicacion(){
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
                var compReadenCords=false
                while(comp){
                    if(readCords){
                        withContext(Dispatchers.Main){
                            if(!compReadenCords){
                                textViewCoordenadas.setText(preferences.getCords())
                                editTextUbicacion.setText(preferences.getAdress())
                                textViewLinkMaps.text = "https://www.google.com/maps/place/" + textViewCoordenadas.text
                                compReadenCords=true
                            }
                        }
                    }
                    else{
                        val result = location.getUserLocation(this@ActivityNuevoCliente)
                        withContext(Dispatchers.Main){
                            segCont++
                            if(segCont>=120){
                                comp=false
                                Toast.makeText(this@ActivityNuevoCliente, "Coordenadas calibradas", Toast.LENGTH_SHORT).show()
                            }
                            if (result == null) {
                                textViewCoordenadas.text = "0,0"
                                if(isLocationPermissionGranted()){
                                    Toast.makeText(this@ActivityNuevoCliente, "Error al obtener las coordenadas", Toast.LENGTH_SHORT).show()
                                    comp=false
                                }
                                else{
                                    Toast.makeText(this@ActivityNuevoCliente, "Es necesario activar los permisos para acceder a la ubicación", Toast.LENGTH_LONG).show()
                                    comp=false
                                }
                            } else {
                                if(coords.equals(""+result?.latitude + "," + result?.longitude)){
                                    contCords++
                                    if(contCords>=10){
                                        comp=false
                                        Toast.makeText(this@ActivityNuevoCliente, "Coordenadas calibradas", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                else{
                                    contCords=0
                                    coords=""+result?.latitude + "," + result?.longitude
                                }
                                textViewCoordenadas.text = "" + result.latitude + "," + result.longitude
                                textViewLinkMaps.text = "https://www.google.com/maps/place/" + textViewCoordenadas.text
                                val adress =
                                    try{
                                        geocoder.getFromLocation(result.latitude, result.longitude, 1)?.get(0)
                                            ?.getAddressLine(0).toString()
                                    }catch (ex: Exception){
                                        ex.printStackTrace()
                                    }

                                if (adress == null) {
                                    editTextUbicacion.setText("Sin domicilio disponible")

                                } else {
                                    try {
                                        editTextUbicacion.setText(
                                            geocoder.getFromLocation(
                                                result.latitude,
                                                result.longitude,
                                                1
                                            )?.get(0)?.getAddressLine(0).toString()
                                        )
                                    }catch (ex: Exception){
                                        ex.printStackTrace()
                                    }

                                }
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
    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        comp=false
        //coroutineScope.cancel()
    }

    override fun onResume() {
        super.onResume()
        progressBar.visibility=View.INVISIBLE
        if(comp==false){
            comp=true
            obtenerUbicacion()
        }
    }







}