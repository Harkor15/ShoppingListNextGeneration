package harkor.shoppinglistnextgeneration

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class FirestoreManager(private val userId: String) {
    val db = FirebaseFirestore.getInstance()
    fun addNewList(items: ArrayList<String>, listName:String){
        val itemsCompleted= ArrayList<ItemStruct>()
        for(i in items){
            itemsCompleted.add(ItemStruct(i,1))
        }
        val shoppingList= hashMapOf(
            "items" to itemsCompleted,
            "shared" to 0,
            "name" to listName
        )

        db.collection("users").document(userId).collection("user_lists")
            .add(shoppingList)
            .addOnSuccessListener { documentReference ->
                Log.d("slng", "DocumentSnapshot added with ID: ${documentReference.id}") }
            .addOnFailureListener { e ->
            Log.w("slng", "Error adding document", e)
        }

    }
}

class ItemStruct( var name: String,  var status: Int) : Serializable
//class ListStruct(shared: Int,name:String, items: ArrayList<ItemStruct>)