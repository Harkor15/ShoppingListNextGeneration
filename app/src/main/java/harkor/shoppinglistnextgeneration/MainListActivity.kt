package harkor.shoppinglistnextgeneration

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main_list.*

class MainListActivity : AppCompatActivity(), MainListInferface {
    var listOfListst=ArrayList<MainListItem>()
    val adapter=MainListAdapter(listOfListst,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)
        MobileAds.initialize(this,applicationContext.resources.getString(R.string.ADDMOB_APP_ID))
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        main_list_recycler_view.adapter=adapter
        main_list_recycler_view.layoutManager = LinearLayoutManager(this)

        val auth = FirebaseAuth.getInstance()
        val firestoreManager=FirestoreManager(auth.currentUser!!.uid)
        firestoreManager.getListOfLists(this)

        add_new_list_button.setOnClickListener(View.OnClickListener {
            val intent= Intent(applicationContext,AddNewListActivity::class.java)
            startActivity(intent)
        })
        sign_out_icon.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        })
    }

    override fun setDataInMainList(listOfListst:ArrayList<MainListItem>) {
        adapter.items=listOfListst
        adapter.notifyDataSetChanged()
    }
}

class MainListItem(val name:String, val shared:Boolean, val mine:Boolean, val userId:String, val listId:String)
interface MainListInferface{fun setDataInMainList(listOfListst:ArrayList<MainListItem>)}