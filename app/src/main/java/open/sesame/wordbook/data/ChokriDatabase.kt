package open.sesame.wordbook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WordBookClass::class], version = 1, exportSchema = false
)
abstract class ChokriDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao

    companion object {
        private const val DB_NAME = "gloridb"

        @Volatile
        private var Instance: ChokriDatabase? = null

        fun create(context: Context): ChokriDatabase {
            /** if the Instance is not null, return it, otherwise create a new database instance.*/
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ChokriDatabase::class.java, DB_NAME)
                    .createFromAsset(DB_NAME)
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}