package open.sesame.wordbook.data.dummy

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import open.sesame.wordbook.R
import open.sesame.wordbook.R.string
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SnacksPop(
    content: @Composable (SnackbarShower) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val snackbarShower = remember { SnackbarShower(snackbarHostState, scope) }

    Scaffold(
        snackbarHost = {
            ReusableSnackbarHost(snackbarHostState = snackbarHostState)
        }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            content(snackbarShower)
        }

    }
}

// the name says it
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun showOrHideKeyboard(): () -> Unit {
    val keyboardController = LocalSoftwareKeyboardController.current
    val isKeyboardVisible = WindowInsets.isImeVisible
    return {
        if (isKeyboardVisible) {
            keyboardController?.hide()
        } else {
            keyboardController?.show()
        }
    }
}

// reusable compose
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainTooltips(
    modifier: Modifier = Modifier, plainTooltipText: String, content: @Composable () -> Unit
) {
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip { Text(plainTooltipText) }
        },
        state = rememberTooltipState(),
        content = content
    )
}

// Squigglies. Just use it anywhere!
@Composable
fun SquigglyDivider() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
    ) {
        val waveAmplitude = 10f
        val waveFrequency = 2f
        val path = Path().apply {
            moveTo(0f, size.height / 2)
            for (x in 0 until size.width.toInt()) {
                val normalizedX = x / size.width
                val y =
                    waveAmplitude * sin(normalizedX * waveFrequency * 2 * PI).toFloat() + size.height / 2
                lineTo(x.toFloat(), y)
            }
        }
        drawPath(path, color = Color.Gray, style = Stroke(width = 2f))
    }
}

// spacer Small , Medium, Large re-composable
enum class Draw { S, M, L }

@Composable
fun SpacerToy(size: Draw) {
    val h = when (size) {
        Draw.S -> 8.dp
        Draw.M -> 16.dp
        Draw.L -> 24.dp
    }
    Spacer(modifier = Modifier.height(h))
}

// Text with leading icon TiL
@Composable
fun TextImageLine(
    i: Int, t: String,  // image
    url: String? = null, // Optional URL to open
    tm: String? = null, // Optional toast message
    appInvitesOrUrl: String? = null, // if the social has android app
) {
    val ctx = LocalContext.current
    val painter = painterResource(id = i) // Convert Int to Painter
    val pkgManager = ctx.packageManager     // to open apps
    val telegramPkgs = listOf(
        "org.telegram.messenger",       // Official Telegram
        "org.thunderdog.challegram",    // Telegram X
        "org.telegram.plus",            // Plus Messenger
        "org.bgram.messenger",          // BGram
        "tw.nekomimi.nekogramx",        // NekoGram X
        "org.forkgram.messenger",        // Forkgram
        "app.nicegram"                // Nicegram
    )
    val whatsAppPkgs = listOf("com.whatsapp", "com.whatsapp.w4b")

    Row(
        modifier = Modifier.clickable {
            val intent = when {
                //Original app first
                appInvitesOrUrl in whatsAppPkgs ||
                        appInvitesOrUrl in telegramPkgs -> {
                    (whatsAppPkgs + telegramPkgs).firstNotNullOfOrNull {
                        pkgManager.getLaunchIntentForPackage(it)
                    }
                }
                //If appInvitesOrUrl is a deep link (not a package name), open it as a URL
                appInvitesOrUrl?.contains("://") == true -> {
                    Intent(Intent.ACTION_VIEW, appInvitesOrUrl.toUri())
                }

                //Or other similar app
                appInvitesOrUrl != null -> {
                    pkgManager.getLaunchIntentForPackage(appInvitesOrUrl)
                }
                //Otherwise internet link
                url != null -> {
                    Intent(Intent.ACTION_VIEW, url.toUri())
                }

                else -> null
            }
            intent?.let {
                ctx.startActivity(it)
            } ?: tm?.let {
                Toast.makeText(ctx, "", Toast.LENGTH_SHORT).show()
            }
        }, verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(24.dp)
                .padding(end = 4.dp),
        )
        SpacerToy(Draw.S)
        Text(t)
    }
}

