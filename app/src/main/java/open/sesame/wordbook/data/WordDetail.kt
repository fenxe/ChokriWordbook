package open.sesame.wordbook.data

//A simplified clean version
data class WordDetail(
    val english: String,
    val chokri: String,
    val meaning: String,
    val enExtra: String,
    val ckExtra: String,
    val submitter: String,
    val partOf: String
)