package harkor.shoppinglistnextgeneration

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_lists_item.view.*
import kotlin.collections.ArrayList

class MainListAdapter(var items:ArrayList<MainListItem>,
                      private val context: Context): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.main_lists_item, parent,false) )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener{
            val intent = Intent(context,DetailsActivity::class.java)
            //val bundle= Bundle()
            intent.putExtra("userId", items[position].userId)
            intent.putExtra("listId", items[position].listId)
            startActivity(context,intent,null)
        }
        holder.name.text=items[position].name
        if(items[position].mine){
            holder.deleteIcon.setOnClickListener{
                deleteDialog(position)
            }
            holder.sharedIcon.setOnClickListener{
                shareDialog()
            }
            if(items[position].shared) holder.sharedIcon.setImageResource(R.drawable.ic_people_green_24dp)
        }else{
            holder.deleteIcon.visibility=View.INVISIBLE
            holder.sharedIcon.setImageResource(R.drawable.ic_people_blue_24dp)
            holder.sharedIcon.setOnClickListener{
                unShareDialog()
            }
        }
    }
    private fun deleteDialog(position:Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.delete_list)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.ok) { dialog, id->
                FirestoreManager(items[position].userId).deleteList(items[position].listId)
                items.removeAt(position)
                this.notifyDataSetChanged()
            }
            .setNegativeButton(R.string.cancel,null)
        builder.create().show()
    }
    private fun shareDialog(){
        //TODO: SHARE DIALOG
    }
    private fun unShareDialog(){
        //TODO: UN SHARE DIALOG
    }

}

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val name: TextView=view.main_list_item_name
    val sharedIcon: ImageView =view.main_list_item_shared_icon
    val deleteIcon:ImageView=view.main_list_item_delete_icon
}