// Get version name and code
@Composable
fun AppVersion(modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val pI = ctx.packageManager.getPackageInfo(ctx.packageName, 0)

    val vn = try {
        pI.versionName ?: "UnknownVersion"
    } catch (e: Exception) {
        PlainTooltips(modifier = Modifier, plainTooltipText = "$e ERRRR") { }
    }
    val vc = pI.longVersionCode

    Text(
        text = stringResource(string.version, vn, vc),
        style = MaterialTheme.typography.labelSmall,
        fontStyle = FontStyle.Italic,
        modifier = modifier
    )
}

// d
@Composable
fun DraggableTextBox(
    modifier: Modifier = Modifier,
    boxColor: Color = MaterialTheme.colorScheme.tertiaryContainer,
    outlineColor: Color = Color.Black,
    outlineWidth: Dp = 2.dp,
    cornerRadius: Dp = 12.dp,
    contentPadding: Dp = 12.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Glitch animation values
    val glitchOffset1 = remember {
        Animatable(
            Offset.Zero, Offset.VectorConverter
        )
    }
    val glitchOffset2 = remember {
        Animatable(
            Offset.Zero, Offset.VectorConverter
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            glitchOffset1.animateTo(
                Offset(
                    Random.nextInt(-5, 5).toFloat(), Random.nextInt(-5, 5).toFloat()
                ), animationSpec = tween(50)
            )
            glitchOffset2.animateTo(
                Offset(
                    Random.nextInt(-5, 5).toFloat(), Random.nextInt(-5, 5).toFloat()
                ), animationSpec = tween(50)
            )
            delay(50)
        }
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX = (offsetX + dragAmount.x).coerceIn(-250f, 250f)
                    offsetY = (offsetY + dragAmount.y).coerceIn(-400f, 400f)
                }
            }) {
        // Glitch background layers
        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationX = glitchOffset1.value.x
                    translationY = glitchOffset1.value.y
                    scaleX = 1.02f   // 🔥 Make glitch layer bigger
                    scaleX = 1.02f
                }
                .border(outlineWidth, Color.Cyan, RoundedCornerShape(cornerRadius))
                .background(Color.Red.copy(alpha = 0.3f), RoundedCornerShape(cornerRadius)))

        Box(
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    translationX = glitchOffset2.value.x
                    translationY = glitchOffset2.value.y
                    scaleX = 1.02f
                    scaleY = 1.01f
                }
                .border(outlineWidth, Color.Magenta, RoundedCornerShape(cornerRadius))
                .background(Color.Blue.copy(alpha = 0.3f), RoundedCornerShape(cornerRadius)))

        // Main text box
        Box(
            modifier = Modifier
                .border(outlineWidth, outlineColor, RoundedCornerShape(cornerRadius))
                .background(boxColor, RoundedCornerShape(cornerRadius))
                .padding(contentPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.Start,
                content = content
            )
        }
    }
}

@Composable
fun HorizontalLine() {
    HorizontalDivider(modifier = Modifier, thickness = 1.dp, color = DividerDefaults.color)
}

// Particle Explosion
@Composable
fun ParticleExplosion(
    explode: Boolean, modifier: Modifier = Modifier, onAnimationEnd: () -> Unit = {}
) {
    val particles = remember {
        List(12) {
            Animatable(Offset(0f, 0f), Offset.VectorConverter)
        }
    }
    val alpha = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(explode) {
        if (explode) {
            // Phase 1: Explosion
            particles.forEachIndexed { index, anim ->
                val angle = (360f / particles.size) * index
                val radians = Math.toRadians(angle.toDouble())
                val dx = cos(radians).toFloat() * 200f
                val dy = sin(radians).toFloat() * 200f

                scope.launch {
                    anim.animateTo(
                        Offset(dx, dy), animationSpec = tween(900, easing = FastOutSlowInEasing)
                    )
                }
            }
            // Fade out
            alpha.animateTo(0f, animationSpec = tween(900))

            // Phase 2: Return
            particles.forEach { anim ->
                scope.launch {
                    anim.animateTo(
                        Offset(0f, 0f), animationSpec = tween(900, easing = FastOutSlowInEasing)
                    )
                }
            }

            // Fade in
            alpha.animateTo(1f, animationSpec = tween(900))

            onAnimationEnd()
        }
    }

    Canvas(modifier = modifier.size(48.dp)) {
        val shouldDraw = explode || alpha.value < 1f || particles.any { it.value != Offset(0f, 0f) }
        if (shouldDraw) {
            particles.forEach {
                drawCircle(
                    color = Color.Red.copy(alpha = alpha.value),
                    radius = 4f,
                    center = center + it.value
                )
            }
        }
    }
}

