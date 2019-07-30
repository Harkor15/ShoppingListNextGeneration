package harkor.shoppinglistnextgeneration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main_list.*
import kotlinx.android.synthetic.main.dialog_add_shared.view.*
import java.util.*
import kotlin.collections.ArrayList
import android.content.ClipboardManager
import android.content.Context
import android.util.Log


class MainListActivity : AppCompatActivity(), MainListInferface, AddToSharedInterface{
    override fun sharedSucces() {
        Toast.makeText(this,R.string.success,Toast.LENGTH_SHORT).show()
    }

    override fun sharedError() {
        Toast.makeText(this,R.string.error,Toast.LENGTH_SHORT).show()
    }

    override fun ownListError() {
        Toast.makeText(this,R.string.success,Toast.LENGTH_SHORT).show()
    }

    var listOfListst=ArrayList<MainListItem>()
    val adapter=MainListAdapter(listOfListst,this)
    var savedTime=0L
    private lateinit var firestoreManager:FirestoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)
        MobileAds.initialize(this,applicationContext.resources.getString(R.string.ADDMOB_APP_ID))
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        main_list_recycler_view.adapter=adapter
        main_list_recycler_view.layoutManager = LinearLayoutManager(this)

        val auth = FirebaseAuth.getInstance()
        firestoreManager=FirestoreManager(auth.currentUser!!.uid)
        firestoreManager.getListOfLists(this)

        add_new_list_button.setOnClickListener(View.OnClickListener {
            val intent= Intent(applicationContext,AddNewListActivity::class.java)
            startActivity(intent)
        })
        sign_out_icon.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()
            finish()
        })
        add_shared_button.setOnClickListener{addSharedDialog()}
    }

    override fun setDataInMainList(listOfListst:ArrayList<MainListItem>) {
        adapter.items=listOfListst
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        val currentTime=Calendar.getInstance().time.time
        if(currentTime-savedTime<2000){
            this.finishAffinity()
        }else{
            Toast.makeText(this,R.string.click_again_to_exit, Toast.LENGTH_SHORT).show()
            savedTime=currentTime
        }
    }
    private fun addSharedDialog(){
        val builder= AlertDialog.Builder(this)
        val mView = LayoutInflater.from(this).inflate(R.layout.dialog_add_shared,null)
        builder.setTitle(R.string.add_shared_list)
        builder.setNegativeButton(R.string.cancel,null)
        mView.add_shared_camera.setOnClickListener{
            startQRScanner()
        }
        mView.add_shared_clipboard.setOnClickListener{
            val clipboardManager=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if(clipboardManager.primaryClip!=null){
                val code = clipboardManager.primaryClip.getItemAt(0).text
                addSharedList(code.toString())
            }
        }
        builder.setView(mView)
        builder.create().show()
    }

    private fun startQRScanner(){
        IntentIntegrator(this).initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data)
        if(result!=null){
            if(result.contents==null){
                Toast.makeText(this,R.string.scanning_canceled,Toast.LENGTH_SHORT).show()
            }else{
                addSharedList(result.contents)
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    private fun addSharedList(code:String){
        val userAndListId=code.split("_")
        if(userAndListId.size==2){
            firestoreManager.addSharedLists(userAndListId[0],userAndListId[1],this)
        }
    }
}

class MainListItem(val name:String, val shared:Boolean, val mine:Boolean, val userId:String, val listId:String)
interface MainListInferface{fun setDataInMainList(listOfListst:ArrayList<MainListItem>)}
interface AddToSharedInterface{fun sharedSucces() fun sharedError() fun ownListError() }