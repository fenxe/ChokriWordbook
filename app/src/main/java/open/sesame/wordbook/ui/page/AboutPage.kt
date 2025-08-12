package open.sesame.wordbook.ui.page

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import open.sesame.wordbook.R
import open.sesame.wordbook.R.string
import open.sesame.wordbook.data.dummy.AnimatedOutlinedTextButtonWithTrail
import open.sesame.wordbook.data.dummy.AppVersion
import open.sesame.wordbook.data.dummy.DraggableTextBox
import open.sesame.wordbook.data.dummy.Draw
import open.sesame.wordbook.data.dummy.RainbowOutlineTextButton
import open.sesame.wordbook.data.dummy.SpacerToy
import open.sesame.wordbook.data.dummy.TextImageLine
import java.io.BufferedReader
import java.io.InputStreamReader


@Composable
fun AppSocialPage(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var showToast by remember { mutableStateOf(false) }
    Dialog(
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnClickOutside = false,
            dismissOnBackPress = false // so user will click button to close
        ), onDismissRequest = onDismiss
    ) {
        // Intercept back press to show toast
        BackHandler {
            showToast = true
        }
        // Show toast from side effect
        LaunchedEffect(showToast) {
            if (showToast) {
                Toast.makeText(
                    context,
                    context.getString(string.click_on_close_to_close_window),
                    Toast.LENGTH_SHORT
                ).show()
                showToast = false
            }
        }

        Surface(
            color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(string.about),
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )

                RainbowOutlineTextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .height(32.dp),
                    cornerRadius = 50.dp
                ) {
                    Text(
                        text = stringResource(string.close), modifier = Modifier.padding(0.dp)
                    )
                }/*
                TextButton(
                    onClick = onDismiss,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 16.dp, top = 8.dp) // optional spacing
                ) {
                    Text(
                        text = stringResource(string.close),
                        textAlign = TextAlign.End
                    )
                }
                */
            }
            DraggableTextBox(modifier = Modifier) {
                Text(
                    text = stringResource(string.social_sns),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = Bold
                    )
                )
                SpacerToy(Draw.L)
                TextImageLine(
                    i = R.drawable.discord,
                    t = stringResource(string.discord),
                    tm = stringResource(string.under_construction)
                )
                TextImageLine(
                    i = R.drawable.facebook,
                    t = stringResource(string.facebook),
                    tm = stringResource(string.under_construction)
                )
                TextImageLine(
                    i = R.drawable.instagram,
                    t = stringResource(string.instagram),
                    tm = stringResource(string.under_construction)
                )
                TextImageLine(
                    i = R.drawable.twitter,
                    t = stringResource(string.twitter),
                    tm = stringResource(string.under_construction)
                )
                TextImageLine(
                    i = R.drawable.telegram,
                    t = stringResource(string.telegram),
                    appInvitesOrUrl = "https://t.me/+Y8nHcLFpmgViMzll",
                    url = "https://t.me/+Y8nHcLFpmgViMzll"
                )
                TextImageLine(
                    i = R.drawable.vk,
                    t = stringResource(string.vk),
                    tm = stringResource(string.under_construction)
                )
                TextImageLine(
                    i = R.drawable.whatsapp,
                    t = stringResource(string.whatsapp),
                    url = "https://chat.whatsapp.com/GVIq9AYed6LKxrWmIAfPmC?mode=ac_t",
                    appInvitesOrUrl = "https://chat.whatsapp.com/GVIq9AYed6LKxrWmIAfPmC?mode=ac_t"
                )
                TextImageLine(
                    i = R.drawable.youtube,
                    t = stringResource(string.youtube),
                    tm = stringResource(string.under_construction)
                )
                SpacerToy(Draw.M)
                Text(
                    text = stringResource(string.support_this_project),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = Bold, textDecoration = TextDecoration.Underline
                    )
                )
                TextImageLine(
                    i = R.drawable.ko_fi, t = stringResource(string.ko_fi), tm = stringResource(
                        string.under_construction
                    )
                )
                TextImageLine(
                    i = R.drawable.github, t = stringResource(string.github), url = stringResource(
                        string.github_repo_link
                    )
                )
                SpacerToy(Draw.S)
                AppVersion(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            SpacerToy(Draw.S)
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Column {
//                    ChangelogDialogButton()
                    SpacerToy(Draw.M)
                    Text(text = stringResource(string.made_with_love))

                }


            }
        }


    }
}

// Load changelog text file
fun loadChangelogText(ctx: Context): String {
    return try {
        val iStream = ctx.assets.open("changelog_proto.txt")
        val r = BufferedReader(InputStreamReader(iStream))
        r.readText()
    } catch (e: Exception) {
        Log.e("ChangelogError", "Failed to load changelog", e)
        "Error loading changelog.txt: ${e.message}" // ✅ Actually returning the string now
    }
}

// Load changelog text file - the main button
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogDialogButton() {
    val context = LocalContext.current
    var changelog by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val loadAndShowDialog = {
        changelog = loadChangelogText(context)
        showDialog = true
    }

    AnimatedOutlinedTextButtonWithTrail(
        onClick = loadAndShowDialog,
        text = stringResource(string.changelog),
        trailColor = Color.Cyan,
        modifier = Modifier
    )

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = { /**Do nothing*/ }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp, max = 440.dp)
                    .padding(start = 8.dp)
            ) {
                Column(modifier = Modifier.padding(top = 16.dp, end = 8.dp, start = 8.dp)) {
                    // ✅ Stationary title
                    Text(
                        text = stringResource(string.changelog),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // ✅ Scrollable content
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = changelog,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

//                    Spacer(modifier = Modifier.height(16.dp))

                    // ✅ Stationary button
                    RainbowOutlineTextButton(
                        onClick = { showDialog = false },
                        modifier = Modifier
                            .padding(8.dp)
//                            .background(MaterialTheme.colorScheme.onTertiaryContainer)
                            .align(Alignment.End)
                            .height(32.dp),
                        bgColor = MaterialTheme.colorScheme.tertiaryContainer,
                        cornerRadius = 50.dp
                    ) {
                        Text(stringResource(string.close), color = Color.Blue)
                    }
                }
            }
        }


    }
}