// Rainbow style outline
/**
 * A custom rainbow-outline button with animated gradient border and rounded background.
 *
 * Features:
 * - Animated rainbow border using an infinite gradient shift
 * - Rounded corner background
 * - Fully clickable area
 * - Customizable border width, corner radius, and padding
 * - Any Composable content can be placed inside (text, icons, etc.)
 *
 * @param onClick The action to perform when the button is clicked.
 * @param modifier Modifiers to adjust layout, size, alignment, etc.
 * @param borderWidth Thickness of the rainbow outline.
 * @param cornerRadius Rounding applied to both border and background.
 * @param contentPadding Padding around the button’s inner content.
 * @param content The inner content of the button, placed inside a Row.
 */
@Composable
fun RainbowOutlineTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
    bgColor: Color = Color.Transparent, // <- defaulted
    content: @Composable RowScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RainbowAnim")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "RainbowOffset"
    )

    Box(
        modifier = modifier
            .height(40.dp) // force visible height
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick)
            .background(bgColor) // so that we can configure anywhere
    ) {
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val strokePx = borderWidth.toPx()
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    ),
                    start = Offset(animatedOffset % size.width, 0f),
                    end = Offset((animatedOffset % size.width) + size.width, size.height)
                ),
                cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx()),
                style = Stroke(width = strokePx)
            )
        }

        Row(
            modifier = Modifier
//                .fillMaxSize()
                .padding(contentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimaryContainer) {
                content()
            }
        }
    }
}


//Animate outline with icon
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AnimatedOutlinedTextButtonWithTrail(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    buttonWidth: Dp = 180.dp,
    buttonHeight: Dp = 56.dp,
    borderColor: Color = Color.Gray,
    borderWidth: Dp = 2.dp,
    trailColor: Color = Color.Cyan.copy(alpha = 0.8f),
    trailLength: Int = 12,
    cornerRadius: Dp = 12.dp,
    textStyle: TextStyle = LocalTextStyle.current
) {
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition(label = "trailLoop")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "progress"
    )

    val trailPoints = remember { mutableStateListOf<Offset>() }
    var lastOffset by remember { mutableStateOf<Offset?>(null) }
    val minDistance = 8f // Distance in px between trail dots

    BoxWithConstraints(
        modifier = modifier.size(buttonWidth, buttonHeight)
    ) {
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        val perimeter = 2 * (widthPx + heightPx)
        val distance = progress * perimeter

        val currentOffset = when {
            distance <= widthPx -> Offset(distance, 0f)
            distance <= widthPx + heightPx -> Offset(widthPx, distance - widthPx)
            distance <= 2 * widthPx + heightPx -> Offset(
                2 * widthPx + heightPx - distance, heightPx
            )

            else -> Offset(0f, perimeter - distance)
        }

        // Sample every frame, but only add new point if it's far enough from the last one
        LaunchedEffect(currentOffset) {
            val last = lastOffset
            if (last == null || (currentOffset - last).getDistance() >= minDistance) {
                trailPoints.add(currentOffset)
                lastOffset = currentOffset
                if (trailPoints.size > trailLength) {
                    trailPoints.removeAt(0)
                }
            }
        }

        // Draw button
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            border = BorderStroke(borderWidth, borderColor),
            shape = RoundedCornerShape(cornerRadius),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(text = text, style = textStyle)
        }

        // Draw trail
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val cornerRadiusPx = with(density) { cornerRadius.toPx() }

            // Create a rounded rectangle path for clipping
            val clipPath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, 0f, size.width, size.height),
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                )
            }

            // Clip drawing to the rounded shape
            clipPath(clipPath) {
                trailPoints.forEachIndexed { index, point ->
                    val alpha = 1f - (index.toFloat() / trailPoints.size)
                    drawCircle(
                        color = trailColor.copy(alpha = alpha), radius = 6f, center = point
                    )
                }
            }
        }
    }
}

