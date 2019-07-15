package harkor.shoppinglistnextgeneration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity(),DetailsInterface {
    var items=ArrayList<ItemStruct>()
    val adapter= DetailsListAdapter(items,this)
    override fun setListData(name: String, mine: Boolean, shared: Boolean, items: ArrayList<ItemStruct>) {
        details_name.text=name
        //TODO: SHARED
        adapter.items=items
        adapter.notifyDataSetChanged()
        Log.d("slng details", items.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        MobileAds.initialize(this,applicationContext.resources.getString(R.string.ADDMOB_APP_ID))
        val adRequest = AdRequest.Builder().build()
        details_ad_view.loadAd(adRequest)
        val listId= intent.extras.getString("listId")
        val userId= intent.extras.getString("userId")
        details_recycler_view.layoutManager= LinearLayoutManager(this)
        details_recycler_view.adapter=adapter
        details_back_icon.setOnClickListener{finish()}
        val auth = FirebaseAuth.getInstance()
        val firestoreManager=FirestoreManager(auth.currentUser!!.uid)
        firestoreManager.getShoppingList(listId!!,userId!!,this)
    }
}
interface DetailsInterface{fun setListData(name:String, mine:Boolean, shared:Boolean, items:ArrayList<ItemStruct>)}
