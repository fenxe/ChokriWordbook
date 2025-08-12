package open.sesame.wordbook.ui

import android.app.Application
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceAtMost
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import open.sesame.wordbook.R.drawable
import open.sesame.wordbook.R.string
import open.sesame.wordbook.data.ChokriDatabase
import open.sesame.wordbook.data.DebugViewModel
import open.sesame.wordbook.data.DebugViewModelFactory
import open.sesame.wordbook.data.WordBookViewModel
import open.sesame.wordbook.data.WordDetail
import open.sesame.wordbook.data.dummy.AnimateTextInOut
import open.sesame.wordbook.data.dummy.ParticleExplosion
import open.sesame.wordbook.data.dummy.PlainTooltips
import open.sesame.wordbook.data.dummy.SnacksPop
import open.sesame.wordbook.data.dummy.StoryView
import open.sesame.wordbook.data.dummy.UnlockDeBugDialog
import open.sesame.wordbook.data.dummy.showOrHideKeyboard
import open.sesame.wordbook.ui.page.AppSocialPage
import open.sesame.wordbook.ui.page.DeBuggyDialog
import open.sesame.wordbook.ui.page.inappmail.MailScreenViewDialog
import open.sesame.wordbook.ui.page.inappmail.MailViewModel

@Composable
fun MainScreen(viewModel: WordBookViewModel, mailViewModel: MailViewModel) {
    var selectedItem by rememberSaveable { mutableStateOf("Wordbook") }

    MainListView(
        viewModel = viewModel,
        selectedItem = selectedItem,
        mailViewModel = mailViewModel,
        onItemSelected = { selectedItem = it })
}

@Composable
fun Float.toDp(): Dp = with(LocalDensity.current) { this@toDp.toDp() }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainListView(
    viewModel: WordBookViewModel,
    mailViewModel: MailViewModel = viewModel(),
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    val ctx = LocalContext.current
    // copy json
//    val assetFileName = "mail.json"
//    val internalFileName = "mail.json"
    val jsonFile = "mail.json"  // There! shorten
    val hasUnreadMail by mailViewModel.hasUnreadMail.collectAsState()
    // On first composition, copy asset and load mails on IO thread, update state on main thread

    LaunchedEffect(Unit) {
        mailViewModel.loadMails(ctx, jsonFile)
    }

    // Save json mails to internal file whenever mailItems changes (not empty)
    /*    LaunchedEffect(mailItem) {
            if (mailItem.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    writeMailItemsToFile(ctx, jsonFile, mailItem)
                }
            }
        }*/
    // Pass unread mails to viewmodel for other screens
    /*  LaunchedEffect(mailItem) {
          mailViewModel.updateUnreadStatus(mailItem)
      }*/


    // UI state
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(-1) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Collect all word lists at once
    val data = viewModel.collectWords()
    val atoz = data.english


    // Top bar scroll behavior
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // Top bar selection spinner
    var expanded by remember { mutableStateOf(false) }
    val items = remember { listOf("Wordbook", "Story") }
    var anchorPosition by remember { mutableStateOf<Rect?>(null) }

    // Search word function
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    // About App dialog
    var showAppSocialDialog by rememberSaveable { mutableStateOf(false) }

    // Sort icon toolbar
    var isSortedWord by rememberSaveable { mutableStateOf(false) }

    // de-bug view model initialize
    val application = LocalContext.current.applicationContext as Application
    val debugFactory = remember { DebugViewModelFactory(application) }
    val debugViewModel: DebugViewModel = viewModel(factory = debugFactory)

    // Mail view
    var showMailWindow by remember { mutableStateOf(false) }
    // mail page validator
    if (showMailWindow) {
        MailScreenViewDialog(
            onDismiss = { showMailWindow = false },
            mailViewModel = mailViewModel,
        )
    }

    // ✅ Intercept back press when in StoryView
    if (selectedItem == "Story") {
        BackHandler {
            onItemSelected("Wordbook")  // Switch back to Wordbook view
        }
    }

    Scaffold(
//        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumTopAppBar(
                navigationIcon = {

                    Box(modifier = Modifier.size(48.dp)) {
                        PlainTooltips(
                            plainTooltipText = "Inbox",
                        ) {
                            IconButton(onClick = {
                                showMailWindow = true
                            }) {
                                Icon(Icons.Default.Email, contentDescription = "Inbox, WIP")
                            }
                            if (hasUnreadMail) {
                                Box(
                                    modifier = Modifier
                                        .padding(top = 12.dp, end = 11.dp)
                                        .size(6.dp)
                                        .background(Color.Red, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    }

                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { expanded = true }
                            .onGloballyPositioned { coordinates ->
                                anchorPosition = coordinates.boundsInWindow()
                            }) {
                        Text(text = selectedItem)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown Arrow",
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }

                }, actions = {
                    // Search icon with explosion animation
                    var explodeParticles by remember { mutableStateOf(false) }
                    // Search icon
                    // Show Search icon only when search bar is not active
                    PlainTooltips(plainTooltipText = stringResource(string.search)) {


                        Row {


                            // Real Search icon here xD

                            // Search icon placeholder
                            Box(modifier = Modifier.size(48.dp)) {
                                // Particle explosion overlay
                                ParticleExplosion(
                                    explode = explodeParticles,
                                    modifier = Modifier.align(Alignment.Center),
                                    onAnimationEnd = {
                                        explodeParticles = false
                                    })
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = !isSearching && selectedItem != "Story",
                                    enter = fadeIn(animationSpec = tween(400, delayMillis = 200)),
                                    exit = fadeOut() + scaleOut(targetScale = 4f) + slideOutVertically { -100 },
                                ) {

                                    IconButton(
                                        onClick = {
                                            explodeParticles = true
                                            isSearching = true
                                        }
                                    ) {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = stringResource(id = string.search),
                                            modifier = Modifier.graphicsLayer(rotationZ = 360f)
                                        )
                                    }
                                }
                            }
                        }

                    }
                    // Sort word icon hide is story selected
                    if (selectedItem != "Story") {
                        PlainTooltips(plainTooltipText = stringResource(string.sort)) {
                            IconButton(onClick = { isSortedWord = !isSortedWord }) {
                                val rotary by animateFloatAsState(
                                    targetValue = if (isSortedWord) 180f else 0f
                                )
                                Icon(
                                    painterResource(drawable.outline_sort_az),
                                    contentDescription = stringResource(
                                        string.sort
                                    ),
                                    modifier = Modifier.rotate(rotary)
                                )
                            }
                        }
                    }

                    // About app dialog
                    PlainTooltips(plainTooltipText = stringResource(string.about)) {
                        IconButton(onClick = { showAppSocialDialog = true }) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = stringResource(string.about)
                            )
                        }
                    }
                }, scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),

                modifier = Modifier