// Animate the text
@Composable
fun AnimateTextInOut(
    text: String,
    style: TextStyle,
    baseTextSize: TextUnit,
    lineHeight: TextUnit,
    stepsLimit: Int = 2,
) {
    var baseSize by remember { mutableStateOf(baseTextSize) }

    // Steps limited otherwise infinity
    val minSteps = (baseTextSize.value - stepsLimit).sp
    val maxSteps = (baseTextSize.value + stepsLimit).sp

    LaunchedEffect(baseTextSize) {
        baseSize = baseSize.value.coerceIn(minSteps.value, maxSteps.value).sp
    }

    AnimatedContent(
        targetState = baseTextSize, transitionSpec = {
            scaleIn(animationSpec = tween(500)).togetherWith(
                scaleOut(animationSpec = tween(500))
            )
        }) { txt ->
        Text(
            text = text, style = style, fontSize = txt, lineHeight = lineHeight
        )
    }

}

// get database version
/*@Composable
fun DatabaseVersionText(
    db: ChokriDatabase,
    modifier: Modifier = Modifier,
    loadingText: String = "Loading DB Version...",
    prefixText: String = "DB Version: "
) {
    val version = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(db) {
        version.value = db.openHelper.readableDatabase.version
    }

    Text(
        text = version.value?.let { "$prefixText$it" } ?: loadingText,
        modifier = modifier
    )
}*/

// ses
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockDeBugDialog(
    modifier: Modifier = Modifier,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    label: @Composable (() -> Unit),
    unlockPhrase: String = "open sesame",
    onUnlock: () -> Unit,
    iconResId: Int = R.drawable.ic_launcher_foreground,

    ) {
    var inputText by remember { mutableStateOf("") }

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = onDismiss
        ) {
            Surface(
                modifier = modifier
                    .widthIn(max = 300.dp)
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // bouncy animate
                    val bounceOffset = remember { Animatable(0f) }

                    LaunchedEffect(Unit) {
                        while (true) {
                            bounceOffset.animateTo(
                                targetValue = -30f,
                                animationSpec = tween(durationMillis = 500, easing = EaseInOut)
                            )
                            bounceOffset.animateTo(
                                targetValue = 30f,
                                animationSpec = tween(durationMillis = 500, easing = EaseInOut)
                            )
                        }
                    }

                    val spiderSize = 98.dp // The spider icon's total size
                    val threadBelowSpider =
                        18.dp          // ✅ Thread below spider's legs / make bottom line longer
                    val threadAboveSpider = 20.dp // ✅ Length of the thread above the spider's head
// If you want the thread to extend below the spider, you can add another variable for that

                    Box(contentAlignment = Alignment.TopCenter) {
                        // 🎯 Draw the dangling thread above the spider
                        Canvas(
                            modifier = Modifier
                                // Height = fixed threadAboveSpider + current bounce amount
                                // Increase threadAboveSpider for a longer "top" thread
                                // If you want to extend *below* the spider, add extra dp here
                                .height(threadAboveSpider + threadBelowSpider + bounceOffset.value.dp)
                                .width(2.dp) // thickness of the thread
                        ) {
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.6f), start = Offset(
                                    size.width / 2, 0f
                                ),         // Line starts at very top of canvas
                                end = Offset(
                                    size.width / 2, size.height
                                ),  // Line ends at bottom of canvas
                                strokeWidth = 2f, cap = StrokeCap.Round
                            )
                        }

                        // 🕷 Spider icon (moves up & down)
                        Icon(
                            painter = painterResource(id = iconResId),
                            contentDescription = "Dialog Icon",
                            modifier = Modifier
                                .size(spiderSize)
                                .offset(y = bounceOffset.value.dp), // moves spider vertically
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Show caution text ONLY when unlock is available
                    if (inputText.trim().equals(unlockPhrase, ignoreCase = true)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "- VOLATILE -\n⚠️USE WITH CAUTION⚠️",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    SpacerToy(Draw.M)

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = {
                            inputText = it
                        },
                        placeholder = { Text("🔒 Type the magic word...") },
                        singleLine = true,
                        label = label,
                        colors = TextFieldDefaults.colors()
                    )

                    SpacerToy(Draw.L)

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (inputText.trim().equals(unlockPhrase, ignoreCase = true)) {
                            // ✅ Show rainbow button instead of close
                            RainbowOutlineTextButton(
                                onClick = {
                                    onUnlock()
                                },
                            ) {
                                Text(text = "Unlock", modifier = Modifier.offset(y = 4.dp))
                            }
                        } else {
                            // ❌ Only show Close button when phrase not correct
                            AnimatedOutlinedTextButtonWithTrail(
                                onClick = onDismiss,
                                text = stringResource(string.close),
                                buttonWidth = 70.dp,
                                buttonHeight = 40.dp,
                            )
                        }
                    }

                }
            }
        }
    }
}

