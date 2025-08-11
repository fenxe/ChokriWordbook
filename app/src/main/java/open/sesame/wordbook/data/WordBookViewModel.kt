package open.sesame.wordbook.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow

class WordBookViewModel(application: Application): AndroidViewModel(application) {
    private val db = ChokriDatabase.create(application)

    val englishWord: Flow<List<String>> = db.wordDao().getEnglishWord()   // May change to stateflow or maybe not
    val englishExtra: Flow<List<String?>> = db.wordDao().getEnglishExtra()
    val chokriWord: Flow<List<String>> = db.wordDao().getChokriWord()   // May change to stateflow or maybe not
    val chokriMeaning: Flow<List<String>> = db.wordDao().getChokriMeaning()
    val chokriExtra: Flow<List<String>> = db.wordDao().getChokriExtra()   // May change to stateflow or maybe not
    val submitter: Flow<List<String>> = db.wordDao().getSubmitter()
    val partOf: Flow<List<String>> = db.wordDao().getPartOf()   // May change to stateflow or maybe not

}

