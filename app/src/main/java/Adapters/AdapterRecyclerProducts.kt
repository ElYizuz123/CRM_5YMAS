package Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.crmpovasa.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import data.Cliente
import data.ProductoWQ
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class AdapterRecyclerProducts(val ctx: Context, var productos:List<ProductoWQ>) : RecyclerView.Adapter<AdapterRecyclerProducts.ProductosViewHolder>(){
    interface OnItemClickListener{
        fun onDeleteItemClick(producto: ProductoWQ)
    }
    var onItemClickListener: OnItemClickListener?=null
    fun updateList(newList:List<ProductoWQ>){
        val productDiff = ProductsDiffUtil(productos, newList)
        val result = DiffUtil.calculateDiff(productDiff)
        productos=newList
        result.dispatchUpdatesTo(this)
    }
    inner class ProductosViewHolder (itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(producto: ProductoWQ){
            val textViewNombre = itemView.findViewById<TextView>(R.id.textViewNombreProducto)
            val textViewCantidad = itemView.findViewById<TextView>(R.id.textViewCantidadProducto)
            val textViewSubtotal = itemView.findViewById<TextView>(R.id.textViewSubtotalProducto)
            val buttonDelete = itemView.findViewById<FloatingActionButton>(R.id.floatingActionButtonDeleteProduct)

            textViewNombre.setText(producto.desc)
            textViewCantidad.setText(producto.cantidad.toString())
            if(producto.peso==25){
                textViewSubtotal.setText(roundToTwoDecimals(producto.peso*producto.cantidad*producto.precio).toString())
            }
            else{
                textViewSubtotal.setText(roundToTwoDecimals(producto.cantidad*producto.precio).toString())
            }
            buttonDelete.setOnClickListener {
                onItemClickListener?.onDeleteItemClick(producto)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductosViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.recycler_products_adapter, null)
        return ProductosViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    override fun onBindViewHolder(holder: ProductosViewHolder, position: Int) {
        holder.bind(productos[position])
    }
    fun roundToTwoDecimals(number: Double): Double {
        val df = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
        return df.format(number).toDouble()
    }
}