// Profile pic circle
@Composable
fun ProfilePic(
    @DrawableRes drawableRes: Int, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Box {
        Image(
            painter = painterResource(drawableRes),
            contentDescription = null,
            modifier = modifier
                .size(48.dp)
                .clip(CircleShape)  // to give a circled profile pic
                .clickable(onClick = onClick) // do something!
        )
    }
}


// List header for mail screen
@Composable
fun ListCardHeader(
    drawableRes: Int,
    mailNumber: String?,
    sender: String?,
    isRead: @Composable (() -> Unit)?,
    timestamp: String,
    onClick: () -> Unit,
    mNuberPaddingVertical: Dp? = null,
    mNuberPaddingAll: Dp? = null,
    mNuberBgStyle: Color? = null,
    size: Dp? = null,

    modifier: Modifier = Modifier
) {
    Row(modifier = Modifier) {
        ProfilePic(
            drawableRes = drawableRes, onClick = onClick
        )
        Box {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp, vertical = 8.dp
                    ), verticalArrangement = Arrangement.Center
            ) {
                if (sender != null) {
                    Text(text = sender, style = MaterialTheme.typography.labelMedium)
                }
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            /*            if (isRead != null) {

                            Text(
                                text = "[ $isRead ]",
                                modifier = Modifier
                                    .offset(y = (-15).dp, x = 10.dp)
                                    .background(
                                        if (isRead == "Finished reading") Color.Green else Color.Red
                                    )
                                    .align(Alignment.TopEnd),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isRead == "Finished reading") Color.Black else Color.White,
                                    fontWeight = FontWeight.Light
                                )

                            )

                        }*/
            isRead?.let {
                Box(
                    modifier = Modifier
                        .offset(y = (-15).dp, x = 10.dp)
                        .align(Alignment.TopEnd)
                )
                {
                    it()
                }
            }
            Text(
                text = mailNumber.toString(),
                modifier = Modifier
                    .padding(vertical = 8.dp)
//                    .then(
//                        if (mNuberPaddingVertical != null)
//                        Modifier.padding(vertical = mNuberPaddingVertical)
//                        else Modifier,
//                    )
//                    .then(
//                        if (mNuberPaddingAll !=null)
//                            Modifier.padding(mNuberPaddingAll)
//                        else Modifier
//                    )
                    .then(
                        if (mNuberBgStyle !=null)
                            Modifier.background(mNuberBgStyle, CircleShape)
                        else Modifier
                    )
//                    .then(
//                        if (size !=null)
//                            Modifier.size(size)
//                        else Modifier
//                    )
                    .size(24.dp)
//                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .align(Alignment.TopEnd)
                    .padding(2.dp)
                    ,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.W900, color = MaterialTheme.colorScheme.onError
                ),
                textAlign = TextAlign.Center
            )



        }
    }
}



