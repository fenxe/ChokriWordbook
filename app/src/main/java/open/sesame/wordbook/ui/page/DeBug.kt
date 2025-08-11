package open.sesame.wordbook.ui.page

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import open.sesame.wordbook.R
import open.sesame.wordbook.data.ChokriDatabase
import open.sesame.wordbook.data.DebugViewModel
import open.sesame.wordbook.data.WordBookClass

// bottom style
/*
@OptIn(FlowPreview::class)
@Composable
fun DeBuggyDialog(
    debugViewModel: DebugViewModel,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column {
            // optional back button row
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color.DarkGray)
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = onBack) {
//                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
//                }
//                Text(
//                    text = "Debug Mode",
//                    color = Color.White,
//                    style = MaterialTheme.typography.titleMedium
//                )
//            }

// your existing content
            DeBuggy(
                onDismiss = onDismiss,
                viewModel = debugViewModel
            )
        }

    }
}
*/


//Dialog style
@OptIn(FlowPreview::class)
@Composable
fun DeBuggyDialog(
    onDismiss: () -> Unit,
    viewModel: DebugViewModel
) {
    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(

            usePlatformDefaultWidth = false, // allows full-screen-like dialog
            decorFitsSystemWindows = false  // edge-to-edge like main activity
        )
    ) {
        Surface(
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxSize()
        ) {
            DeBuggy(
                onDismiss = onDismiss,
                viewModel = viewModel
            )
        }
    }
}


