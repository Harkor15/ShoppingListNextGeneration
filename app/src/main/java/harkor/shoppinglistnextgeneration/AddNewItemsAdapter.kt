package harkor.shoppinglistnextgeneration

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.add_list_item.view.*

class AddNewItemsAdapter(private val items: ArrayList<String>, private val context: Context) :
    RecyclerView.Adapter<AddNewItemsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.add_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemName.text = items[position]
        holder.itemDeleteButton.setOnClickListener {
            items.removeAt(position)
            this.notifyDataSetChanged()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val itemName: TextView = view.add_item_name
        val itemDeleteButton: ImageView = view.add_item_delete
    }
}
