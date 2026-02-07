package com.ling.noto.presentation.component.setting

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Http
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Switch
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ling.noto.R
import com.ling.noto.presentation.component.dialog.ProgressDialog
import com.ling.noto.presentation.event.DatabaseEvent
import com.ling.noto.presentation.util.Constants
import com.ling.noto.presentation.viewmodel.SharedViewModel

@Composable
fun CloudPane(sharedViewModel: SharedViewModel) {
    val webDAVUrlState = rememberTextFieldState()
    val webDAVAccountState = rememberTextFieldState()
    val webDAVPasswordState = rememberTextFieldState()
    var showWebDAVPassword by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val settingsState by sharedViewModel.settingsStateFlow.collectAsStateWithLifecycle()
    val actionState by sharedViewModel.dataActionStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(settingsState.webdavUrl, settingsState.webdavUsername, settingsState.webdavPassword) {
        webDAVUrlState.setTextAndPlaceCursorAtEnd(settingsState.webdavUrl)
        webDAVAccountState.setTextAndPlaceCursorAtEnd(settingsState.webdavUsername)
        webDAVPasswordState.setTextAndPlaceCursorAtEnd(settingsState.webdavPassword)
    }

    val hasCredentials = webDAVUrlState.text.isNotEmpty() &&
            webDAVAccountState.text.isNotEmpty() &&
            webDAVPasswordState.text.isNotEmpty()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SettingsHeader(text = "WebDAV")

        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.Http,
                    contentDescription = stringResource(R.string.url)
                )
            },
            headlineContent = {
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = webDAVUrlState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    decorator = { innerTextField ->
                        Box {
                            if (webDAVUrlState.text.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.url),
                                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            })

        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = stringResource(R.string.username)
                )
            },
            headlineContent = {
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = webDAVAccountState,
                    lineLimits = TextFieldLineLimits.SingleLine,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    decorator = { innerTextField ->
                        Box {
                            if (webDAVAccountState.text.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.username),
                                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            })

        ListItem(
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.Password,
                    contentDescription = stringResource(id = R.string.pass)
                )
            },
            headlineContent = {
                BasicSecureTextField(
                    modifier = Modifier.fillMaxWidth(),
                    state = webDAVPasswordState,
                    keyboardOptions = KeyboardOptions(
                        autoCorrectEnabled = true,
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    onKeyboardAction = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
                    decorator = { innerTextField ->
                        Box {
                            if (webDAVPasswordState.text.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.pass),
                                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            innerTextField()
                        }
                    },
                    textObfuscationMode = if (showWebDAVPassword) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped
                )
            },
            trailingContent = {
                IconButton(
                    onClick = { showWebDAVPassword = !showWebDAVPassword }
                ) {
                    Icon(
                        imageVector = if (showWebDAVPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = "Visibility toggle"
                    )
                }
            })

        ListItem(
            headlineContent = { Text(text = stringResource(R.string.webdav_save_config_hint)) },
            trailingContent = {
                TextButton(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        sharedViewModel.putPreferenceValue(Constants.Preferences.WEBDAV_URL, webDAVUrlState.text.toString())
                        sharedViewModel.putPreferenceValue(Constants.Preferences.WEBDAV_USERNAME, webDAVAccountState.text.toString())
                        sharedViewModel.putPreferenceValue(Constants.Preferences.WEBDAV_PASSWORD, webDAVPasswordState.text.toString())
                    },
                    colors = ButtonDefaults.textButtonColors()
                        .copy(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(text = stringResource(R.string.webdav_save_config))
                }
            }
        )

        ListItem(
            headlineContent = { Text(text = stringResource(R.string.webdav_test_connection)) },
            supportingContent = { Text(text = stringResource(R.string.webdav_test_connection_desc)) },
            trailingContent = {
                TextButton(
                    enabled = hasCredentials && !actionState.loading,
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        sharedViewModel.onDatabaseEvent(
                            DatabaseEvent.WebDavTest(
                                url = webDAVUrlState.text.toString().trimEnd('/'),
                                username = webDAVAccountState.text.toString(),
                                password = webDAVPasswordState.text.toString()
                            )
                        )
                    },
                    colors = ButtonDefaults.textButtonColors()
                        .copy(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VerifiedUser,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(R.string.webdav_test))
                }
            }
        )

        SettingsHeader(text = stringResource(R.string.sync))

        ListItem(
            headlineContent = { Text(text = stringResource(R.string.webdav_auto_sync)) },
            supportingContent = { Text(text = stringResource(R.string.webdav_auto_sync_desc)) },
            trailingContent = {
                Switch(
                    checked = settingsState.webdavAutoSyncEnabled,
                    onCheckedChange = {
                        sharedViewModel.putPreferenceValue(
                            Constants.Preferences.WEBDAV_AUTO_SYNC_ENABLED, it
                        )
                    }
                )
            }
        )

        ListItem(
            headlineContent = { Text(text = stringResource(R.string.webdav_sync_desc)) },
            trailingContent = {
                TextButton(
                    enabled = hasCredentials && !actionState.loading,
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        sharedViewModel.onDatabaseEvent(
                            DatabaseEvent.WebDavSync(
                                url = webDAVUrlState.text.toString().trimEnd('/'),
                                username = webDAVAccountState.text.toString(),
                                password = webDAVPasswordState.text.toString()
                            )
                        )
                    },
                    colors = ButtonDefaults.textButtonColors()
                        .copy(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Sync,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(R.string.webdav_sync))
                }
            }
        )

        if (actionState.message.isNotEmpty() && !actionState.loading) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = actionState.message,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }

    ProgressDialog(
        isLoading = actionState.loading,
        progress = actionState.progress,
        infinite = actionState.infinite,
        message = actionState.message,
        isError = actionState.isError,
        onDismissRequest = sharedViewModel::cancelDataAction
    )
}
