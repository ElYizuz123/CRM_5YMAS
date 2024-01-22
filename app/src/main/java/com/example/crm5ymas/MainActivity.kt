package com.example.crm5ymas

import PreferencesPackage.CRMPovasaApplication.Companion.preferences
import android.Manifest
import android.app.ActivityManager
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import data.Distancia
import data.LocationService
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.text.DecimalFormat
import java.util.Calendar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    companion object{
        const val REQUEST_CODE_LOCATION =0
        const val REQUEST_CODE_READ_STORAGE=1
    }
    val absoluteRute = ArrayList<String>()
    val absoluteFile = ArrayList<String>()
    lateinit var lista: MutableList<String>
    lateinit var listaRutas: MutableList<String>
    lateinit var spinnerRuta: Spinner
    lateinit var adaptador: ArrayAdapter<String>
    lateinit var adapterRutas: ArrayAdapter<String>
    lateinit var spinnerRutasAbs : Spinner
    val ruta1 = mutableListOf("Zinapécuaro", "Maravatio", "Ciudad Hidalgo", "Zitácuaro")
    val ruta2= mutableListOf("Quiroga", "Zacapu", "Zamora", "Jiquilpan", "Sahuayo", "La piedad")
    val ruta3= mutableListOf("Queretaro", "El Pueblito", "Tequisquiapan", "Acámbaro")
    val ruta4= mutableListOf("Uruapan", "Nueva Italia", "Apatzingán")
    val ruta5= mutableListOf("Moroleón", "Salvatierra", "Celaya", "Cortazar", "Salamanca", "Irapuato", "Silao", "León")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val editTextFiltro = findViewById<EditText>(R.id.editTextFiltroRuta)
        spinnerRuta =  findViewById(R.id.spinnerRuta)
        val buttonIngresar = findViewById<Button>(R.id.buttonIngresar)
        val buttonFileSelecter = findViewById<FloatingActionButton>(R.id.floatingActionButtonFileSelecter)
        val buttonCotizar = findViewById<ImageButton>(R.id.imageButtonCotizar)
        val buttonInitTracker = findViewById<ImageButton>(R.id.imageButtonStartTracking)
        val buttonDeleteTrackerList = findViewById<ImageButton>(R.id.imageButtonDelteTrackingList)
        val buttonShareFile = findViewById<ImageButton>(R.id.imageButtonShareFile)
        val filtroAbsoluteRute= findViewById<EditText>(R.id.editTextFiltroAbsoluteRute)
        val buttonTrackingReport = findViewById<ImageButton>(R.id.imageButtonTrackingReport)
        spinnerRutasAbs = findViewById(R.id.spinnerRutaAb)
        lista = ruta1.sorted().toMutableList()
        listaRutas= MutableList(1){""}
        adaptador=ArrayAdapter(this,R.layout.spinner_adapter,lista )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRuta.adapter=adaptador
        adapterRutas= ArrayAdapter(this, R.layout.spinner_adapter, listaRutas)
        adapterRutas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRutasAbs.adapter=adapterRutas
        setSpinner()
        askForPermission()
        verificarCarpeta()
        rutasExistentes()
        if(isMyServiceRunning(this, LocationService::class.java)){
            buttonInitTracker.setImageResource(R.mipmap.distancetracker)
        }
        else{
            buttonInitTracker.setImageResource(R.mipmap.canceldistancetracker)
        }
        spinnerRutasAbs.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                setSpinner()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                setSpinner()
            }

        }
        buttonShareFile.setOnClickListener {
            try{
                val fileXLSX = File(preferences.getPath())
                val intent = Intent(Intent.ACTION_SEND)
                val uriArchivo = FileProvider.getUriForFile(this, "com.example.crmpovasa", fileXLSX)
                intent.setDataAndType(uriArchivo, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.putExtra(Intent.EXTRA_STREAM, uriArchivo)
                startActivity(Intent.createChooser(intent, "Compartir archivo XLSX"))
            }catch(ex: Exception){
                ex.printStackTrace()
            }

        }
        buttonTrackingReport.setOnClickListener {
            showInputDialogTrackingReport(this){
                writeReport(it)
            }
        }
        buttonDeleteTrackerList.setOnClickListener {
            if(isMyServiceRunning(this, LocationService::class.java)){
                Toast.makeText(this, "Es necesario detener el proceso de seguimiento", Toast.LENGTH_LONG).show()
            }
            else{
                mostrarDialogoConfirmacion()
            }
        }
        buttonInitTracker.setOnClickListener{
            try{
                //If tracking is on, we turn it of
                if(isMyServiceRunning(this, LocationService::class.java)){
                    buttonInitTracker.setImageResource(R.mipmap.canceldistancetracker)
                    Intent(applicationContext, LocationService::class.java).apply {
                        action = LocationService.ACTION_STOP
                        startService(this)
                    }
                    Toast.makeText(this, "Proceso de seguimiento finalizado", Toast.LENGTH_SHORT).show()
                }
                //If tracking is off, we turn it on
                else{
                    buttonInitTracker.setImageResource(R.mipmap.distancetracker)
                    Intent(applicationContext, LocationService::class.java).apply {
                        action = LocationService.ACTION_START
                        startService(this)
                    }
                    Toast.makeText(this, "Proceso de seguimiento iniciado", Toast.LENGTH_SHORT).show()
                }
            }catch (ex: Exception){
                ex.printStackTrace()
            }


        }
        buttonFileSelecter.setOnClickListener {
            setSpinner()
            askForPermission()
            verificarCarpeta()
            rutasExistentes()
            restartApp()
            //getResult.launch("application/xlsx")
        }

        filtroAbsoluteRute.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                listaRutas.clear()
                for(i in absoluteFile){
                    if(i.lowercase().trim().contains(filtroAbsoluteRute.text.toString().lowercase().trim())){
                        listaRutas.add(i)
                    }
                }
                adapterRutas.clear()
                listaRutas=listaRutas.sorted().toMutableList()
                adapterRutas.addAll(listaRutas)
                adapterRutas.notifyDataSetChanged()
                if(spinnerRutasAbs.selectedItemPosition!=-1){
                    setSpinner()
                }
                else{
                    adaptador.clear()
                    adaptador.add("")
                }

            }

        })
        editTextFiltro.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                lista.clear()
                for(i in absoluteRute){
                    if(i.lowercase().trim().contains(editTextFiltro.text.toString().lowercase().trim())){
                        lista.add(i)
                    }
                }
                adaptador.clear()
                lista=lista.sorted().toMutableList()
                adaptador.addAll(lista)
                adaptador.notifyDataSetChanged()
            }

        })
        editTextFiltro.setText("m")
        editTextFiltro.setText("")
        buttonIngresar.setOnClickListener {
            if(spinnerRuta.selectedItem!=null&&spinnerRuta.selectedItem!=""){
                val excelSheet = sheetSelecter(spinnerRuta.selectedItem.toString())
                val segue = Intent(this, ActivityPrin::class.java)
                segue.putExtra("excelSheet", excelSheet)
                segue.putExtra("ruta", spinnerRuta.selectedItem.toString())
                startActivity(segue)
            }
            else{
                Toast.makeText(this, "Seleccione una ruta válida", Toast.LENGTH_LONG).show()
            }
        }
        buttonCotizar.setOnClickListener{
            showInputDialog(this){
                val segue = Intent(this, ActivityCotizacion::class.java)
                segue.putExtra("excelSheet", "")
                segue.putExtra("ruta", "")
                segue.putExtra("lista", it.toInt())
                startActivity(segue)
            }
        }

    }

    private fun writeReport(asesor: String) {
        if(isMyServiceRunning(this, LocationService::class.java)){
            Toast.makeText(this, "Es necesario detener el seguimiento", Toast.LENGTH_LONG).show()
        }
        else{
            var trackingList = readList("distancias.dat", this)
            if(trackingList!=null){
                guardarPdfGaleria(this, convertirPDFImagen(crearPDF(asesor, trackingList)))
            }
        }
    }

    private fun verificarCarpeta() {
        try{
            val folderExist = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M").toString())
            if (!folderExist.exists()){
                try{
                    folderExist.mkdirs()
                    Toast.makeText(this, "Carpeta de rutas creada: Rutas5&M en Descargas", Toast.LENGTH_LONG).show()
                }catch (ex: Exception){
                    ex.printStackTrace()
                }
            }
        }catch (ex:Exception){
            ex.printStackTrace()
        }

    }

    private fun rutasExistentes() {
        try{
            if(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M").toString()).listFiles().isNotEmpty()){
                listaRutas.clear()
                val rutaQuer = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta QUERETARO.xlsx").toString())
                if(rutaQuer.exists()){
                    listaRutas.add("Querétaro")
                }
                val rutaZamor = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta ZAMORA.xlsx").toString())
                if(rutaZamor.exists()){
                    listaRutas.add("Zamora")
                }
                val rutaUruapan = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta URUAPAN.xlsx").toString())
                if(rutaUruapan.exists()){
                    listaRutas.add("Uruapan")
                }
                val rutaGuajajuato = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta GUANAJUATO.xlsx").toString())
                if(rutaGuajajuato.exists()){
                    listaRutas.add("Guanajuato")
                }
                val rutaCiudadHidalgo = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta CIUDAD HIDALGO.xlsx").toString())
                if(rutaCiudadHidalgo.exists()){
                    listaRutas.add("Ciudad Hidalgo")
                }
                absoluteFile.clear()
                for(i in listaRutas){
                    absoluteFile.add(i)
                }
            }
        }catch (ex: Exception){
            ex.printStackTrace()
        }

//        adapterRutas.clear()
//        adapterRutas.addAll(listaRutas)
//        adapterRutas.notifyDataSetChanged()
    }

    private fun setSpinner() {
        when(spinnerRutasAbs.selectedItem){
            "Querétaro" ->{
                preferences.savePath(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta QUERETARO.xlsx").toString())
                lista = ruta3.sorted().toMutableList()
                adaptador.clear()
                lista=ruta3.sorted().toMutableList()
                adaptador.addAll(lista)
                adaptador.notifyDataSetChanged()
                absoluteRute.clear()
                for(i in ruta3){
                    absoluteRute.add(i)
                }
            }
            "Zamora" ->{
                preferences.savePath(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta ZAMORA.xlsx").toString())
                lista = ruta2.sorted().toMutableList()
                adaptador.clear()
                lista=ruta2.sorted().toMutableList()
                adaptador.addAll(lista)
                adaptador.notifyDataSetChanged()
                absoluteRute.clear()
                for(i in ruta2){
                    absoluteRute.add(i)
                }
            }
            "Uruapan" ->{
                preferences.savePath(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta URUAPAN.xlsx").toString())
                lista = ruta4.sorted().toMutableList()
                adaptador.clear()
                lista=ruta4.sorted().toMutableList()
                adaptador.addAll(lista)
                adaptador.notifyDataSetChanged()
                absoluteRute.clear()
                for(i in ruta4){
                    absoluteRute.add(i)
                }
            }
            "Guanajuato" ->{
                preferences.savePath(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta GUANAJUATO.xlsx").toString())
                lista = ruta5.sorted().toMutableList()
                adaptador.clear()
                lista=ruta5.sorted().toMutableList()
                adaptador.addAll(lista)
                adaptador.notifyDataSetChanged()
                absoluteRute.clear()
                for(i in ruta5){
                    absoluteRute.add(i)
                }
            }
            "Ciudad Hidalgo" ->{
                preferences.savePath(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/Ruta CIUDAD HIDALGO.xlsx").toString())
                adaptador.clear()
                lista=ruta1.sorted().toMutableList()
                adaptador.addAll(lista)
                adaptador.notifyDataSetChanged()
                absoluteRute.clear()
                for(i in ruta1){
                    absoluteRute.add(i)
                }
            }
            else ->{
                preferences.savePath("")
                absoluteRute.clear()
                absoluteRute.add("")
            }
        }
    }

    private fun sheetSelecter(ruta: String): String {
        var a=""
        when (ruta){
            "Zinapécuaro" -> {a="Zinapecuaro"}
            "Maravatio" ->{a="Maravatio"}
            "Ciudad Hidalgo"->{a="Ciudad Hidalgo"}
            "Zitácuaro" ->{a="Zitacuaro"}
            "Pátzcuaro" ->{a="Patzcuaro"}
            "Quiroga" ->{a="Quiroga"}
            "Zacapu" ->{a="Zacapu"}
            "Zamora"->{a="Zamora"}
            "Jiquilpan"->{a="Jiquilpan"}
            "Sahuayo"->{a="Sahuayo"}
            "La piedad"->{a="La_Piedad"}
            "Queretaro"->{a="Queretaro"}
            "El Pueblito"->{a="El Pueblito"}
            "Tequisquiapan"->{a="Tequisquiapan"}
            "Acámbaro"->{a="Acambaro"}
            "Uruapan"->{a="Uruapan"}
            "Nueva Italia"->{a="Nueva Italia"}
            "Apatzingán"->{a="Apatzingan"}
            "Moroleón"->{a="Moroleon"}
            "Salvatierra"->{a="Salvatierra"}
            "Celaya"->{a="Celaya"}
            "Cortazar"->{a="Cortazar"}
            "Salamanca"->{a="Salamanca"}
            "Irapuato"->{a="Irapuato"}
            "Silao"->{a="Silao"}
            "León"->{a="Leon"}
            else ->{a="Hoja1"}
        }
        return a
    }


    private fun showInputDialog(context: Context, callback: (String) ->Unit){
        val input = EditText(context)
        input.inputType=InputType.TYPE_CLASS_NUMBER
        input.hint = "Número de lista"
        val filterArray = arrayOf(InputFilter.LengthFilter(1), digitFilter)
        input.filters= filterArray
        val dialog = AlertDialog.Builder(context)
            .setTitle("Lista a la que pertenece")
            .setView(input)
            .setPositiveButton("Aceptar"){_, _ ->
                val inputText = input.text.toString()
                callback(inputText)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    //Name of Asesor
    private fun showInputDialogTrackingReport(context: Context, callback: (String) ->Unit){
        val input = EditText(context)
        input.inputType=InputType.TYPE_CLASS_TEXT
        input.hint = "Nombre del asesor"
        val dialog = AlertDialog.Builder(context)
            .setTitle("Nombre del asesor")
            .setView(input)
            .setPositiveButton("Aceptar"){_, _ ->
                val inputText = input.text.toString()
                callback(inputText)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }
    val digitFilter = object :InputFilter {
        override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            p3: Spanned?,
            p4: Int,
            p5: Int
        ): CharSequence {
            if (source.toString().length==1) {
                try{
                    if(source.toString().toInt() in 1..3){
                        return source!!
                    }
                }
                catch (ex: Exception){
                    ex.printStackTrace()
                }
            }

            // Si el texto ingresado contiene más de un dígito o es vacío, no lo aceptamos
            return ""
        }

    }

    private fun isFilesPermissionGranted()=
        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE
    )== PackageManager.PERMISSION_GRANTED

    val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.POST_NOTIFICATIONS
    )
    private fun askForPermission() {
        var notGrantedPermissions=ArrayList<String>()
        for(i in permissions){
            if( ContextCompat.checkSelfPermission(this, i)!=PackageManager.PERMISSION_GRANTED){
                notGrantedPermissions.add(i)
            }
        }
        if (notGrantedPermissions.isNotEmpty()) {
            val requestCode = 1
            ActivityCompat.requestPermissions(this, notGrantedPermissions.toTypedArray(), requestCode)
        }
        //This is for android 11 and above and is for the permission to access to all files
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            if(!Environment.isExternalStorageManager()){
                askForPermissionAllowAccessToData()
            }
        }
    }
    // This function allow the usser to activate the permission for access to all data
    private fun askForPermissionAllowAccessToData() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Permiso de acceso a todos los archivos")
        builder.setMessage("Para permitir la lectura y escritura de datos, es necesario permitir este acceso a la aplicación.")

        builder.setPositiveButton("Ir a configuración") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.setData(Uri.parse(String.format("package:%s",applicationContext.packageName)))
                startActivityIfNeeded(intent, 101)

            } catch (ex: Exception){
                val intent = Intent()
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                startActivityIfNeeded(intent, 101)
            }

        }

        builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    //Method that allows to restart the app
    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private fun isMyServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager?.getRunningServices(Int.MAX_VALUE) ?: emptyList()) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }
    fun readList(nombreArchivo: String, context: Context): List<Distancia>? {
        var objetos: List<Distancia>? = null
        try {
            val fileInputStream = context.openFileInput(nombreArchivo)
            val objectInputStream = ObjectInputStream(fileInputStream)
            objetos = objectInputStream.readObject() as List<Distancia>
            objectInputStream.close()
            fileInputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return objetos
    }
    fun crearPDF(asesor: String, trackingList: List<Distancia>): ByteArray{
        val outputStream = ByteArrayOutputStream()
        try {
            val pdfWriter = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A3)
            val page = pdfDocument.addNewPage()
            val canvas = PdfCanvas(page)
            val formatNum = DecimalFormat("#.###")
            canvas.saveState()
            canvas.rectangle(0.0, 0.0, page.getPageSize().getWidth().toDouble(), page.getPageSize().getHeight().toDouble())
            canvas.setFillColor(DeviceRgb(255, 255, 255)) // Blanco
            canvas.fill()
            canvas.restoreState()
            val tamLetFill=22f
            val colorLetFill= DeviceRgb(27, 36, 231)
            var imageResId= R.mipmap.marco
            var bitmap= BitmapFactory.decodeResource(this.resources, imageResId)
            var salida = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
            var arregloBytes=salida.toByteArray()
            val image3 = com.itextpdf.layout.element.Image(ImageDataFactory.create(arregloBytes))
            image3.scaleAbsolute(page.pageSize.width+45f, page.pageSize.height+45f)
            image3.setFixedPosition(-22f, -22f)
            document.add(image3)
            imageResId= R.mipmap.comercializadoralogo
            bitmap= BitmapFactory.decodeResource(this.resources, imageResId)
            salida = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
            arregloBytes=salida.toByteArray()
            val image = com.itextpdf.layout.element.Image(ImageDataFactory.create(arregloBytes))
            image.scaleAbsolute(120f, 120f)
            image.setFixedPosition(30f, 1040f)
            document.add(image)
            imageResId= R.mipmap.povasalogo
            bitmap= BitmapFactory.decodeResource(this.resources, imageResId)
            salida = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
            arregloBytes=salida.toByteArray()
            val image2 = com.itextpdf.layout.element.Image(ImageDataFactory.create(arregloBytes))
            image2.scaleAbsolute(160f, 60f)
            image2.setFixedPosition(650f, 1060f)
            document.add(image2)
            val comer= Paragraph("COMERCIALIZADORA LUIGUI")
            comer.setFontColor(DeviceRgb(0,0,0))
            comer.setFontSize(22f)
            val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)
            comer.setFont(font)
            comer.setFixedPosition((page.pageSize.width/2)-150f, 1090f, 600f)
            document.add(comer)
            val titulo= Paragraph("REPORTE DE KILOMETRAJE")
            titulo.setFontColor(DeviceRgb(0,0,0))
            titulo.setFontSize(18f)
            val font2 = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE)
            titulo.setFont(font2)
            titulo.setFixedPosition((page.pageSize.width/2)-105f, 1060f, 600f)
            document.add(titulo)
            val cliente= Paragraph("ASESOR:     _______________________________________________")
            cliente.setFontColor(DeviceRgb(0,0,0))
            cliente.setFontSize(18f)
            cliente.setFont(font2)
            cliente.setFixedPosition((page.pageSize.width/2)-250f, 1020f, 1000f)
            document.add(cliente)
            val clienteV= Paragraph(asesor)
            clienteV.setFontColor(DeviceRgb(0,0,0))
            clienteV.setFontSize(18f)
            clienteV.setFont(font)
            clienteV.setFixedPosition((page.pageSize.width/2)-asesor.length*1.5f, 1025f, 1000f)
            document.add(clienteV)
            val rute= Paragraph("RUTA:          _______________________________________________")
            rute.setFontColor(DeviceRgb(0,0,0))
            rute.setFontSize(18f)
            rute.setFont(font2)
            rute.setFixedPosition((page.pageSize.width/2)-250f, 985f, 1000f)
            document.add(rute)
            val ruteName= Paragraph(spinnerRutasAbs.selectedItem.toString())
            ruteName.setFontColor(DeviceRgb(0,0,0))
            ruteName.setFontSize(18f)
            ruteName.setFont(font)
            ruteName.setFixedPosition((page.pageSize.width/2)-spinnerRutasAbs.selectedItem.toString().length*1.5f, 990f, 1000f)
            document.add(ruteName)
            val fecha= Paragraph("FECHA:        _______________________________________________")
            fecha.setFontColor(DeviceRgb(0,0,0))
            fecha.setFontSize(18f)
            fecha.setFont(font2)
            fecha.setFixedPosition((page.pageSize.width/2)-250f, 950f, 1000f)
            document.add(fecha)
            val fechaV= Paragraph(getFecha())
            fechaV.setFontColor(DeviceRgb(0,0,0))
            fechaV.setFontSize(18f)
            fechaV.setFont(font)
            fechaV.setFixedPosition((page.pageSize.width/2)-getFecha().length*1f, 955f, 1000f)
            document.add(fechaV)
            val indSku= Paragraph("Kilometros recorridos")
            indSku.setFontColor(DeviceRgb(0,0,0))
            indSku.setFontSize(16f)
            indSku.setFont(font2)
            indSku.setFixedPosition(80f, 910f, 200f)
            document.add(indSku)
            val indDesc= Paragraph("Fecha")
            indDesc.setFontColor(DeviceRgb(0,0,0))
            indDesc.setFontSize(16f)
            indDesc.setFont(font2)
            indDesc.setFixedPosition((page.pageSize.width/2)-20f, 910f, 100f)
            document.add(indDesc)
            val precioUnit= Paragraph("Horas trabajadas")
            precioUnit.setFontColor(DeviceRgb(0,0,0))
            precioUnit.setFontSize(16f)
            precioUnit.setFont(font2)
            precioUnit.setFixedPosition((page.pageSize.width/2)+250f, 910f, 200f)
            document.add(precioUnit)
            var totalDistance=0.0
            var totalWorkingHours = 0.0
            for(i in 0..trackingList.size-1){
                val kilometros= Paragraph(formatNum.format(trackingList[i].distancia).toString()+" Km")
                kilometros.setFontColor(DeviceRgb(0,0,0))
                kilometros.setFontSize(16f)
                kilometros.setFont(font)
                kilometros.setFixedPosition(140f-trackingList[i].distancia.toString().length*3f, 880f-(i*20), 300f)
                document.add(kilometros)
                val fecha= Paragraph(trackingList[i].fecha)
                fecha.setFontColor(DeviceRgb(0,0,0))
                fecha.setFontSize(16f)
                fecha.setFont(font)
                fecha.setFixedPosition((page.pageSize.width/2)-trackingList[i].fecha.length*2.5f, 880f-(i*20), 1000f)
                document.add(fecha)
                val workingHours= Paragraph(formatNum.format(trackingList[i].horasTrabajo).toString())
                workingHours.setFontColor(DeviceRgb(0,0,0))
                workingHours.setFontSize(16f)
                workingHours.setFont(font)
                workingHours.setFixedPosition((page.pageSize.width/2)+300f, 880f-(i*20), 300f)
                document.add(workingHours)
                totalDistance+=trackingList[i].distancia
                totalWorkingHours+=trackingList[i].horasTrabajo
            }
            val totalKilometers= Paragraph("Total de kilometros: ")
            totalKilometers.setFontColor(DeviceRgb(0,0,0))
            totalKilometers.setFontSize(16f)
            totalKilometers.setFont(font2)
            totalKilometers.setFixedPosition(90f, 150f, 1000f)
            document.add(totalKilometers)
            val nKilometros= Paragraph(formatNum.format(totalDistance).toString())
            nKilometros.setFontColor(DeviceRgb(0,0,0))
            nKilometros.setFontSize(16f)
            nKilometros.setFont(font)
            nKilometros.setFixedPosition(280f-formatNum.format(totalDistance).toString().length*4f, 150f, 1000f)
            document.add(nKilometros)
            val messBultos= Paragraph("Km")
            messBultos.setFontColor(DeviceRgb(0,0,0))
            messBultos.setFontSize(16f)
            messBultos.setFont(font)
            messBultos.setFixedPosition(310f, 150f, 1000f)
            document.add(messBultos)
            val totalHoras= Paragraph("Total de horas trabajadas: ")
            totalHoras.setFontColor(DeviceRgb(0,0,0))
            totalHoras.setFontSize(16f)
            totalHoras.setFont(font2)
            totalHoras.setFixedPosition(480f, 150f, 1000f)
            document.add(totalHoras)
            val pesoT= Paragraph(formatNum.format(totalWorkingHours).toString())
            pesoT.setFontColor(DeviceRgb(0,0,0))
            pesoT.setFontSize(16f)
            pesoT.setFont(font)
            pesoT.setFixedPosition(720f-formatNum.format(totalWorkingHours).toString().length*4f, 150f, 1000f)
            document.add(pesoT)
            val messPeso= Paragraph("Horas")
            messPeso.setFontColor(DeviceRgb(0,0,0))
            messPeso.setFontSize(16f)
            messPeso.setFont(font)
            messPeso.setFixedPosition(750f, 150f, 1000f)
            document.add(messPeso)
            //Fin del documento
            document.close()
            pdfDocument.close()
            val byteArray =outputStream.toByteArray()
            outputStream.close()
            return byteArray
        }catch (ex: Exception){
            ex.printStackTrace()
            Toast.makeText(this, "Error al generar la imagen", Toast.LENGTH_LONG).show()
        }
        return ByteArray(0)
    }
    private fun getFecha(): String {
        val date = Calendar.getInstance()
        val day = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH)+1
        val year = date.get(Calendar.YEAR)
        return "$day/$month/$year"
    }
    fun guardarPdfGaleria(context: Context, byteArray: ByteArray?){
        try {
            val contentResovler: ContentResolver = context.contentResolver
            val imageFileName = "${generateHexKey()}.jpeg"
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            val imageUri: Uri? = contentResovler.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.let {uri->
                val outputStream = contentResovler.openOutputStream(uri)
                outputStream?.use {stream->
                    stream.write(byteArray)
                }
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "image/*")
                startActivity(intent)
            }
            Toast.makeText(this, "Reporte generado con éxito", Toast.LENGTH_LONG).show()
        }catch (ex: Exception){
            ex.printStackTrace()
            Toast.makeText(this, "Error al generar la imagen", Toast.LENGTH_LONG).show()
        }

    }

    fun generateHexKey(): String {
        val allowedChars = "0123456789ABCDEF"
        val random = Random(System.currentTimeMillis())
        val sb = StringBuilder(10)

        repeat(10) {
            val randomIndex = random.nextInt(0, allowedChars.length)
            sb.append(allowedChars[randomIndex])
        }

        return sb.toString()
    }
    fun convertirPDFImagen(byteArray: ByteArray):ByteArray?{
        val tempFile = File.createTempFile("temp", ".pdf")
        tempFile.deleteOnExit()
        val fileOutputStream = FileOutputStream(tempFile)
        fileOutputStream.write(byteArray)
        fileOutputStream.close()
        val parcelFileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(parcelFileDescriptor)
        val paginaPdf=pdfRenderer.openPage(0)
        val bitmap = Bitmap.createBitmap(paginaPdf.width, paginaPdf.height, Bitmap.Config.ARGB_8888)
        paginaPdf.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val imageByteArray = outputStream.toByteArray()
        paginaPdf.close()
        pdfRenderer.close()
        return imageByteArray
    }

    private fun mostrarDialogoConfirmacion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Esta acción borrara la lista de kilometrajes")
        builder.setMessage("¿Estás seguro de continuar?")

        builder.setPositiveButton("Confirmar") { dialogInterface: DialogInterface, _: Int ->
            var listaVacia = emptyList<Distancia>()
            preferences.saveDistance(0.0f, "", 0.0f)
            saveList(listaVacia, "distancias.dat", this)
            Toast.makeText(this, "Lista de kilometrajes eliminada", Toast.LENGTH_SHORT).show()
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, _: Int ->

            dialogInterface.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
    fun saveList(objetos: List<Distancia>, nombreArchivo: String, context: Context) {
        try {
            val fileOutputStream = context.openFileOutput(nombreArchivo, Context.MODE_PRIVATE)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            objectOutputStream.writeObject(objetos)
            objectOutputStream.close()
            fileOutputStream.close()
            println("Guardado correctamente")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}