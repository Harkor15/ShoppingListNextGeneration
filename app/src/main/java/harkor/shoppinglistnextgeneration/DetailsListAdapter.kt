package harkor.shoppinglistnextgeneration

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.details_list_item.view.*

class DetailsListAdapter(
    var items: ArrayList<FirestoreManager.ItemStruct>,
    private val context: Context,
    private val editDetails: EditDetails
) :
    RecyclerView.Adapter<DetailsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        return DetailsViewHolder(LayoutInflater.from(context).inflate(R.layout.details_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        holder.nameCheckBox.text = items[position].name
        holder.nameCheckBox.isChecked = items[position].status == "selected"
        holder.deleteIcon.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(R.string.delete_product)
                .setMessage(R.string.are_you_sure)
                .setPositiveButton(R.string.ok) { dialog, id -> editDetails.deleteProduct(position) }
                .setNegativeButton(R.string.cancel, null)
                .create().show()
        }
        holder.nameCheckBox.setOnClickListener { editDetails.checkBoxSign(position, holder.nameCheckBox.isChecked) }
    }
}

class DetailsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val nameCheckBox = view.item_check_box
    val deleteIcon = view.item_delete_icon
}