@OptIn(FlowPreview::class)
@Composable
fun DeBuggy(
    onDismiss: () -> Unit, // for dialog
    viewModel: DebugViewModel
) {
    val ctx = LocalContext.current
    val focusManager = LocalFocusManager.current
    val horizontalScroll = rememberScrollState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // For double back press to exit
    var backPressCount by remember { mutableIntStateOf(0) }
    // Helper to detect keyboard visibility
    val isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    var searchQuery by remember { mutableStateOf("") }
    val debouncedQuery = remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        snapshotFlow { searchQuery }
            .debounce(300)
            .collect { debouncedQuery.value = it }
    }

    var isSearchVisible by remember { mutableStateOf(false) }
    var currentMatchIndex by remember { mutableIntStateOf(0) }

    fun itemFields(item: WordBookClass) = listOf(
        "${item.id}",
        item.englishWord,
        item.englishExtra ?: "<BAMO>", // BAMO as in ABSENT/EMPTY
        item.chokri,
        item.chokriMeaning ?: "<BAMO>",
        item.chokriExtraExample ?: "<BAMO>",
        item.submittedBy ?: "<BAMO>",
        item.partOf ?: "<BAMO>"
    )

    val matchedPositions = remember(debouncedQuery.value) {
        derivedStateOf {
            if (debouncedQuery.value.isBlank()) emptyList() else
                viewModel.items.flatMapIndexed { rowIndex, item ->
                    val fields = itemFields(item)
                    fields.mapIndexedNotNull { colIndex, field ->
                        if (field.contains(debouncedQuery.value, ignoreCase = true))
                            rowIndex to colIndex else null
                    }
                }
        }
    }


    val currentMatchPosition = if (matchedPositions.value.isEmpty()) {
        null
    } else {
        matchedPositions.value.getOrNull(
            currentMatchIndex.coerceIn(0, matchedPositions.value.lastIndex)
        )
    }


    val columnWidths = listOf(60.dp, 140.dp, 120.dp, 140.dp, 200.dp, 200.dp, 100.dp, 100.dp)
    val density = LocalDensity.current

    // Back key handling so no accidental close
    BackHandler(enabled = true) {
        when {
            // 1. If keyboard visible, close it
            isKeyboardOpen -> {
                focusManager.clearFocus()
            }
            // 2. If search is visible, hide search
            isSearchVisible -> {
                isSearchVisible = false
            }
            // 3. If back pressed once, show toast and start timeout
            backPressCount == 0 -> {
                backPressCount = 1
                Toast.makeText(ctx, "Click again to exit", Toast.LENGTH_SHORT).show()
                coroutineScope.launch {
                    delay(2000)
                    backPressCount = 0
                }
            }
            // 4. If back pressed twice within timeout, dismiss dialog
            else -> {
                onDismiss()
            }
        }
    }

    // Helper composable to detect keyboard visibility
   /* @Composable
    fun isKeyboardOpen(): Boolean {
        val ime = WindowInsets.ime
        val imeVisible = ime.getBottom(LocalDensity.current) > 0
        return imeVisible
    }*/

    LaunchedEffect(currentMatchPosition) {
        currentMatchPosition?.let { (rowIndex, colIndex) ->
            coroutineScope.launch {
                listState.animateScrollToItem(rowIndex, scrollOffset = 0)
                val scrollToPx = with(density) {
                    columnWidths.take(colIndex).sumOf { it.toPx().toInt() }
                }
                horizontalScroll.animateScrollTo(scrollToPx)
            }
        }
    }

    LaunchedEffect(debouncedQuery.value, matchedPositions.value) {
        if (debouncedQuery.value.isNotBlank() && matchedPositions.value.isEmpty()) {
            Toast.makeText(ctx, "not found or invalid query", Toast.LENGTH_SHORT).show()
        }
    }

    fun goToNextMatch() {
        if (matchedPositions.value.isNotEmpty()) {
            currentMatchIndex = (currentMatchIndex + 1) % matchedPositions.value.size
        }
    }

    fun goToPreviousMatch() {
        if (matchedPositions.value.isNotEmpty()) {
            currentMatchIndex =
                (currentMatchIndex - 1 + matchedPositions.value.size) % matchedPositions.value.size
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DebugStatusBar(
            searchQuery = searchQuery,
            onSearchQueryChange = {
                searchQuery = it
                currentMatchIndex = 0
            },
            isSearchVisible = isSearchVisible,
            onToggleSearch = { isSearchVisible = !isSearchVisible },
            onNextMatch = { goToNextMatch() },
            onPreviousMatch = { goToPreviousMatch() }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            state = listState
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScroll)
                ) {
                    listOf(
                        "ID",
                        "English",
                        "EnglishExtra",
                        "Chokri",
                        "ChokriMeaning",
                        "ChokriExtra",
                        "SubmittedBy",
                        "PartOf"
                    ).forEachIndexed { i, header ->
                        Text(
                            header,
                            Modifier
                                .width(columnWidths[i])
                                .padding(horizontal = 4.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            itemsIndexed(viewModel.items) { index, item ->
                val fields = itemFields(item)
                val isMatch = currentMatchPosition?.first == index
                val matchedCol = if (isMatch) currentMatchPosition.second else -1
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScroll)
                        .padding(vertical = 4.dp)
                ) {
                    fields.forEachIndexed { colIndex, field ->
                        HighlightedText(
                            text = field,
                            query = debouncedQuery.value,
                            modifier = Modifier
                                .width(columnWidths[colIndex])
                                .padding(horizontal = 4.dp),
                            isMatch = isMatch && colIndex == matchedCol,
                            isFocused = isMatch && colIndex == matchedCol
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HighlightedText(
    text: String,
    query: String,
    modifier: Modifier = Modifier,
    isMatch: Boolean,
    isFocused: Boolean
) {
    val backgroundColor = when {
        isFocused -> Color.Cyan
        isMatch -> Color.Yellow
        else -> Color.Transparent
    }

    val annotated = remember(text, query, backgroundColor) {
        if (query.isBlank() || !isMatch) AnnotatedString(text)
        else {
            val lowerText = text.lowercase()
            val lowerQuery = query.lowercase()
            val matchIndex = lowerText.indexOf(lowerQuery)
            if (matchIndex == -1) {
                AnnotatedString(text)
            } else {
                buildAnnotatedString {
                    append(text.substring(0, matchIndex))
                    withStyle(SpanStyle(background = backgroundColor)) {
                        append(text.substring(matchIndex, matchIndex + query.length))
                    }
                    append(text.substring(matchIndex + query.length))
                }
            }
        }
    }

    Text(text = annotated, modifier = modifier)
}


// for search highlights
@Composable
fun DebugStatusBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchVisible: Boolean,
    onToggleSearch: () -> Unit,
    onNextMatch: () -> Unit,
    onPreviousMatch: () -> Unit,
) {
    val ctx = LocalContext.current
    // db version for debug
    val db = ChokriDatabase.create(ctx)
    val dbClassName = db.javaClass.simpleName
    // State to hold the DB version, nullable Int
    val dbVersion = remember { mutableStateOf<Int?>(null) }
    // Read version once when db is created
    LaunchedEffect(db) {
        dbVersion.value = db.openHelper.readableDatabase.version
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Debug Mode:_DB_${dbClassName}_|_Version-${dbVersion.value ?: "Loading..."}",
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 0.dp)
            )
            IconButton(
                onClick = onToggleSearch,
                modifier = Modifier.padding(top = 18.dp)
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.search),
                    tint = Color.White
                )
            }
        }
        if (isSearchVisible) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreviousMatch) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous"
                    )
                }
                IconButton(onClick = onNextMatch) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next")
                }
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    label = { Text(stringResource(R.string.search)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
