package harkor.shoppinglistnextgeneration

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.details_list_item.view.*

class DetailsListAdapter(var items:ArrayList<ItemStruct>,private val context:Context):  RecyclerView.Adapter<DetailsViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        return DetailsViewHolder(LayoutInflater.from(context).inflate(R.layout.details_list_item, parent,false) )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        holder.nameCheckBox.text=items[position].name
        holder.nameCheckBox.isChecked = items[position].status=="selected"

        Log.d("slng",items[position].status)
        //TODO: ON DELETE CLICK
        //TODO: ON CHECK / UNCHECK

    }

}

class DetailsViewHolder(view: View):RecyclerView.ViewHolder(view){
    val nameCheckBox=view.item_check_box
    val deleteIcon=view.item_delete_icon
}