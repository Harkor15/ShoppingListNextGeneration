package harkor.shoppinglistnextgeneration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_add_new_list.*


class AddNewListActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_list)

        val items: ArrayList<String> = ArrayList()
        add_recycler_view.layoutManager = LinearLayoutManager(this)
        val adapter=AddNewItemsAdapter(items,this )
        add_recycler_view.adapter=adapter




        add_back_button.setOnClickListener(View.OnClickListener {
            finish()
        })
        add_item_button.setOnClickListener {
            val itemText=add_single_item_field.text.toString()
            if(itemText!=""){
                add_single_item_field.setText("")
                items.add(itemText)
                adapter.notifyDataSetChanged()
            }
        }
        add_confirm_button.setOnClickListener{//TODO: Check empty fields and uid
            val firestoreManager=FirestoreManager("testtesttest")
            firestoreManager.addNewList(items,"testtest")
        }
    }
}

