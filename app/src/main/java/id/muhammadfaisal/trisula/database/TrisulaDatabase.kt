package id.muhammadfaisal.trisula.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase
import id.muhammadfaisal.trisula.database.dao.UserDao
import id.muhammadfaisal.trisula.database.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class TrisulaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}