//                    .graphicsLayer { alpha = topBarAlpha.value }
                    .zIndex(1f)
            )
        },

        )

    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // ✅ Try to correctly placed DropdownMenu here, may change to m3 dropdown list later
            anchorPosition?.let { anchor ->
                DropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }, offset = DpOffset(
                        x = anchor.left.toDp(), y = anchor.bottom.toDp()
                    )
                ) {
                    items.forEach { label ->
                        DropdownMenuItem(text = { Text(label) }, onClick = {
                            onItemSelected(label)
                            expanded = false
                        })
                    }
                }
            }

            // Show search bar only on Wordbook screen
            if (isSearching && selectedItem == "Wordbook") {
                AnimatedVisibility(             // can't get off this animator huhu
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search words...") },
                        trailingIcon = @Composable {

                            Row {
                                val showOrHideKeyboard = showOrHideKeyboard()
                                TextButton(
                                    onClick = { searchQuery = "" }) {
                                    Text(text = stringResource(string.clear))
                                }
                                IconButton(
                                    onClick = {
                                        showOrHideKeyboard()
                                    },
                                ) {
                                    Icon(
                                        painter = painterResource(drawable.keyboard),
                                        contentDescription = stringResource(
                                            string.keyboard
                                        )
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        isSearching = !isSearching
                                        searchQuery = ""
                                    },
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Close",
                                    )
                                }
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }

            // about/social page validator
            if (showAppSocialDialog) {
                AppSocialPage(onDismiss = { showAppSocialDialog = false })
            }

            // Main screen switcher with sorting
            // derivedstate for snappy smooth!
            val baseList by remember(
                atoz,
                isSortedWord
            ) { derivedStateOf { if (isSortedWord) atoz.sortedDescending() else atoz.sorted() } }

//            Crossfade(targetState = selectedItem, label = "ScreenSwitcher") { screen ->
            val filteredListState by remember(baseList, searchQuery) {
                derivedStateOf {
                    if (searchQuery.isEmpty()) baseList
                    else baseList.filter { it.contains(searchQuery, ignoreCase = true) }
                }
            }
            val filteredList = filteredListState

            when (selectedItem) {
                "Story" -> StoryView()
                "Wordbook" -> if (searchQuery.isNotEmpty() && filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(stringResource(string.dummy_text1))
                    }
                } else {
                    val listState = rememberLazyListState()

                    WordbookList(
                        wordList = filteredList,
//                        selectedIndex = selectedIndex,    // dunno why i put this
                        onSelect = { clickedWord ->
                            selectedIndex = atoz.indexOf(clickedWord)
                            openBottomSheet = selectedIndex != -1
                        }, scrollBehavior = scrollBehavior,
                        listState = listState
                    )
                }
            }
        }

        if (selectedItem == "Wordbook" && openBottomSheet && selectedIndex in atoz.indices) {
            val detail = WordDetail(
                english = data.english.safeGet(selectedIndex),
                chokri = data.chokri.safeGet(selectedIndex),
                meaning = data.meaning.safeGet(selectedIndex),
                enExtra = data.enExtra.safeGet(selectedIndex),
                ckExtra = data.ckExtra.safeGet(selectedIndex),
                submitter = data.submitter.safeGet(selectedIndex),
                partOf = data.partOf.safeGet(selectedIndex)
            )

            BottomSheetSample(
                detail = detail, sheetState = sheetState, onDismiss = {
                    scope.launch {
                        sheetState.hide()
                        openBottomSheet = false
                    }
                },
                debugViewModel = debugViewModel
            )
        }
    }
}

