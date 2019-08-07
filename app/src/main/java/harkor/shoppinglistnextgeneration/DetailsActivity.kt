package harkor.shoppinglistnextgeneration

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_details.*

class DetailsActivity : AppCompatActivity(), DetailsInterface, EditDetails {
    private var items = ArrayList<FirestoreManager.ItemStruct>()
    private val adapter = DetailsListAdapter(items, this, this)
    private val loggedUserId = FirebaseAuth.getInstance().currentUser!!.uid
    private var userId = ""
    private var listId = ""
    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        MobileAds.initialize(this, applicationContext.resources.getString(R.string.ADDMOB_APP_ID))
        val adRequest = AdRequest.Builder().build()
        details_ad_view.loadAd(adRequest)
        listId = intent.extras.getString("listId")
        userId = intent.extras.getString("userId")
        details_recycler_view.layoutManager = LinearLayoutManager(this)
        details_recycler_view.adapter = adapter
        details_back_icon.setOnClickListener { finish() }
        val auth = FirebaseAuth.getInstance()
        val firestoreManager = FirestoreManager(auth.currentUser!!.uid)
        firestoreManager.getShoppingList(listId!!, userId!!, this)
        details_add_button.setOnClickListener {
            val newProduct = details_new_edit_text.text.toString()
            if (newProduct == "") {
                Toast.makeText(applicationContext, R.string.enter_name_of_new_product, Toast.LENGTH_SHORT).show()
            } else {
                items.add(FirestoreManager.ItemStruct(newProduct, "unselected"))
                firestoreManager.editProduct(userId, listId, items)
                details_new_edit_text.setText("")
            }
        }
        if (userId != loggedUserId) {
            details_delete_icon.visibility = View.INVISIBLE
        } else {
            details_delete_icon.setOnClickListener {
                MobileAds.initialize(this, applicationContext.resources.getString(R.string.ADDMOB_APP_ID))
                mInterstitialAd = InterstitialAd(this)
                mInterstitialAd.adUnitId = applicationContext.resources.getString(R.string.FULL_SCREEN_AD_ID)
                mInterstitialAd.loadAd(AdRequest.Builder().build())
                mInterstitialAd.adListener = object : AdListener() {
                    override fun onAdClosed() {
                        finish()
                    }
                }
                AlertDialog.Builder(this)
                    .setTitle(R.string.delete_list)
                    .setMessage(R.string.are_you_sure)
                    .setPositiveButton(R.string.ok) { dialog, id ->

                        firestoreManager.deleteList(listId)
                        firestoreManager.deleteList(listId)
                        if (mInterstitialAd.isLoaded) {
                            mInterstitialAd.show()
                        }
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .create().show()
            }
        }

    }

    override fun setListData(
        name: String,
        mine: Boolean,
        shared: Boolean,
        items: ArrayList<FirestoreManager.ItemStruct>
    ) {
        details_name.text = name

        this.items = items
        adapter.items = this.items
        adapter.notifyDataSetChanged()
    }

    override fun deleteProduct(productId: Int) {
        val firestoreManager = FirestoreManager(loggedUserId)
        items.removeAt(productId)
        firestoreManager.editProduct(userId, listId, items)
    }

    override fun checkBoxSign(position: Int, check: Boolean) {
        val firestoreManager = FirestoreManager(loggedUserId)
        if (check) {
            items[position].status = "selected"

        } else {
            items[position].status = "unselected"
        }
        firestoreManager.editProduct(userId, listId, items)
    }
}

interface DetailsInterface {
    fun setListData(name: String, mine: Boolean, shared: Boolean, items: ArrayList<FirestoreManager.ItemStruct>)
}

interface EditDetails {
    fun deleteProduct(productId: Int)
    fun checkBoxSign(position: Int, check: Boolean)
}