package id.muhammadfaisal.trisula.helper

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase

class AuthHelper {
    companion object {
        fun getCurrentUser(): FirebaseUser? {
            return FirebaseAuth.getInstance().currentUser
        }

        fun getUid(): String? {
            return FirebaseAuth.getInstance().currentUser?.uid
        }
    }

    class Account {
        companion object {

            const val API_KEY = "AIzaSyCwXnUw79FdvhwPPaNr1mdIuYOs0C155gQ"
            const val DB_URL = "https://trisula-readiness-system-default-rtdb.firebaseio.com/"
            const val APP_ID = "trisula-readiness-system"

            fun signInWithEmailPassword(email: String, password: String): Task<AuthResult> {
                return FirebaseAuth
                    .getInstance()
                    .signInWithEmailAndPassword(email, password)
            }

            fun createAccount(user: UserModelFirebase): Task<AuthResult> {
                return FirebaseAuth
                    .getInstance()
                    .createUserWithEmailAndPassword(user.email!!, user.password!!)
            }

            fun updatePassword(user: UserModelFirebase): Task<Void> {
                return getCurrentUser()!!.updatePassword(user.password!!)
            }

            fun logout() {
                return FirebaseAuth.getInstance().signOut()
            }
        }
    }
}