package com.example.crm5ymas

import PreferencesPackage.CRMPovasaApplication
import PreferencesPackage.CRMPovasaApplication.Companion.preferences
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception
import java.util.Calendar

class ActivityRazonNoCompra : AppCompatActivity() {
    lateinit var excelSheet: String
    var clientId=0
    lateinit var editTextRazonNoPedido: EditText
    lateinit var editTextStatus: EditText
    lateinit var spinnerMedioContacto: Spinner
    lateinit var ruta: String
    lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_razon_no_compra)
        ruta = intent.getStringExtra("ruta")!!
        excelSheet = intent.getStringExtra("excelSheet")!!
        clientId = intent.getIntExtra("id", 0)
        editTextRazonNoPedido = findViewById(R.id.editTextTextMultiLineRazonNoPedido)
        editTextStatus = findViewById(R.id.editTextTextMultiLineStatus)
        spinnerMedioContacto = findViewById(R.id.spinnerMedioContacto)
        progressBar = findViewById(R.id.progressBarRazonNoCompra)
        val buttonBack = findViewById<ImageButton>(R.id.imageButtonBackRazNoComp)
        val buttonRegistrarRazon = findViewById<Button>(R.id.buttonRegistrarRazon)
        val lista = listOf("CORREO", "LLAMADA", "MENSAJE DE TEXTO", "VISITA", "WHATSAPP")
        val adaptador= ArrayAdapter(this,R.layout.spinner_adapter,lista )
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMedioContacto.adapter=adaptador
        buttonBack.setOnClickListener{
            val segue = Intent(this, ActivityPrin::class.java)
            segue.putExtra("excelSheet", excelSheet)
            segue.putExtra("ruta", ruta)
            startActivity(segue)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
            finish()
        }
        buttonRegistrarRazon.setOnClickListener {
            if(editTextRazonNoPedido.text.isNotEmpty()){
                if(editTextStatus.text.isNotEmpty()){
                    if(spinnerMedioContacto.selectedItemPosition!=-1){
                        progressBar.visibility= View.VISIBLE
                        addComment()
                    }
                    else{
                        Toast.makeText(this, "Es necesario seleccionar un medio de contacto", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(this, "Es necesario añadir un status", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "Es necesario añadir una razón", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun addComment() {
        try {
            progressBar.visibility=View.VISIBLE
            if(preferences.getPath()!=null) {
                val file = File(CRMPovasaApplication.preferences.getPath())
                if(file.exists()){
                    val workbook : Workbook = WorkbookFactory.create(FileInputStream(preferences.getPath()))
                    val sheet: Sheet = workbook.getSheet(excelSheet)
                    val rowNumber = clientId
                    var columnNumber = 14
                    var row: Row = sheet.getRow(rowNumber)
                    var cell: Cell? =row.getCell(columnNumber)
                    if(cell?.cellType==CellType.STRING) {
                        if (cell?.stringCellValue != null) {

                            while (cell?.stringCellValue != "") {
                                columnNumber += 4
                                cell = row.getCell(columnNumber)
                                if (cell?.stringCellValue == null) {
                                    cell = row.createCell(columnNumber)
                                }
                            }
                        } else {
                            cell = row.createCell(columnNumber)
                        }
                    }
                    else{
                        if(cell?.cellType==CellType.NUMERIC){
                            if(cell?.numericCellValue!=0.0){
                                while(cell?.numericCellValue!=0.0){
                                    columnNumber+=4
                                    cell = row.getCell(columnNumber)
                                    if(cell?.numericCellValue==null){
                                        cell = row.createCell(columnNumber)
                                    }
                                }
                            }
                            else {
                                cell = row.createCell(columnNumber)
                            }
                        }
                        else{
                            println(cell?.cellType.toString())
                        }
                    }
                    cell=row.createCell(columnNumber)
                    val cellStyle = workbook.createCellStyle()
                    cellStyle.wrapText=true
                    cellStyle.verticalAlignment= VerticalAlignment.CENTER
                    cellStyle.alignment= HorizontalAlignment.CENTER
                    cellStyle.borderTop = BorderStyle.THIN
                    cellStyle.borderBottom= BorderStyle.THIN
                    cellStyle.borderLeft= BorderStyle.THIN
                    cellStyle.borderRight= BorderStyle.THIN
                    cell.cellStyle=cellStyle
                    cell.setCellValue(getFecha())
                    cell=row.createCell(columnNumber+1)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(spinnerMedioContacto.selectedItem.toString())
                    cell=row.createCell(columnNumber+2)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextRazonNoPedido.text.toString())
                    cell=row.createCell(columnNumber+3)
                    cell.cellStyle=cellStyle
                    cell.setCellValue(editTextStatus.text.toString())
                    val outputStream = FileOutputStream(preferences.getPath())
                    workbook.write(outputStream)
                    outputStream.close()
                    workbook.close()
                    Toast.makeText(this, "Comentario añadido", Toast.LENGTH_LONG).show()
                    val segue = Intent(this, ActivityPrin::class.java)
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    segue.putExtra("ruta", ruta)
                    segue.putExtra("excelSheet", excelSheet)
                    startActivity(segue)
                }
                else{
                    Toast.makeText(this, "Seleccione un archivo válido", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "Seleccione un archivo válido", Toast.LENGTH_LONG).show()
            }
        }catch (ex: Exception){
            ex.printStackTrace()
            Toast.makeText(this, "Error al añadir comentario", Toast.LENGTH_LONG).show()
        }
    }
    private fun getFecha(): String {
        val date = Calendar.getInstance()
        val day = date.get(Calendar.DAY_OF_MONTH)
        val month = date.get(Calendar.MONTH)+1
        val year = date.get(Calendar.YEAR)
        return "$day/$month/$year"
    }

    override fun onResume() {
        progressBar.visibility=View.INVISIBLE
        super.onResume()
    }
}