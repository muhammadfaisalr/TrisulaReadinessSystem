package id.muhammadfaisal.trisula.helper

import android.content.Context
import androidx.room.Room
import id.muhammadfaisal.trisula.database.TrisulaDatabase
import id.muhammadfaisal.trisula.database.dao.UserDao
import id.muhammadfaisal.trisula.utils.Constant

class RoomHelper {
    companion object{
        private fun connect(context: Context): TrisulaDatabase {
            return Room.databaseBuilder(context, TrisulaDatabase::class.java, "trisula-db").allowMainThreadQueries().build()
        }

        fun userDao(context: Context): UserDao {
            val database = connect(context)

            return database.userDao()
        }
    }
}