package com.example.student_life_app.features.profile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.student_life_app.core.designsystem.components.AppButton
import com.example.student_life_app.core.designsystem.components.AppTextField
import com.example.student_life_app.core.designsystem.components.ConfirmDialog
import com.example.student_life_app.core.designsystem.components.StudentLifeHeader
import com.example.student_life_app.core.designsystem.components.StudentLifeScaffold
import com.example.student_life_app.features.profile.viewmodel.ProfileViewModel
import com.example.student_life_app.ui.theme.StudentlifeappTheme

/**
 * Halaman Pengaturan Profil (Stateful): menghubungkan ProfileViewModel ke ProfileContent.
 */
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(message)
            viewModel.consumeSnackbarMessage()
        }
    }

    LaunchedEffect(uiState.loggedOut) {
        if (uiState.loggedOut) {
            onLogout()
        }
    }

    ProfileContent(
        savedFullName = uiState.savedFullName,
        savedNim = uiState.savedNim,
        email = uiState.email,
        fullNameInput = viewModel.fullNameInput,
        onFullNameChange = viewModel::onFullNameChange,
        nimInput = viewModel.nimInput,
        onNimChange = viewModel::onNimChange,
        fullNameError = uiState.validationErrors.fullNameError,
        nimError = uiState.validationErrors.nimError,
        isLoading = uiState.isLoading,
        isSaving = uiState.isSaving,
        isLoggingOut = uiState.isLoggingOut,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onSaveClick = viewModel::saveProfile,
        onLogoutClick = { showLogoutDialog = true }
    )

    ConfirmDialog(
        show = showLogoutDialog,
        title = "Konfirmasi Keluar",
        message = "Apakah Anda yakin ingin keluar dari akun?",
        confirmText = "Keluar",
        dismissText = "Batal",
        onConfirm = {
            showLogoutDialog = false
            viewModel.logout()
        },
        onDismiss = { showLogoutDialog = false }
    )
}

@Composable
fun ProfileContent(
    savedFullName: String,
    savedNim: String,
    email: String,
    fullNameInput: String,
    onFullNameChange: (String) -> Unit,
    nimInput: String,
    onNimChange: (String) -> Unit,
    fullNameError: String?,
    nimError: String?,
    isLoading: Boolean,
    isSaving: Boolean,
    isLoggingOut: Boolean,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onSaveClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val initials = remember(savedFullName) { extractInitials(savedFullName) }
    val isBusy = isLoading || isSaving || isLoggingOut

    StudentLifeScaffold(
        modifier = modifier,
        topBar = {
            StudentLifeHeader(
                title = "Pengaturan Profil",
                showBackButton = true,
                onBackClick = onBack,
                showProfileButton = false
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Kartu 1: Ringkasan Identitas
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onPrimaryContainer
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = savedFullName.ifBlank { "Mahasiswa" },
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (savedNim.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "NIM: $savedNim",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                            if (email.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = email,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Kartu 2: Form Ubah Personalia
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Ubah Identitas",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                        )

                        AppTextField(
                            value = fullNameInput,
                            onValueChange = onFullNameChange,
                            label = "Nama Lengkap",
                            placeholder = "Masukkan nama lengkap",
                            errorMessage = fullNameError,
                            isRequired = true
                        )

                        AppTextField(
                            value = nimInput,
                            onValueChange = onNimChange,
                            label = "NIM",
                            placeholder = "Masukkan NIM",
                            errorMessage = nimError
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        AppButton(
                            text = if (isSaving) "Menyimpan..." else "Simpan Perubahan",
                            enabled = !isBusy,
                            onClick = {
                                focusManager.clearFocus()
                                onSaveClick()
                            }
                        )
                    }
                }

                // Bagian 3: Keluar Akun
                OutlinedButton(
                    onClick = onLogoutClick,
                    enabled = !isBusy,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, colorScheme.error),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = null,
                        tint = colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLoggingOut) "Keluar..." else "Keluar Akun",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.error
                    )
                }
            }
        }
    }
}

private fun extractInitials(fullName: String): String {
    val parts = fullName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> "M"
        parts.size == 1 -> parts[0].take(2).uppercase()
        else -> "${parts.first().first()}${parts.last().first()}".uppercase()
    }
}

@Preview(showBackground = true, name = "Profile Light")
@Composable
private fun ProfileContentLightPreview() {
    StudentlifeappTheme(darkTheme = false) {
        ProfileContent(
            savedFullName = "Prayoga Adi Setyawan",
            savedNim = "245150401111021",
            email = "prayoga@student.ub.ac.id",
            fullNameInput = "Prayoga Adi Setyawan",
            onFullNameChange = {},
            nimInput = "245150401111021",
            onNimChange = {},
            fullNameError = null,
            nimError = null,
            isLoading = false,
            isSaving = false,
            isLoggingOut = false,
            snackbarHostState = SnackbarHostState(),
            onBack = {},
            onSaveClick = {},
            onLogoutClick = {}
        )
    }
}
