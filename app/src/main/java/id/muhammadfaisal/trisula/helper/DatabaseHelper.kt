package id.muhammadfaisal.trisula.helper

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import id.muhammadfaisal.trisula.utils.Constant

class DatabaseHelper {
    class Endpoint{
        companion object{
            const val USER = "USER"
            const val GROUP = "GROUP"
            const val TOKEN = "TOKEN"

            const val USERS = "users"
            const val MESSAGES = "messages"
        }
    }
    companion object{
        fun getUserReference(): DatabaseReference {
            return FirebaseDatabase.getInstance().getReference(Endpoint.USER)
        }

        fun getAccountReference(uid: String): DatabaseReference {
            return getUserReference().child(uid)
        }

        fun getGroupReference() : DatabaseReference{
            return FirebaseDatabase.getInstance().getReference(Endpoint.GROUP)
        }

        fun getDetailGroupReference(groupName: String) : DatabaseReference{
            return FirebaseDatabase.getInstance().getReference(Endpoint.GROUP).child(groupName)
        }

        fun getUserInGroupReference(groupName : String) : DatabaseReference{
            return FirebaseDatabase.getInstance().getReference(Endpoint.GROUP).child(groupName).child(Endpoint.USERS)
        }

        fun getInboxInGroupReference(groupName : String) : DatabaseReference{
            return FirebaseDatabase.getInstance().getReference(Endpoint.GROUP).child(groupName).child(Endpoint.MESSAGES)
        }

        fun getTokenReference() : DatabaseReference{
            return FirebaseDatabase.getInstance().getReference(Endpoint.TOKEN)
        }
    }
}