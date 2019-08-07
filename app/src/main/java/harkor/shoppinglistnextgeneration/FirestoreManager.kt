package harkor.shoppinglistnextgeneration

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

@Suppress("UNCHECKED_CAST")
class FirestoreManager(private val userId: String) {
    private val db = FirebaseFirestore.getInstance()

    fun addNewList(items: ArrayList<String>, listName: String, resultInterface: AddNewShoppingListResult) {
        val itemsCompleted = ArrayList<ItemStruct>()
        for (item in items) {
            itemsCompleted.add(ItemStruct(item, "unselected"))

        }
        val shoppingList = hashMapOf(
            "items" to itemsCompleted,
            "shared" to 0,
            "name" to listName
        )
        db.collection("users").document(userId).collection("user_lists")
            .add(shoppingList)
            .addOnSuccessListener { resultInterface.addSuccess() }
            .addOnFailureListener { resultInterface.addFailure() }
    }

    fun getListOfLists(mainListInferface: MainListInferface) {
        db.collection("users").document(userId).collection("user_lists")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w("slng", "Listen failed.", e)
                    return@addSnapshotListener
                }
                val listOfLists = ArrayList<MainListItem>()
                for (doc in value!!) {
                    var shared = false
                    if (doc.data["shared"] != 0L) shared = true
                    listOfLists.add(MainListItem(doc.data["name"] as String, shared, true, userId, doc.id, ""))
                }
                mainListInferface.setDataInMainList(listOfLists)
            }
    }

    fun deleteList(listId: String) {
        db.collection("users").document(userId).collection("user_lists").document(listId)
            .delete()
            .addOnSuccessListener { Log.d("slng", "List deleted") }
            .addOnFailureListener { Log.d("slng", "Deleting failure") }
    }

    fun getShoppingList(listId: String, userId: String, detailsInterface: DetailsInterface) {
        db.collection("users").document(userId).collection("user_lists").document(listId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("slng", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val items = ArrayList<ItemStruct>()
                    val dataItems = snapshot.data?.get("items") as ArrayList<HashMap<String, String>>
                    for (item in dataItems) {
                        items.add(ItemStruct(item["name"].toString(), item["status"].toString()))
                    }
                    var mine = false
                    if (this.userId == userId) mine = true
                    var shared = false
                    val sharedDb = snapshot.data!!["shared"]
                    if (sharedDb != 0L) {
                        shared = true
                    }
                    detailsInterface.setListData(
                        snapshot.data?.get("name") as String, mine, shared, items
                    )
                }
            }
    }

    fun editProduct(userId: String, listId: String, items: ArrayList<ItemStruct>) {
        db.collection("users").document(userId).collection("user_lists").document(listId)
            .update(mapOf("items" to items))
            .addOnFailureListener { Log.d("slng", "Update failure") }
            .addOnSuccessListener { Log.d("slng", "Update success") }
    }

    fun addSharedLists(
        listOwnerId: String,
        listId: String,
        addToSharedInterface: AddToSharedInterface,
        mainListSharedInterface: MainListSharedInterface
    ) {
        if (userId == listOwnerId) {
            addToSharedInterface.ownListError()
        } else {
            val sharedList = hashMapOf(
                "list_owner_id" to listOwnerId,
                "list_id" to listId
            )
            db.collection("users").document(userId).collection("user_shared_lists")
                .add(sharedList)
                .addOnSuccessListener { doc ->
                    addToSharedInterface.sharedSuccess()
                    addSingleSharedList(doc.id, listOwnerId, listId, mainListSharedInterface)
                }
                .addOnFailureListener { addToSharedInterface.sharedError() }
        }
    }

    fun deleteSharedList(sharedListRecordId: String) {
        db.collection("users").document(userId).collection("user_shared_lists").document(sharedListRecordId)
            .delete()
            .addOnSuccessListener { Log.d("slng", "List deleted") }
            .addOnFailureListener { Log.d("slng", "Deleting failure") }
    }

    fun getSharedLists(mainListSharedInterface: MainListSharedInterface) {
        db.collection("users").document(userId).collection("user_shared_lists")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val listOwnerId = document.get("list_owner_id") as String
                    val listId = document.get("list_id") as String
                    addSingleSharedList(id, listOwnerId, listId, mainListSharedInterface)
                }
            }
            .addOnFailureListener { Log.d("slng", "Get shared list ERROR") }
    }

    private fun addSingleSharedList(
        recordId: String,
        listOwnerId: String,
        listId: String,
        mainListSharedInterface: MainListSharedInterface
    ) {
        db.collection("users").document(listOwnerId).collection("user_lists").document(listId)
            .get()
            .addOnFailureListener { Log.d("slng", "Single shared list get ERROR") }
            .addOnSuccessListener { doc ->
                if (doc != null) {
                    try {
                        mainListSharedInterface.addSharedList(
                            MainListItem(
                                doc.data!!["name"] as String,
                                true,
                                false,
                                listOwnerId,
                                doc.id,
                                recordId
                            )
                        )
                    } catch (ex: Exception) {
                        deleteSharedList(recordId)
                    }

                }
            }
    }

    class ItemStruct(var name: String, var status: String) : Serializable
}
