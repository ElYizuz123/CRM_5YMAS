package Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.crm5ymas.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AdapterNuevoCliente(val ctx: Context, val adicionales:ArrayList<String>) : RecyclerView.Adapter<AdapterNuevoCliente.AdicionalesViewHolder>() {
    inner class AdicionalesViewHolder(itemView: View): ViewHolder(itemView){
        fun bind(adicional: String, position: Int){
            val textoAdicionales = itemView.findViewById<TextView>(R.id.textViewAdicional)
            val buttonEliminar = itemView.findViewById<FloatingActionButton>(R.id.floatingActionButtonBorrar)

            textoAdicionales.text=adicional
            buttonEliminar.setOnClickListener{
                adicionales.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdicionalesViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.adapter_nuevo_cliente, null)
        return AdicionalesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return adicionales.size
    }

    override fun onBindViewHolder(holder: AdicionalesViewHolder, position: Int) {
        holder.bind(adicionales[position], position)
    }
}