package open.sesame.wordbook.data

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM ohsesame")
    suspend fun getAllWords(): List<WordBookClass>
//
//    @Query("SELECT * FROM ohsesame ORDER BY englishWord DESC")
//    suspend fun getAllWordsZYX(): List<WordBookClass>
    @Query("SELECT englishWord FROM ohsesame ORDER BY englishWord ASC")
    fun wordABC(): Flow<List<String>>

    @Query("SELECT englishWord FROM ohsesame ORDER BY englishWord DESC")
    fun wordZYX(): Flow<List<String>>

    @Query("SELECT englishWord FROM ohsesame")
    fun getEnglishWord(): Flow<List<String>>

    @Query("SELECT englishExtra FROM ohsesame")
    fun getEnglishExtra(): Flow<List<String?>>

    @Query("SELECT chokri FROM ohsesame")
    fun getChokriWord(): Flow<List<String>>

    @Query("SELECT chokriMeaning FROM ohsesame")
    fun getChokriMeaning(): Flow<List<String>>

    @Query("SELECT chokriExtraExample FROM ohsesame")
    fun getChokriExtra(): Flow<List<String>>

    @Query("SELECT submittedBy FROM ohsesame")
    fun getSubmitter(): Flow<List<String>>

    @Query("SELECT partOf FROM ohsesame")
    fun getPartOf(): Flow<List<String>>
}