package Adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.crmpovasa.ActivityClientInfo
import com.example.crmpovasa.ActivityPrin
import com.example.crmpovasa.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import data.Cliente

class AdapterRecyclerClientes(val ctx: Context, var clientes:List<Cliente>, var ruta: String, var excelSheet: String) : RecyclerView.Adapter<AdapterRecyclerClientes.ClientesViewHolder>(){
    fun updateList(newList:List<Cliente>){
        val clienteDiff = ClientesDiffUtil(clientes, newList)
        val result = DiffUtil.calculateDiff(clienteDiff)
        clientes=newList
        result.dispatchUpdatesTo(this)
    }

    inner class ClientesViewHolder(itemView: View): ViewHolder(itemView){
        fun bind(cliente: Cliente){
            val textViewNombre=itemView.findViewById<TextView>(R.id.textViewNombreCliente)
            val textViewContacto=itemView.findViewById<TextView>(R.id.textViewContactoCliente)
            val textViewNumeroTel=itemView.findViewById<TextView>(R.id.textViewNumeroCliente)
            val textViewDomicilio= itemView.findViewById<TextView>(R.id.textViewDomicilioCliente)
            val buttonVerCliente = itemView.findViewById<FloatingActionButton>(R.id.floatingActionButtonVerCliente)

            textViewNombre.setText(cliente.nombre)
            textViewContacto.setText(cliente.contacto)
            textViewNumeroTel.setText(cliente.telefono)
            textViewDomicilio.setText(cliente.ubicacion)
            buttonVerCliente.setOnClickListener {
                val segue = Intent(ctx, ActivityClientInfo::class.java)
                segue.putExtra("cliente", cliente)
                segue.putExtra("ruta", ruta)
                segue.putExtra("excelSheet", excelSheet)
                startActivity(ctx, segue, null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterRecyclerClientes.ClientesViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.recycler_clientes_adapter, null)
        return ClientesViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterRecyclerClientes.ClientesViewHolder, position: Int) {
        holder.bind(clientes[position])
    }

    override fun getItemCount(): Int {
        return clientes.size
    }


}