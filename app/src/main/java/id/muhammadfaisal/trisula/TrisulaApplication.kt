package id.muhammadfaisal.trisula

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import id.muhammadfaisal.trisula.database.dao.UserDao
import id.muhammadfaisal.trisula.database.entity.UserEntity
import id.muhammadfaisal.trisula.helper.AuthHelper
import id.muhammadfaisal.trisula.helper.DatabaseHelper
import id.muhammadfaisal.trisula.helper.RoomHelper
import id.muhammadfaisal.trisula.model.firebase.UserModelFirebase

class TrisulaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        this.insertUser()
    }

    private fun insertUser() {
        val userDao = RoomHelper.userDao(this)
        if (AuthHelper.getCurrentUser() != null) {
            DatabaseHelper.getUserReference().addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sizeFirebase = snapshot.childrenCount
                    val sizeRoom = userDao.getAll().size

                    if (userDao.getAll().isEmpty()){
                        this@TrisulaApplication.loopData(snapshot, userDao)
                    } else {
                        if (sizeFirebase != sizeRoom.toLong()) {
                            userDao.deleteAll()
                            this@TrisulaApplication.loopData(snapshot, userDao)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TrisulaApplication::class.simpleName, error.message)
                }

            })
        }
    }

    private fun loopData(snapshot: DataSnapshot, userDao: UserDao) {
        for (data in snapshot.children) {
            val user = data.getValue(UserModelFirebase::class.java)!!
            userDao.insert(
                UserEntity(
                    null,
                    user.name!!,
                    user.email!!,
                    user.phoneNumber!!,
                    user.groupId!!,
                    null
                )
            )
        }
    }
}