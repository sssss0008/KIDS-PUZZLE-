package com.example.ui.drawer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.KidsSoundManager
import com.example.data.*
import com.example.ui.theme.*

// -------------------------------------------------------------
// 1. PROFILE EDIT DIALOG
// -------------------------------------------------------------
@Composable
fun ProfileEditDialog(
    profile: KidProfile,
    soundManager: KidsSoundManager,
    onDismiss: () -> Unit,
    onSave: (name: String, age: Int, avatarId: String) -> Unit
) {
    var editName by remember { mutableStateOf(profile.name) }
    var editAge by remember { mutableIntStateOf(profile.age) }
    var editAvatarId by remember { mutableStateOf(profile.avatarId) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Profile ✏️",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = KidPurple
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Name Field
                OutlinedTextField(
                    value = editName,
                    onValueChange = { if (it.length <= 16) editName = it },
                    label = { Text("Kid's Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Age Picker
                Text(
                    text = "Age: $editAge years",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = KidTextDark
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    listOf(2, 3, 4, 5, 6, 7).forEach { age ->
                        val isSelected = editAge == age
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) KidCoral else Color(0xFFF1F2F6))
                                .clickable {
                                    soundManager.playPopSound()
                                    editAge = age
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$age",
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else KidTextDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Avatar Choice
                Text(
                    text = "Choose Adventure Buddy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = KidTextDark
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().height(140.dp).padding(vertical = 6.dp)
                ) {
                    items(AVAILABLE_AVATARS) { avatar ->
                        val isSelected = editAvatarId == avatar.id
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) Color(avatar.colorHex).copy(alpha = 0.25f) else Color.White)
                                .border(
                                    width = if (isSelected) 2.5.dp else 1.dp,
                                    color = if (isSelected) Color(avatar.colorHex) else Color.LightGray,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable {
                                    soundManager.playPopSound()
                                    editAvatarId = avatar.id
                                    soundManager.speak(avatar.name)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = avatar.emoji, fontSize = 26.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        soundManager.playCorrectChime()
                        onSave(editName.trim(), editAge, editAvatarId)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_profile_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KidPurple)
                ) {
                    Text("Save Profile 🌟", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. LEADERBOARD & RANKING DIALOG ("MAKE RAKING FEAUTRE ASLO")
// -------------------------------------------------------------
@Composable
fun LeaderboardDialog(
    leaderboard: List<LeaderboardEntry>,
    soundManager: KidsSoundManager,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(4.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Kids Hall of Fame 🏆",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = KidPurple
                        )
                        Text(
                            text = "Earn stars to climb the ladder!",
                            fontSize = 12.sp,
                            color = KidTextMuted
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(leaderboard) { entry ->
                        val medal = when (entry.rank) {
                            1 -> "🥇"
                            2 -> "🥈"
                            3 -> "🥉"
                            else -> "#${entry.rank}"
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (entry.isCurrentUser) KidYellowLight else Color(0xFFF8F9FA)
                            ),
                            border = BorderStroke(
                                width = if (entry.isCurrentUser) 2.dp else 1.dp,
                                color = if (entry.isCurrentUser) KidYellow else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = medal,
                                    fontSize = if (entry.rank <= 3) 22.sp else 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(32.dp),
                                    textAlign = TextAlign.Center
                                )

                                Text(text = entry.avatarEmoji, fontSize = 26.sp)

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = entry.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (entry.isCurrentUser) KidCoralDark else KidTextDark
                                    )
                                    Text(
                                        text = entry.title,
                                        fontSize = 11.sp,
                                        color = KidTextMuted
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "⭐", fontSize = 12.sp)
                                        Spacer(Modifier.width(2.dp))
                                        Text(
                                            text = "${entry.stars}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            color = KidTextDark
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KidPurple)
                ) {
                    Text("Close Hall of Fame", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. BADGES & TROPHIES DIALOG
// -------------------------------------------------------------
@Composable
fun BadgesDialog(
    profile: KidProfile,
    soundManager: KidsSoundManager,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f).padding(4.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Trophy Room 🎖️",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = KidPurple
                        )
                        Text(
                            text = "${profile.badgesUnlocked.size} of ${APP_BADGES.size} Badges Unlocked",
                            fontSize = 12.sp,
                            color = KidTextMuted
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(APP_BADGES) { badge ->
                        val isUnlocked = profile.badgesUnlocked.contains(badge.id)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    soundManager.playPopSound()
                                    if (isUnlocked) {
                                        soundManager.speak("Badge: ${badge.title}! ${badge.description}")
                                    } else {
                                        soundManager.speak("Locked badge: Earn ${badge.requiredStars} stars to unlock!")
                                    }
                                },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnlocked) KidYellowLight.copy(alpha = 0.5f) else Color(0xFFF1F2F6)
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isUnlocked) KidYellow else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = if (isUnlocked) badge.iconEmoji else "🔒",
                                    fontSize = 32.sp
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = badge.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isUnlocked) KidTextDark else Color.Gray
                                    )
                                    Text(
                                        text = badge.description,
                                        fontSize = 11.sp,
                                        color = KidTextMuted
                                    )
                                }

                                if (isUnlocked) {
                                    Text("Unlocked!", color = KidGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                } else {
                                    Text("${badge.requiredStars} ⭐", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KidPurple)
                ) {
                    Text("Got It! 🌟", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. BACKUP & RESTORE DIALOG ("DATA NOT LOST AFTER UNINSTALL")
// -------------------------------------------------------------
@Composable
fun BackupRestoreDialog(
    profile: KidProfile,
    repository: DataRepository,
    soundManager: KidsSoundManager,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var inputCode by remember { mutableStateOf("") }
    var restoreStatus by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cloud Passport & Backup 🛡️",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = KidPurple
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "If you uninstall and reinstall the app, use this passport code to restore all your stars and progress!",
                    fontSize = 12.sp,
                    color = KidTextMuted,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Current Passport Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = KidYellowLight),
                    border = BorderStroke(1.5.dp, KidYellow)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("YOUR KID PASSPORT CODE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = KidYellow)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = profile.passportCode,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = KidTextDark,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                soundManager.playPopSound()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Kids Puzzle Passport", profile.passportCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Passport Code copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = KidPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy Code", fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Restore Old Progress 🔁",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = KidTextDark
                )

                OutlinedTextField(
                    value = inputCode,
                    onValueChange = { inputCode = it },
                    placeholder = { Text("Paste code (e.g. KP-7892-STAR)") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                Button(
                    onClick = {
                        val success = repository.restoreFromBackup(inputCode)
                        if (success) {
                            isSuccess = true
                            restoreStatus = "Hooray! Data restored successfully! ⭐"
                            soundManager.playCelebrationFanfare()
                            soundManager.speak("Welcome back! All your stars and progress are restored!")
                        } else {
                            isSuccess = false
                            restoreStatus = "Could not restore. Please check the code."
                            soundManager.playWrongBuzz()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KidCoral)
                ) {
                    Text("Restore My Progress 🚀", fontWeight = FontWeight.Bold)
                }

                restoreStatus?.let { msg ->
                    Text(
                        text = msg,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSuccess) KidGreen else KidCoralDark,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. SOUND & SPEECH SETTINGS DIALOG
// -------------------------------------------------------------
@Composable
fun SoundSettingsDialog(
    soundManager: KidsSoundManager,
    onDismiss: () -> Unit
) {
    var speechEnabled by remember { mutableStateOf(soundManager.isSpeechEnabled) }
    var sfxEnabled by remember { mutableStateOf(soundManager.isSoundEffectsEnabled) }
    var speechRate by remember { mutableFloatStateOf(soundManager.speechRate) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sound & Speech 🔊",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = KidPurple
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Kid Voice Narration (TTS)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Reads letters, animals & quizzes aloud", fontSize = 11.sp, color = KidTextMuted)
                    }
                    Switch(
                        checked = speechEnabled,
                        onCheckedChange = {
                            speechEnabled = it
                            soundManager.isSpeechEnabled = it
                            if (it) soundManager.speak("Voice narration is on!")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Game Sound Effects", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Balloon pops, chimes, star fanfare", fontSize = 11.sp, color = KidTextMuted)
                    }
                    Switch(
                        checked = sfxEnabled,
                        onCheckedChange = {
                            sfxEnabled = it
                            soundManager.isSoundEffectsEnabled = it
                            if (it) soundManager.playCorrectChime()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text("Speech Speed: ${(speechRate * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Slider(
                    value = speechRate,
                    onValueChange = {
                        speechRate = it
                        soundManager.speechRate = it
                    },
                    valueRange = 0.6f..1.2f
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        soundManager.playCorrectChime()
                        soundManager.speak("Sounds like a plan! Let's play!")
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = KidPurple)
                ) {
                    Text("Save & Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 6. PARENT GATE DIALOG (Protects settings & reset)
// -------------------------------------------------------------
@Composable
fun ParentGateDialog(
    soundManager: KidsSoundManager,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val num1 = remember { (2..6).random() }
    val num2 = remember { (1..5).random() }
    val expected = num1 + num2
    var input by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Grown-Up Zone 🔒",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = KidPurple
                )
                Text(
                    text = "Please solve this to enter settings:",
                    fontSize = 13.sp,
                    color = KidTextMuted,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "$num1 + $num2 = ?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = KidCoral
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Answer") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(0.6f)
                )

                errorMsg?.let {
                    Text(it, color = KidCoralDark, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            if (input.trim().toIntOrNull() == expected) {
                                soundManager.playCorrectChime()
                                onSuccess()
                            } else {
                                soundManager.playWrongBuzz()
                                errorMsg = "Incorrect answer, try again."
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KidPurple)
                    ) {
                        Text("Enter")
                    }
                }
            }
        }
    }
}
