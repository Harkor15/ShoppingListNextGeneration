package harkor.shoppinglistnextgeneration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_add_new_list.*


class AddNewListActivity : AppCompatActivity(), AddNewShoppingListResult {
    override fun addSuccess() {
        Toast.makeText(applicationContext,R.string.shopping_list_added,Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun addFailure() {
        Toast.makeText(applicationContext,R.string.error,Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_list)
        val auth = FirebaseAuth.getInstance()
        Log.d("slng", "user: "+auth.currentUser?.uid)
        val items: ArrayList<String> = ArrayList()
        add_recycler_view.layoutManager = LinearLayoutManager(this)
        val adapter=AddNewItemsAdapter(items,this )
        add_recycler_view.adapter=adapter

        add_back_button.setOnClickListener {
            finish()
        }
        add_item_button.setOnClickListener {
            val itemText=add_single_item_field.text.toString()
            if(itemText!=""){
                add_single_item_field.setText("")
                items.add(itemText)
                adapter.notifyDataSetChanged()
            }
        }
        add_confirm_button.setOnClickListener{
            add_confirm_button.visibility=View.INVISIBLE
            val firestoreManager=FirestoreManager(auth.currentUser!!.uid)
            var shoppingListName=add_shopping_list_title.text.toString()
            if(shoppingListName=="") shoppingListName= getString(R.string.new_shopping_list)
            firestoreManager.addNewList(items,shoppingListName,this)
        }
    }
}

interface AddNewShoppingListResult{
    fun addSuccess()
    fun addFailure()
}