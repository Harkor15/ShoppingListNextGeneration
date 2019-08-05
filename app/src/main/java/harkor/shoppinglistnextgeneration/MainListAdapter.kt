package harkor.shoppinglistnextgeneration

import android.app.AlertDialog
import android.content.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.dialog_share_list.view.*
import kotlinx.android.synthetic.main.main_lists_item.view.*


class MainListAdapter(private val context: Context): RecyclerView.Adapter<ViewHolder>() {
    lateinit var firestoreManager:FirestoreManager
    var items=ArrayList<MainListItem>()
    var sharedItems=ArrayList<MainListItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.main_lists_item, parent,false) )
    }

    override fun getItemCount(): Int {
        return items.size+sharedItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        lateinit var singleListItem:MainListItem
        if(position<items.size){
            singleListItem=items[position]
        }else{
            singleListItem=sharedItems[position-items.size]
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(context,DetailsActivity::class.java)
            intent.putExtra("userId", singleListItem.userId)
            intent.putExtra("listId", singleListItem.listId)
            startActivity(context,intent,null)
        }
        holder.name.text=singleListItem.name
        if(singleListItem.mine){
            holder.deleteIcon.setOnClickListener{
                deleteDialog(position)
            }
            holder.deleteIcon.visibility=View.VISIBLE
            holder.sharedIcon.setOnClickListener{
                val code=singleListItem.userId+"_"+singleListItem.listId
                shareDialog(code)
            }


            val shared=singleListItem.shared
            Log.d("slng", "$shared on $position")
            if(singleListItem.shared) holder.sharedIcon.setImageResource(R.drawable.ic_people_green_24dp)
            else holder.sharedIcon.setImageResource(R.drawable.ic_people_black_24dp)
        }else{
            holder.deleteIcon.visibility=View.INVISIBLE
            holder.sharedIcon.setImageResource(R.drawable.ic_people_blue_24dp)
            holder.sharedIcon.setOnClickListener{
                unShareDialog(singleListItem.sharedId,position-items.size)
            }
        }
    }
    private fun deleteDialog(position:Int){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.delete_list)
            .setMessage(R.string.are_you_sure)
            .setPositiveButton(R.string.ok) { dialog, id->
                firestoreManager.deleteList(items[position].listId)
                items.removeAt(position)
                this.notifyDataSetChanged()
            }
            .setNegativeButton(R.string.cancel,null)
        builder.create().show()
    }
    private fun shareDialog(code:String){
        val builder=AlertDialog.Builder(context)
        val mView=LayoutInflater.from(context).inflate(R.layout.dialog_share_list,null)
        //builder.setTitle(R.string.share_your_shopping_list)
        //builder.setPositiveButton(R.string.ok,null)

        val multiFormatWriter = MultiFormatWriter()
        try{
            val bitMatrix = multiFormatWriter.encode(code, BarcodeFormat.QR_CODE,500,500)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            mView.share_qr_code.setImageBitmap(bitmap)
        }catch (e: WriterException){
            e.printStackTrace()
        }
        mView.share_copy_code.setOnClickListener {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("simple text", code)
            clipboard.primaryClip = clip
            Toast.makeText(context,R.string.copied_to_clipboard,Toast.LENGTH_SHORT).show()
        }
        mView.share_send_button.setOnClickListener{
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_TEXT, code)
            context.startActivity(Intent.createChooser(sharingIntent, context.getResources().getString(R.string.share_using)))
        }
        builder.setView(mView)
        builder.create().show()
    }
    private fun unShareDialog(sharedId:String,numberInList:Int){
        AlertDialog.Builder(context)
            .setTitle(R.string.cancel_shareing)
            .setMessage(R.string.cancel_sharing_sure)
            .setNegativeButton(R.string.cancel,null)
            .setPositiveButton(R.string.confirm){dialog,id ->
                //firestoreManager.deleteList(sharedId)
                firestoreManager.deleteSharedList(sharedId)
                sharedItems.removeAt(numberInList)
                this.notifyDataSetChanged()
            }
            .create().show()
    }
}

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
    val name: TextView=view.main_list_item_name
    val sharedIcon: ImageView =view.main_list_item_shared_icon
    val deleteIcon:ImageView=view.main_list_item_delete_icon
}