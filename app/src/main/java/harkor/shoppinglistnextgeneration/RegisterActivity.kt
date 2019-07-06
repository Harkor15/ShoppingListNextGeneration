package harkor.shoppinglistnextgeneration

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = FirebaseAuth.getInstance()

        reg_button.setOnClickListener(View.OnClickListener {
            val login=reg_login_edit_text.text.toString()
            val password=reg_password_edit_text.text.toString()
            val password2 = reg_password2_edit_text.text.toString()
            if(login!=""&&password==password2&&password!=""){
                auth.createUserWithEmailAndPassword(login, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(baseContext, R.string.account_created,Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Log.w("slng", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        })
    }


}
