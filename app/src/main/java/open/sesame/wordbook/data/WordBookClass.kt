package open.sesame.wordbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ohsesame")
data class WordBookClass(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val englishWord: String,
    val englishExtra: String?,  // added ? since it can be empty field
    val chokri: String,
    val chokriMeaning: String?,
    val chokriExtraExample: String?,
    val submittedBy: String?,
    val partOf: String?,
)

/**
 * CREATE TABLE "ohsesame"
 * (
 *     id                 INTEGER not null
 *         constraint ohsesame_pk
 *             primary key autoincrement,
 *     englishWord        TEXT    not null,
 *     englishExtra       TEXT,
 *     chokri             TEXT    not null,
 *     chokriMeaning      TEXT,
 *     chokriExtraExample TEXT,
 *     submittedBy        TEXT,
 *     partOf             TEXT
 * )
 *
 *
 *
 */