package com.example.ui.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.KidsSoundManager
import com.example.ui.theme.*

@Composable
fun AboutScreen(
    soundManager: KidsSoundManager,
    onOpenBackupDialog: () -> Unit
) {
    val context = LocalContext.current
    var showHeartAnimation by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KidBackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Identity Hero Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("about_hero_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(KidPurple, KidPurpleDark)
                            )
                        )
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧩", fontSize = 48.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "KIDS PUZZLE",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Version 1.0 • Educational Edition",
                            fontSize = 13.sp,
                            color = KidYellowSoft,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Developer Spotlight: Awiskar Acharya
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("developer_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, KidBlue.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(KidBlueBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👨‍💻", fontSize = 34.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = KidBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "CREATOR & LEAD DEVELOPER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = KidBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Awiskar Acharya",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = KidTextDark
                            )
                            Text(
                                text = "Software Engineer & Tech Innovator",
                                fontSize = 12.sp,
                                color = KidTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Passionate about empowering the next generation with joyful, tactile, and brain-stimulating learning apps. Designed with love to nurture curious young minds through phonics, puzzles, and creativity.",
                        fontSize = 13.sp,
                        color = KidTextMuted,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // LinkedIn Link Button
                    Button(
                        onClick = {
                            soundManager.playPopSound()
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.linkedin.com/in/awiskaracharya/")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("linkedin_profile_btn"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0077B5)),
                        elevation = ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Connect on LinkedIn",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email Link
                    OutlinedButton(
                        onClick = {
                            soundManager.playPopSound()
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:awiskaracharya@gmail.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Kids Puzzle Feedback & Love")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, KidBlue)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = KidBlue, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "awiskaracharya@gmail.com",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KidBlue
                        )
                    }
                }
            }
        }

        // Data Recovery & Anti-Loss Protection Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        soundManager.playPopSound()
                        onOpenBackupDialog()
                    },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, KidGreenLight),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(KidGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🛡️", fontSize = 28.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reinstall Data Protection",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidTextDark
                        )
                        Text(
                            text = "Uninstalled the app? No worries! Android Cloud Backup and your Magic Passport Code keep all stars and badges safe.",
                            fontSize = 12.sp,
                            color = KidTextMuted,
                            lineHeight = 16.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        tint = KidGreen
                    )
                }
            }
        }

        // Kids Safety & Privacy Pledges
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Safety & Quality Pledges 🌟",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidPurple
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PledgeRow(icon = "🔒", title = "100% Kid Safe & COPPA Compliant", desc = "Zero tracking or intrusive third-party ads.")
                    PledgeRow(icon = "✈️", title = "Fully Offline Capable", desc = "Play games and learn phonics anywhere without internet.")
                    PledgeRow(icon = "🗣️", title = "Native Speech Synthesis", desc = "Clear, cheerful kid pronunciation powered by Android TTS.")
                    PledgeRow(icon = "🏆", title = "Fair Frontend Rankings", desc = "Encouraging progression with level badges and friendly ranks.")
                }
            }
        }

        // Send Love / Heart Interaction
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF0F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (showHeartAnimation) "💖 Thank you for playing Kids Puzzle! 💖" else "Enjoying the app? Send love to the developer!",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidPink,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            showHeartAnimation = true
                            soundManager.playCelebrationFanfare()
                            soundManager.speak("Thank you so much! Awiskar appreciates your love!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KidPink),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Tap to Send ❤️", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PledgeRow(icon: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = KidTextDark)
            Text(text = desc, fontSize = 11.sp, color = KidTextMuted)
        }
    }
}