//}         // crossfade block only with "screen" on  when (selectedItem) block


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordbookList(
    wordList: List<String>,
//    selectedIndex: Int,
    onSelect: (String) -> Unit, scrollBehavior: TopAppBarScrollBehavior,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp)          // to show more space for word list
            .nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        itemsIndexed(wordList, key = { _, word -> word }) { idx, word ->
            val isLastItem = idx == wordList.lastIndex
            val shape = if (isLastItem) {
                RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            } else {
                RoundedCornerShape(16.dp)
            }
            Card(
                shape = shape,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isLastItem) 8.dp else 0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                onClick = { onSelect(word) }
            ) {
                Text(
                    text = word,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetSample(
    debugViewModel: DebugViewModel,
    detail: WordDetail, sheetState: SheetState, onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss, sheetState = sheetState,
        dragHandle = {
            if (sheetState.currentValue == SheetValue.Expanded) {
                null
            } else {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "null")
            }
        },
        content = {
            val expanded = sheetState.currentValue == SheetValue.Expanded
            // track if sheet is opened and expanded
            val sheetOpenedExpanded =
                sheetState.isVisible && sheetState.currentValue == SheetValue.Expanded

            val textToShow = if (expanded) detail.chokri else detail.english
            // text size test
            val tUnitSaver = Saver<TextUnit, Float>(
                save = { it.value },
                restore = { it.sp }
            )
            val minSizeText = 8.sp
            val maxSizeText = 69.sp     // -_-
            var textSize by rememberSaveable(stateSaver = tUnitSaver) { mutableStateOf(45.sp) } // starting size
            val ctx = LocalContext.current

            // sesas
            var showDebugDialog by remember { mutableStateOf(false) }

            // Debug dialog
            var showDebugWindow by remember { mutableStateOf(false) }
            // debug page validator
            if (showDebugWindow) {
                DeBuggyDialog(onDismiss = { showDebugWindow = false }, debugViewModel)
            }

            // db version when sharing
            val db = ChokriDatabase.create(ctx)
            // State to hold the DB version, nullable Int
            val dbVersion = remember { mutableStateOf<Int?>(null) }
            // Read version once when db is created
            LaunchedEffect(db) {
                dbVersion.value = db.openHelper.readableDatabase.version
            }

            SnacksPop { snack ->
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(10.dp)
                ) {
                    AnimatedVisibility(visible = expanded) {
                        // Added surface to give top bar more pleasant view
                        Surface(
                            shape = RoundedCornerShape(
                                bottomStart = 16.dp, bottomEnd = 16.dp
                            )
                        ) {
                            TopAppBar(
                                modifier = Modifier.height(32.dp), // Since modalbottomsheet has a problem with appbar,
                                // we need this. Refer to TopAppBar(WindowInsets),
                                // more like insects?! Show some love right there!
                                windowInsets = WindowInsets(top = 0.dp), // fucking birch, this took me 6.5 hours to debug!
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.primary
                                ), title = {
                                    Text(
                                        "Details", modifier = Modifier.offset(y = 4.dp)
                                    )
                                }, // don't nag when it works
                                navigationIcon = {
                                    IconButton(onClick = onDismiss) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = stringResource(string.close)
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = {
                                        showDebugDialog =
                                            true // sheetState remains untouched here, LaunchedEffect will keep it expanded

                                    }) {
                                        Icon(
                                            painterResource(drawable.outline_bug),
                                            contentDescription = "Debug"
                                        )
                                    }
                                    IconButton(onClick = {
                                        val pManager =
                                            ctx.packageManager.getPackageInfo(ctx.packageName, 0)
                                        val vName = pManager.versionName
                                        val vCode = pManager.longVersionCode
                                        val appName = ctx.getString(string.app_name)

                                        val sendIntent: Intent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TITLE, "Share word")
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "${detail.english} | ${detail.chokri} " +
                                                        "\n-${detail.meaning}\n" +
                                                        "(AP_DEV_TEST_${appName}_${vName}.${vCode}_DATABASE_V_${dbVersion.value})"
                                            )
                                            type = "text/plain"
                                        }
                                        val shareIntent = Intent.createChooser(sendIntent, "APP")
                                        ctx.startActivity(shareIntent)
                                    }) {
                                        Icon(
                                            Icons.Default.Share,
                                            contentDescription = "Share text by clipboard"
                                        )
                                    }
                                    IconButton(onClick = {
                                        //HERE
                                        textSize =
                                            (textSize.value - 2).fastCoerceAtLeast(minSizeText.value).sp
                                    }) {
                                        Icon(
                                            painterResource(drawable.outline_text_decrease_24),
                                            contentDescription = stringResource(
                                                string.decrease_text_size
                                            )
                                        )
                                    }
                                    IconButton(onClick = {
                                        // HERE
                                        textSize =
                                            (textSize.value + 2).fastCoerceAtMost(maxSizeText.value).sp
                                    }) {
                                        Icon(
                                            painterResource(drawable.outline_text_increase_24),
                                            contentDescription = stringResource(
                                                string.increase_text_size
                                            )
                                        )
                                    }

                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = textToShow,
                        onValueChange = { it },
                        readOnly = true,
                        textStyle = MaterialTheme.typography.displayLarge.copy(
                            textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(if (expanded) "Chokri" else "English")
//                            Text("English")
                        })

                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (textToShow == detail.chokri) detail.english else detail.chokri,
                        style = MaterialTheme.typography.displayMedium,
                    )    //inverse logic shit
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                    Spacer(Modifier.height(12.dp))
                    AnimateTextInOut(
                        detail.meaning, style = MaterialTheme.typography.displaySmall,
                        baseTextSize = textSize,
                        lineHeight = textSize * 1.1f
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = stringResource(string.example_thaka),
                        textDecoration = TextDecoration.Underline,
                        fontStyle = FontStyle.Italic,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(detail.ckExtra, style = MaterialTheme.typography.headlineMedium)

                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = detail.ckExtra, style = MaterialTheme.typography.headlineMedium,
                    )

                    /*Spacer(Modifier.height(16.dp))
                    RainbowOutlineTextButton(
                        onClick = onDismiss, modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .wrapContentHeight()
                    ) {
                        Text("Close Page")
                    }*/
                }
            }
            UnlockDeBugDialog(
                showDialog = showDebugDialog,
                iconResId = drawable.spider_vector,
                label = { Text("Ali Baba and the Forty Thieves") },
                onDismiss = { showDebugDialog = false },  // only hide dialog, don't dismiss sheet
                unlockPhrase = "open sesame",   // don't dare to open Pandora Box :P
                onUnlock = {
                    showDebugWindow = true
                    if (sheetOpenedExpanded) {
                        sheetOpenedExpanded
                    }
                    showDebugDialog = false
                }
            )
            LaunchedEffect(showDebugDialog) {
                if (showDebugDialog && sheetState.currentValue != SheetValue.Expanded) {
                    sheetState.expand()
                }
            }

        })


}
// TODO: when opened keyboard in unlock secret key dialog, the model sheet full page stays after full blown page debuggy exits
// TODO: example below
/*
val isKeyboardOpen by keyboardAsState()
LaunchedEffect(isKeyboardOpen) {
    if (isKeyboardOpen && sheetState.currentValue != SheetValue.Expanded) {
        sheetState.expand() } }
@Composable
fun keyboardAsState(): State<Boolean> {
    val keyboardState = remember { mutableStateOf(false) }
    val insets = LocalWindowInsets.current
    LaunchedEffect(insets) {
        keyboardState.value = insets.ime.isVisible }
    return keyboardState
}
*/



