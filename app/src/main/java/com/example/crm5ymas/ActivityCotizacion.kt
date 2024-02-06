package com.example.crm5ymas

import Adapters.AdapterRecyclerProducts
import PreferencesPackage.CRMPovasaApplication
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
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
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import data.ProductClass
import data.Producto
import data.ProductoWQ
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random


class ActivityCotizacion : AppCompatActivity(), AdapterRecyclerProducts.OnItemClickListener {
    lateinit var listProds : List<ProductoWQ>
    lateinit var compListProducts: List<Producto>
    lateinit var adapterRecycler : AdapterRecyclerProducts
    lateinit var editTextAsesor : EditText
    lateinit var editTextCliente: EditText
    var clientId=0
    lateinit var ruta: String
    lateinit var excelSheet: String
    lateinit var progressBar: ProgressBar
    var exit= false
    var otrosCont=-1
    var clientList=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cotizacion)
        clientList = intent.getIntExtra("lista", 0)
        val spinnerProductos = findViewById<Spinner>(R.id.spinnerProductos)
        editTextAsesor = findViewById(R.id.editTextTextNombreAsesor)
        editTextCliente = findViewById(R.id.editTextTextNombreCliente)
        ruta=intent.getStringExtra("ruta")!!
        excelSheet=intent.getStringExtra("excelSheet")!!
        clientId=intent.getIntExtra("id", -1)
        val clientNombre = intent.getStringExtra("nombre")
        progressBar=findViewById(R.id.progressBarCotizador)
        val editTextFiltro = findViewById<EditText>(R.id.editTextTextFiltroProducto)
        val editTextCantidad = findViewById<EditText>(R.id.editTextNumberCantidadProductos)
        val textViewSubtotal = findViewById<TextView>(R.id.textViewSubtotal)
        val buttonPlusItem = findViewById<FloatingActionButton>(R.id.floatingActionButtonNuevoProducto)
        val recyclerViewProducts = findViewById<RecyclerView>(R.id.recyclerViewProductos)
        val buttonCotizar = findViewById<Button>(R.id.buttonCotizar)
        val buttonBack = findViewById<ImageButton>(R.id.imageButtonBackCotiz)
        val arrayDesc = ArrayList<String>()
        val arrayProds = ArrayList<ProductoWQ>()
        compListProducts = ProductClass().getProducts()
        if(clientNombre!=null){
            editTextCliente.setText(clientNombre+" ("+ruta+")")
        }
        readExcelPrices()
        for(i in compListProducts){
            arrayProds.add(ProductoWQ(i.id, i.sku, i.desc, i.peso, i.precio, 0))
            arrayDesc.add(i.desc)
        }
        listProds = List(0){ProductoWQ(0,"","",0,0.0,0)}
        adapterRecycler= AdapterRecyclerProducts(this, listProds )
        adapterRecycler.onItemClickListener=this
        recyclerViewProducts.apply {
            layoutManager= LinearLayoutManager(this@ActivityCotizacion)
            adapter=adapterRecycler
        }
        val adaptador = ArrayAdapter(this,R.layout.spinner_adapter,arrayDesc)
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerProductos.adapter=adaptador
        editTextFiltro.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                arrayDesc.clear()
                arrayProds.clear()
                for(i in compListProducts){
                    if(i.desc.lowercase().contains(editTextFiltro.text.toString().lowercase().trim())){
                        arrayProds.add(ProductoWQ(i.id, i.sku, i.desc, i.peso, i.precio, 0))
                    }
                }
                for(i in arrayProds){
                    arrayDesc.add(i.desc)
                }
                adaptador.notifyDataSetChanged()
            }
        })
        spinnerProductos.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                editTextCantidad.setText("")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                editTextCantidad.setText("")
            }

        }
        buttonBack.setOnClickListener {
            if(ruta==""){
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                finish()
            }else{
                val segue = Intent(this, ActivityPrin::class.java)
                segue.putExtra("excelSheet", excelSheet)
                segue.putExtra("ruta", ruta)
                startActivity(segue)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
                finish()
            }
        }
        buttonPlusItem.setOnClickListener {
            if(editTextCantidad.text.isNotEmpty()&&editTextCantidad.text.toString().toInt()>0){
                var newProduct=arrayProds[spinnerProductos.selectedItemPosition]
                if(spinnerProductos.selectedItem!="Otros"){
                    var comp = false
                    for(i in listProds){
                        if(i.id==newProduct.id){
                            comp=true
                            break
                        }
                    }
                    if(comp){
                        Toast.makeText(this, "Este producto ya fue añadido", Toast.LENGTH_LONG).show()
                    }
                    else{
                        newProduct.cantidad=editTextCantidad.text.toString().toInt()
                        listProds=listProds.plus(newProduct)
                        adapterRecycler.updateList(listProds)
                        editTextCantidad.setText("")
                        editTextFiltro.setText("")
                    }
                }
                else{
                    showInputDialogString(this){
                        it.let{sku->
                            if(sku!=""){
                                newProduct.sku=sku
                                showInputDialogDesc(this){
                                    it.let{desc->
                                        if(desc!=""){
                                            newProduct.desc=desc
                                            showInputDialogInt(this){
                                                newProduct.precio=it.toDouble()
                                                newProduct.id=newProduct.id+otrosCont
                                                otrosCont++
                                                newProduct.cantidad=editTextCantidad.text.toString().toInt()
                                                listProds=listProds.plus(newProduct)
                                                adapterRecycler.updateList(listProds)
                                                editTextCantidad.setText("")
                                                editTextFiltro.setText("")
                                            }
                                        }
                                    }
                                }

                            }
                            else{
                                Toast.makeText(this, "Es necesario agregar un SKU", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
            else{
                Toast.makeText(this, "Es necesario ingresar una cantidad", Toast.LENGTH_LONG).show()
            }
        }
        editTextCantidad.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                if(editTextCantidad.text.isNotEmpty()){
                    if(spinnerProductos.selectedItem!="Otros"){
                        if(arrayProds[spinnerProductos.selectedItemPosition].peso==25){
                            textViewSubtotal.setText(roundToTwoDecimals(arrayProds[spinnerProductos.selectedItemPosition].precio*editTextCantidad.text.toString().toInt()*25).toString())
                        }
                        else{
                            textViewSubtotal.setText(roundToTwoDecimals(arrayProds[spinnerProductos.selectedItemPosition].precio*editTextCantidad.text.toString().toInt()).toString())
                        }
                    }
                }
                else{
                    textViewSubtotal.setText("")
                }
            }

        })
        buttonCotizar.setOnClickListener {
            if(editTextAsesor.text.isNotEmpty()){
                if(editTextCliente.text.isNotEmpty()){
                    if(listProds.size>0){
                        if(clientId!=-1){
                            colorClient()
                        }
                        val pdfCot = crearPDF()
                        guardarPdfGaleria(this, convertirPDFImagen(pdfCot))
                    }
                    else{
                        Toast.makeText(this, "Es necesario ingresar al menos un producto", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this, "Es necesario ingresar un cliente", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "Es necesario ingresar un asesor", Toast.LENGTH_LONG).show()
            }

        }
    }


    private fun readExcelPrices() {
        try {
            val listaPrecios = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/LISTA DE PRECIOS 5 Y MAS.xlsx").toString())
            if(listaPrecios.exists()){
                val workbook : Workbook = WorkbookFactory.create(
                    FileInputStream(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/Rutas5&M/LISTA DE PRECIOS 5 Y MAS.xlsx"))
                )
                val sheet: Sheet = workbook.getSheet("Lista")
                val rowNumber = 2
                var columnNumber = clientList+2
                var row: Row = sheet.getRow(rowNumber)
                var cell: Cell? =row.getCell(columnNumber)
                for(i in compListProducts){
                    if(i.grupo!=-1){
                        var tempRow = 0
                        while(true){
                            row=sheet.getRow(tempRow)
                            cell=row.getCell(0)
                            if(cell?.cellType==CellType.NUMERIC){
                                if(cell?.numericCellValue!=null){
                                    if(cell?.numericCellValue!!.toInt()==i.grupo){
                                        break
                                    }
                                }
                            }
                            tempRow++
                            if(tempRow==80){
                                println("chin")
                                break
                            }
                        }
                        row=sheet.getRow(tempRow)
                        cell=row.getCell(columnNumber)
                        var precio=0.0
                        if(cell?.cellType==CellType.STRING){
                            if(cell?.stringCellValue!=null){
                                precio=cell.stringCellValue.toDouble()
                            }
                        }
                        if(cell?.cellType==CellType.NUMERIC){
                            if(cell?.numericCellValue!=null){
                                precio=cell.numericCellValue
                            }
                        }
                        i.precio=precio
                    }
                }
                workbook.close()
            }
            else{
                Toast.makeText(this, "Seleccione un archivo válido", Toast.LENGTH_LONG).show()
            }
        }catch (ex: java.lang.Exception){
            ex.printStackTrace()
            Toast.makeText(this, "Error al actualizar los precios", Toast.LENGTH_LONG).show()
        }
    }

    fun crearPDF(): ByteArray{
        val outputStream = ByteArrayOutputStream()
        try {
            progressBar.visibility=View.VISIBLE
            val pdfWriter = PdfWriter(outputStream)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A3)
            val page = pdfDocument.addNewPage()
            val canvas = PdfCanvas(page)
            canvas.saveState()
            canvas.rectangle(0.0, 0.0, page.getPageSize().getWidth().toDouble(), page.getPageSize().getHeight().toDouble())
            canvas.setFillColor(DeviceRgb(255, 255, 255)) // Blanco
            canvas.fill()
            canvas.restoreState()
            val tamLetFill=22f
            val colorLetFill=DeviceRgb(27, 36, 231)
            var imageResId= R.mipmap.marco
            var bitmap= BitmapFactory.decodeResource(this.resources, imageResId)
            var salida = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
            var arregloBytes=salida.toByteArray()
            val image3 = Image(ImageDataFactory.create(arregloBytes))
            image3.scaleAbsolute(page.pageSize.width+45f, page.pageSize.height+45f)
            image3.setFixedPosition(-22f, -22f)
            document.add(image3)
            imageResId= R.mipmap.cincoymaslogoempresa
            bitmap= BitmapFactory.decodeResource(this.resources, imageResId)
            salida = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
            arregloBytes=salida.toByteArray()
            val image = Image(ImageDataFactory.create(arregloBytes))
            image.scaleAbsolute(120f, 120f)
            image.setFixedPosition(30f, 1030f)
            document.add(image)
            val comer= Paragraph("COMERCIALIZADORA 5 Y MAS")
            comer.setFontColor(DeviceRgb(0,0,0))
            comer.setFontSize(22f)
            val font = PdfFontFactory.createFont(StandardFonts.HELVETICA)
            comer.setFont(font)
            comer.setFixedPosition((page.pageSize.width/2)-150f, 1090f, 600f)
            document.add(comer)
            val titulo= Paragraph("FORMATO DE PEDIDO")
            titulo.setFontColor(DeviceRgb(0,0,0))
            titulo.setFontSize(18f)
            val font2 = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLDOBLIQUE)
            titulo.setFont(font2)
            titulo.setFixedPosition((page.pageSize.width/2)-95f, 1060f, 600f)
            document.add(titulo)
            val cliente=Paragraph("CLIENTE:     _______________________________________________")
            cliente.setFontColor(DeviceRgb(0,0,0))
            cliente.setFontSize(16f)
            cliente.setFont(font2)
            cliente.setFixedPosition((page.pageSize.width/2)-250f, 1010f, 1000f)
            document.add(cliente)
            val clienteV=Paragraph(editTextCliente.text.toString())
            clienteV.setFontColor(DeviceRgb(0,0,0))
            clienteV.setFontSize(16f)
            clienteV.setFont(font)
            clienteV.setFixedPosition((page.pageSize.width/2)-editTextCliente.text.toString().length*1.5f, 1015f, 1000f)
            document.add(clienteV)
            val asesor=Paragraph("ASESOR:     _______________________________________________")
            asesor.setFontColor(DeviceRgb(0,0,0))
            asesor.setFontSize(16f)
            asesor.setFont(font2)
            asesor.setFixedPosition((page.pageSize.width/2)-250f, 980f, 1000f)
            document.add(asesor)
            val asesorV=Paragraph(editTextAsesor.text.toString())
            asesorV.setFontColor(DeviceRgb(0,0,0))
            asesorV.setFontSize(16f)
            asesorV.setFont(font)
            asesorV.setFixedPosition((page.pageSize.width/2)-editTextAsesor.text.toString().length*1.5f, 985f, 1000f)
            document.add(asesorV)
            val fecha=Paragraph("FECHA:        _______________________________________________")
            fecha.setFontColor(DeviceRgb(0,0,0))
            fecha.setFontSize(16f)
            fecha.setFont(font2)
            fecha.setFixedPosition((page.pageSize.width/2)-250f, 950f, 1000f)
            document.add(fecha)
            val fechaV=Paragraph(getFecha())
            fechaV.setFontColor(DeviceRgb(0,0,0))
            fechaV.setFontSize(16f)
            fechaV.setFont(font)
            fechaV.setFixedPosition((page.pageSize.width/2)-getFecha().length*1f, 955f, 1000f)
            document.add(fechaV)
            val indBult=Paragraph("BULTOS")
            indBult.setFontColor(DeviceRgb(0,0,0))
            indBult.setFontSize(14f)
            indBult.setFont(font2)
            indBult.setFixedPosition(20f, 920f, 60f)
            document.add(indBult)
            val indPeso=Paragraph("PESO (KGS)")
            indPeso.setFontColor(DeviceRgb(0,0,0))
            indPeso.setFontSize(14f)
            indPeso.setFont(font2)
            indPeso.setFixedPosition(90f, 920f, 100f)
            document.add(indPeso)
            val indSku=Paragraph("SKU")
            indSku.setFontColor(DeviceRgb(0,0,0))
            indSku.setFontSize(14f)
            indSku.setFont(font2)
            indSku.setFixedPosition(210f, 920f, 100f)
            document.add(indSku)
            val indDesc=Paragraph("DESCRIPCIÓN")
            indDesc.setFontColor(DeviceRgb(0,0,0))
            indDesc.setFontSize(14f)
            indDesc.setFont(font2)
            indDesc.setFixedPosition((page.pageSize.width/2)-30f, 920f, 100f)
            document.add(indDesc)
            val precioUnit=Paragraph("PRECIO UNIT.")
            precioUnit.setFontColor(DeviceRgb(0,0,0))
            precioUnit.setFontSize(14f)
            precioUnit.setFont(font2)
            precioUnit.setFixedPosition((page.pageSize.width/2)+190f, 920f, 100f)
            document.add(precioUnit)
            val subtotal=Paragraph("SUBTOTAL")
            subtotal.setFontColor(DeviceRgb(0,0,0))
            subtotal.setFontSize(14f)
            subtotal.setFont(font2)
            subtotal.setFixedPosition((page.pageSize.width/2)+310f, 920f, 100f)
            document.add(subtotal)
            var cantidad=0
            var kilos = 0
            var total= 0.0
            for(i in 0..listProds.size-1){
                val bultos=Paragraph(listProds[i].cantidad.toString())
                bultos.setFontColor(DeviceRgb(0,0,0))
                bultos.setFontSize(14f)
                bultos.setFont(font)
                bultos.setFixedPosition(45f-listProds[i].cantidad.toString().length*5f, 890f-(i*20), 100f)
                document.add(bultos)
                val peso=Paragraph((listProds[i].cantidad*listProds[i].peso).toString())
                peso.setFontColor(DeviceRgb(0,0,0))
                peso.setFontSize(14f)
                peso.setFont(font)
                peso.setFixedPosition(135f-(listProds[i].cantidad*listProds[i].peso).toString().length*5f, 890f-(i*20), 1000f)
                document.add(peso)
                val sku=Paragraph(listProds[i].sku)
                sku.setFontColor(DeviceRgb(0,0,0))
                sku.setFontSize(14f)
                sku.setFont(font)
                sku.setFixedPosition(185f-listProds[i].sku.length*0.5f, 890f-(i*20), 1000f)
                document.add(sku)
                val desc=Paragraph(listProds[i].desc)
                desc.setFontColor(DeviceRgb(0,0,0))
                desc.setFontSize(14f)
                desc.setFont(font)
                desc.setFixedPosition((page.pageSize.width/2)-listProds[i].desc.length*2.5f, 890f-(i*20), 1000f)
                document.add(desc)
                val sign1=Paragraph("$")
                sign1.setFontColor(DeviceRgb(0,0,0))
                sign1.setFontSize(14f)
                sign1.setFont(font)
                sign1.setFixedPosition((page.pageSize.width/2)+195f, 890f-(i*20), 300f)
                document.add(sign1)
                lateinit var indPrec: Paragraph
                if(listProds[i].precio.toString().split(".")[1].length<2){
                    indPrec=Paragraph(listProds[i].precio.toString()+"0")
                }
                else{
                    indPrec=Paragraph(listProds[i].precio.toString())
                }
                indPrec.setFontColor(DeviceRgb(0,0,0))
                indPrec.setFontSize(14f)
                indPrec.setFont(font)
                indPrec.setFixedPosition((page.pageSize.width/2)+255f-listProds[i].precio.toString().length*4.5f, 890f-(i*20), 1000f)
                document.add(indPrec)
                val sign2=Paragraph("$")
                sign2.setFontColor(DeviceRgb(0,0,0))
                sign2.setFontSize(14f)
                sign2.setFont(font)
                sign2.setFixedPosition((page.pageSize.width/2)+300f, 890f-(i*20), 1000f)
                document.add(sign2)
                lateinit var calcPrec: Paragraph
                var texto=""
                texto = roundToTwoDecimals(listProds[i].precio*listProds[i].cantidad).toString()
                if(texto.split(".")[1].length<2){
                    if(!formatNumber(roundToTwoDecimals(listProds[i].precio*listProds[i].cantidad)).contains(".")){
                        calcPrec=Paragraph(formatNumber(roundToTwoDecimals(listProds[i].precio*listProds[i].cantidad))+".00")
                    }
                    else{
                        calcPrec=Paragraph(formatNumber(roundToTwoDecimals(listProds[i].precio*listProds[i].cantidad))+"0")
                    }
                }
                else{
                    if(!formatNumber(roundToTwoDecimals(listProds[i].precio*listProds[i].cantidad)).contains(".")){
                        calcPrec=Paragraph(formatNumber(roundToTwoDecimals(listProds[i].precio*listProds[i].cantidad))+".00")
                    }
                    else{
                        calcPrec=Paragraph(formatNumber(roundToTwoDecimals(listProds[i].precio*listProds[i].cantidad)))
                    }
                }
                calcPrec.setFontColor(DeviceRgb(0,0,0))
                calcPrec.setFontSize(14f)
                calcPrec.setFont(font)
                calcPrec.setFixedPosition((page.pageSize.width/2)+370f-texto.length*4.5f, 890f-(i*20), 1000f)
                document.add(calcPrec)
                cantidad+=listProds[i].cantidad
                kilos+=listProds[i].peso*listProds[i].cantidad
                if(listProds[i].peso==25){
                    total+=listProds[i].peso*listProds[i].cantidad*listProds[i].precio
                }
                else{
                    total+=listProds[i].cantidad*listProds[i].precio
                }

            }
            val nBultos=Paragraph(cantidad.toString())
            nBultos.setFontColor(DeviceRgb(0,0,0))
            nBultos.setFontSize(14f)
            nBultos.setFont(font)
            nBultos.setFixedPosition(45f-cantidad.toString().length*4f, 150f, 1000f)
            document.add(nBultos)
            val messBultos=Paragraph("BULTOS")
            messBultos.setFontColor(DeviceRgb(0,0,0))
            messBultos.setFontSize(14f)
            messBultos.setFont(font)
            messBultos.setFixedPosition(20f, 130f, 1000f)
            document.add(messBultos)
            val pesoT=Paragraph(kilos.toString())
            pesoT.setFontColor(DeviceRgb(0,0,0))
            pesoT.setFontSize(14f)
            pesoT.setFont(font)
            pesoT.setFixedPosition(135f-kilos.toString().length*4f, 150f, 1000f)
            document.add(pesoT)
            val messPeso=Paragraph("KILOS")
            messPeso.setFontColor(DeviceRgb(0,0,0))
            messPeso.setFontSize(14f)
            messPeso.setFont(font)
            messPeso.setFixedPosition(115f, 130f, 1000f)
            document.add(messPeso)
            val messTotal=Paragraph("TOTAL:")
            messTotal.setFontColor(DeviceRgb(0,0,0))
            messTotal.setFontSize(14f)
            messTotal.setFont(font)
            messTotal.setFixedPosition(650f, 130f, 1000f)
            document.add(messTotal)
            val signT=Paragraph("$")
            signT.setFontColor(DeviceRgb(0,0,0))
            signT.setFontSize(14f)
            signT.setFont(font)
            signT.setFixedPosition(720f, 130f, 1000f)
            document.add(signT)
            lateinit var totalV: Paragraph
            if(formatNumber(roundToTwoDecimals(total)).contains(".")){
                if(formatNumber(roundToTwoDecimals(total)).split(".")[1].length<2){
                    totalV=Paragraph(formatNumber(roundToTwoDecimals(total))+"0")
                }
                else{
                    totalV=Paragraph(formatNumber(roundToTwoDecimals(total)))
                }
            }else{
                totalV=Paragraph(formatNumber(roundToTwoDecimals(total))+".00")
            }
            totalV.setFontColor(DeviceRgb(0,0,0))
            totalV.setFontSize(14f)
            totalV.setFont(font)
            totalV.setFixedPosition(780f-formatNumber(roundToTwoDecimals(total)).length*4f, 130f, 1000f)
            document.add(totalV)
            val agrad=Paragraph("¡GRACIAS POR SU PREFERENCIA!")
            agrad.setFontColor(DeviceRgb(0,0,0))
            agrad.setFontSize(14f)
            agrad.setFont(font)
            agrad.setFixedPosition((page.pageSize.width/2)-115f, 100f, 1000f)
            document.add(agrad)
            val vigencia=Paragraph("COTIZACIÓN VIGENTE POR 7 DÍAS HÁBILES*")
            vigencia.setFontColor(DeviceRgb(0,0,0))
            vigencia.setFontSize(14f)
            vigencia.setFont(font)
            vigencia.setFixedPosition((page.pageSize.width/2)-150f, 80f, 1000f)
            document.add(vigencia)
            val cambios=Paragraph("PRECIOS SUJETOS A CAMBIOS*")
            cambios.setFontColor(DeviceRgb(0,0,0))
            cambios.setFontSize(14f)
            cambios.setFont(font)
            cambios.setFixedPosition((page.pageSize.width/2)-110f, 60f, 1000f)
            document.add(cambios)
            val contacto=Paragraph("TELÉFONO CELULAR DE CONTACTO: 4433251242                                   TELÉFONO FIJO DE CONTACTO: 4438226491")
            contacto.setFontColor(DeviceRgb(0,0,0))
            contacto.setFontSize(14f)
            contacto.setFont(font)
            contacto.setFixedPosition((page.pageSize.width/2)-380f, 30f, 1000f)
            document.add(contacto)
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
            Toast.makeText(this, "Cotización generada con éxito", Toast.LENGTH_LONG).show()
            exit=true
        }catch (ex: Exception){
            ex.printStackTrace()
            Toast.makeText(this, "Error al generar la imagen", Toast.LENGTH_LONG).show()
            progressBar.visibility=View.INVISIBLE
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
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File.createTempFile(generateHexKey(), ".pdf", path)
        val os = FileOutputStream(file)
        os.write(byteArray)
        os.close()
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

    fun roundToTwoDecimals(number: Double): Double {
        val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
        return df.format(number).toDouble()
    }
    fun formatNumber(number: Double): String{
        val numberFormat = NumberFormat.getNumberInstance(Locale.US)
        return numberFormat.format(number)
    }
    override fun onDeleteItemClick(producto: ProductoWQ) {
        listProds= listProds.minus(producto)
        adapterRecycler.updateList(listProds)
    }

    private fun showInputDialogInt(context: Context, callback: (String) ->Unit){
        val input = EditText(context)
        input.inputType= InputType.TYPE_CLASS_PHONE
        input.hint = "Precio"
        val filterArray = arrayOf(digitFilter)
        input.filters= filterArray
        val dialog = AlertDialog.Builder(context)
            .setTitle("Precio")
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
                if(source.toString()=="."){
                    return source!!
                }
                else{
                    try {
                        source.toString().toInt()
                        return source!!
                    }catch (ex: Exception){}
                }
            }

            // Si el texto ingresado contiene más de un dígito o es vacío, no lo aceptamos
            return ""
        }

    }
    private fun showInputDialogString(context: Context, callback: (String) ->Unit){
        val input = EditText(context)
        input.hint = "SKU"
        val dialog = AlertDialog.Builder(context)
            .setTitle("SKU")
            .setView(input)
            .setPositiveButton("Aceptar"){_, _ ->
                val inputText = input.text.toString()
                callback(inputText)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }
    private fun showInputDialogDesc(context: Context, callback: (String) ->Unit){
        val input = EditText(context)
        input.hint = "Descripción"
        val dialog = AlertDialog.Builder(context)
            .setTitle("Descripción")
            .setView(input)
            .setPositiveButton("Aceptar"){_, _ ->
                val inputText = input.text.toString()
                callback(inputText)
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    private fun colorClient() {
        try {
            progressBar.visibility=View.VISIBLE
            if(CRMPovasaApplication.preferences.getPath()!=null) {
                val file = File(CRMPovasaApplication.preferences.getPath())
                if(file.exists()){
                    val workbook : Workbook = WorkbookFactory.create(
                        FileInputStream(
                            CRMPovasaApplication.preferences.getPath())
                    )
                    val sheet: Sheet = workbook.getSheet(excelSheet)
                    val rowNumber = clientId
                    var columnNumber = 0
                    var row: Row = sheet.getRow(rowNumber)
                    var cell: Cell? =row.getCell(columnNumber)
                    val cellStyle = workbook.createCellStyle()
                    cellStyle.wrapText=true
                    cellStyle.verticalAlignment= VerticalAlignment.CENTER
                    cellStyle.alignment= HorizontalAlignment.CENTER
                    cellStyle.borderTop = BorderStyle.THIN
                    cellStyle.borderBottom= BorderStyle.THIN
                    cellStyle.borderLeft= BorderStyle.THIN
                    cellStyle.borderRight= BorderStyle.THIN
                    cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    while(columnNumber<14){
                        if(cell?.cellType== CellType.STRING) {
                            if (cell?.stringCellValue != null) {
                                cell = row.getCell(columnNumber)
                                if (cell?.stringCellValue == null) {
                                    cell = row.createCell(columnNumber)
                                }
                            } else {
                                cell = row.createCell(columnNumber)
                            }
                            cell.cellStyle=cellStyle
                        }
                        else{
                            if(cell?.cellType== CellType.NUMERIC){
                                if(cell?.numericCellValue!=0.0){
                                    cell = row.getCell(columnNumber)
                                    if(cell?.numericCellValue==null){
                                        cell = row.createCell(columnNumber)
                                    }
                                }
                                else {
                                    cell = row.createCell(columnNumber)
                                }
                                cell.cellStyle=cellStyle
                            }
                            else{
                                if(cell?.cellType==CellType.BLANK){
                                    cell?.cellStyle=cellStyle
                                }
                                println(cell?.cellType.toString())
                            }
                        }
                        columnNumber++
                    }

                    val outputStream = FileOutputStream(CRMPovasaApplication.preferences.getPath())
                    workbook.write(outputStream)
                    outputStream.close()
                    workbook.close()
                }
                else{
                    Toast.makeText(this, "Seleccione un archivo válido", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "Seleccione un archivo válido", Toast.LENGTH_LONG).show()
            }
        }catch (ex: java.lang.Exception){
            ex.printStackTrace()
            Toast.makeText(this, "Error al cambiar color de celdas", Toast.LENGTH_LONG).show()
        }
    }
    override fun onResume() {
        super.onResume()
        progressBar.visibility=View.INVISIBLE
    }
}