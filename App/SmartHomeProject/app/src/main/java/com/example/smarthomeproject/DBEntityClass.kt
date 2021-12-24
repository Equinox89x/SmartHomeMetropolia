package com.example.smarthomeproject

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity
data class Home(@PrimaryKey val uid: Long = 1.toLong(), val HouseName: String = "Unset", val Country: String = "Unset", val City: String = "Unset", val Street: String = "Unset", val StreetNumber: String = "Unset", val PostalCode: String = "Unset") { //constructor, getter and setter are implicit :)
    override fun toString(): String = "$HouseName"
}
@Dao
interface HomeDAO {
    @Query("SELECT * FROM home")
    fun getInfo(): LiveData<Home>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: Home): Long

    @Update
    fun update(user: Home)
}

@Database(entities = [(Home::class)], version = 1)
abstract class HomeDB: RoomDatabase() {
    abstract fun homeDao(): HomeDAO
    /* one and only one instance, similar to static in Java */
    companion object {
        private var sInstance: HomeDB? = null

        @Synchronized
        fun get(context: Context): HomeDB {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.applicationContext, HomeDB::class.java, "home.db").build()
            }
            return sInstance!!
        }
    }
}
