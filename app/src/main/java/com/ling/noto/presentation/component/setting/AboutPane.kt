package com.ling.noto.presentation.component.setting

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Commit
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.ling.noto.R
import com.ling.noto.presentation.component.ConfettiEffect
import com.ling.noto.presentation.component.CurlyCornerShape
import com.ling.noto.presentation.util.rememberCustomTabsIntent

@Composable
fun AboutPane() {

    val context = LocalContext.current
    val customTabsIntent = rememberCustomTabsIntent()
    var showConfetti by rememberSaveable { mutableStateOf(false) }

    Column(
        Modifier
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val packageInfo = context.packageManager
            .getPackageInfo(context.packageName, 0)
        val version = remember(packageInfo) { packageInfo.versionName }
        var pressAMP by remember { mutableFloatStateOf(16f) }
        val animatedPress by animateFloatAsState(pressAMP)

        val haptic = LocalHapticFeedback.current

        Box(
            modifier = Modifier
                .size(240.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = CurlyCornerShape(curlAmplitude = animatedPress.toDouble()),
                )
                .shadow(
                    elevation = 10.dp,
                    shape = CurlyCornerShape(curlAmplitude = animatedPress.toDouble()),
                    ambientColor = MaterialTheme.colorScheme.primaryContainer,
                    spotColor = MaterialTheme.colorScheme.primaryContainer,
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            pressAMP = 0f
                            tryAwaitRelease()
                            haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                            pressAMP = 16f
                        },
                        onLongPress = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            showConfetti = true
                        }
                    )
                }
        ) {
            Image(
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                contentDescription = "Icon"
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.version) + " " + version,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        ListItem(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clip(CircleShape)
                .clickable {
                    customTabsIntent.launchUrl(
                        context, "https://github.com/ling0412/noto/issues".toUri()
                    )
                },
            colors = ListItemDefaults.colors()
                .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.BugReport, contentDescription = "bug"
                )
            },
            headlineContent = {
                Text(text = stringResource(R.string.report_a_bug_or_request_a_feature))
            })

        ListItem(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clip(CircleShape)
                .clickable {
                    customTabsIntent.launchUrl(
                        context, "https://github.com/ling0412/noto".toUri()
                    )
                },
            colors = ListItemDefaults.colors()
                .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.Commit, contentDescription = "code"
                )
            },
            headlineContent = {
                Text(text = stringResource(R.string.source_code))
            })

        ListItem(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clip(CircleShape)
                .clickable {
                    customTabsIntent.launchUrl(
                        context,
                        "https://github.com/ling0412/noto/blob/main/Guide.zh.md".toUri()
                    )
                },
            colors = ListItemDefaults.colors()
                .copy(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.TipsAndUpdates, contentDescription = "Guide"
                )
            },
            headlineContent = {
                Text(text = stringResource(R.string.guide))
            })

        Spacer(Modifier.navigationBarsPadding())
    }

    if (showConfetti) {
        ConfettiEffect()
    }
}
