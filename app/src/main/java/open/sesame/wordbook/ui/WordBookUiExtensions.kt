package open.sesame.wordbook.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import open.sesame.wordbook.data.WordBookViewModel

// Aggregated snapshot
data class WordSnapshot(
    val english: List<String>,
    val chokri: List<String>,
    val meaning: List<String>,
    val enExtra: List<String?>,
    val ckExtra: List<String?>,
    val submitter: List<String?>,
    val partOf: List<String?>,

    )

// Bundles parallel flows into one data object
@Composable
fun WordBookViewModel.collectWords(): WordSnapshot = WordSnapshot(
    english = englishWord.collectAsState(emptyList()).value,
    chokri = chokriWord.collectAsState(emptyList()).value,
    meaning = chokriMeaning.collectAsState(emptyList()).value,
    enExtra = englishExtra.collectAsState(emptyList()).value,
    ckExtra = chokriExtra.collectAsState(emptyList()).value,
    submitter = submitter.collectAsState(emptyList()).value,
    partOf = partOf.collectAsState(emptyList()).value
)

// Safe list indexing + default
fun <T> List<T?>.safeGet(index: Int): T? = getOrNull(index)

// For non-null Strings with default blank
fun List<String?>.safeGet(index: Int): String = getOrNull(index).orEmpty()
