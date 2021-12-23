package id.muhammadfaisal.trisula.database.dao

import androidx.room.*
import id.muhammadfaisal.trisula.database.entity.UserEntity

@Dao
interface UserDao {

    @Insert
    fun insert(userEntity: UserEntity)

    @Query("DELETE FROM m_user WHERE email = :email")
    fun delete(email: String)

    @Query("SELECT * FROM m_user WHERE email = :email")
    fun get(email: String) : UserEntity

    @Query("SELECT * FROM m_user")
    fun getAll() : List<UserEntity>

    @Query("SELECT * FROM m_user WHERE group_name = :groupName")
    fun getUserInGroup(groupName: String) : List<UserEntity>

    @Query("DELETE FROM m_user")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM m_user WHERE group_name = :groupName")
    fun getTotalUserInGroup(groupName: String) : Int
}