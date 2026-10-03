package com.example.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.KidsSoundManager
import com.example.data.KidProfile
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    profile: KidProfile,
    soundManager: KidsSoundManager,
    onNavigateToLearn: (categoryIndex: Int) -> Unit,
    onNavigateToPractice: (gameIndex: Int) -> Unit,
    onOpenDrawer: () -> Unit
) {
    // Pulse animation for daily star icon
    val infiniteTransition = rememberInfiniteTransition(label = "star_pulse")
    val starScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KidBackgroundLight)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Kid Greeting & Status Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_header_card"),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(KidPurple, Color(0xFF8C7AE6))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Avatar & Greeting
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable {
                                        soundManager.playPopSound()
                                        onOpenDrawer()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.currentAvatar.emoji,
                                    fontSize = 36.sp
                                )
                            }

                            Column {
                                Text(
                                    text = "Hello, ${profile.name}! 👋",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color.White.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = "Level ${profile.level} • ${profile.rankTitle}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        // Stars Badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = KidYellowSoft,
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .scale(starScale)
                                .clickable {
                                    soundManager.playStarCollect()
                                    soundManager.speak("You have ${profile.stars} stars! Super cool!")
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "⭐", fontSize = 16.sp)
                                Text(
                                    text = "${profile.stars}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = KidTextDark
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Streak & Passport Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            soundManager.playPopSound()
                            soundManager.speak("Your streak is ${profile.streakDays} days! Keep exploring every day!")
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, KidCoralLight.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "🔥", fontSize = 28.sp)
                        Column {
                            Text(
                                text = "${profile.streakDays} Day Streak",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = KidTextDark
                            )
                            Text(
                                text = "Daily Bonus Active!",
                                fontSize = 11.sp,
                                color = KidCoralDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Passport / Backup Code Quick Peek
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            soundManager.playPopSound()
                            onOpenDrawer()
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, KidGreenLight.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "🛡️", fontSize = 28.sp)
                        Column {
                            Text(
                                text = "Cloud Passport",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = KidTextDark
                            )
                            Text(
                                text = profile.passportCode,
                                fontSize = 11.sp,
                                color = KidGreen,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // Today's Featured Adventure Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("featured_adventure_card")
                    .clickable {
                        soundManager.playCelebrationFanfare()
                        soundManager.speak("Let's solve the Shape Match Puzzle!")
                        onNavigateToPractice(0) // Shape match
                    },
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEAA7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = KidCoral
                        ) {
                            Text(
                                text = "TODAY'S QUEST",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Shape Match Puzzle!",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            color = KidTextDark
                        )
                        Text(
                            text = "Match 4 shapes to unlock +20 stars",
                            fontSize = 13.sp,
                            color = KidTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🧩", fontSize = 38.sp)
                    }
                }
            }
        }

        // Learning & Play Categories Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Learning Kingdoms 🏰",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = KidTextDark
                )
            }
        }

        // 4 Fast Learning Portals
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryTile(
                        title = "Phonics A-Z",
                        subtitle = "Hear letter sounds",
                        emoji = "🔤",
                        color = Color(0xFFFF7675),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            soundManager.playPopSound()
                            onNavigateToLearn(0)
                        }
                    )
                    CategoryTile(
                        title = "Number Fun",
                        subtitle = "Count 1 to 20",
                        emoji = "🔢",
                        color = Color(0xFF00CEC9),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            soundManager.playPopSound()
                            onNavigateToLearn(1)
                        }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CategoryTile(
                        title = "Animals & Sounds",
                        subtitle = "Meet happy pets",
                        emoji = "🦁",
                        color = Color(0xFFFFAA00),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            soundManager.playPopSound()
                            onNavigateToLearn(2)
                        }
                    )
                    CategoryTile(
                        title = "Shapes & Colors",
                        subtitle = "Circles, stars & more",
                        emoji = "🎨",
                        color = Color(0xFF6C5CE7),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            soundManager.playPopSound()
                            onNavigateToLearn(3)
                        }
                    )
                }
            }
        }

        // Practice & Mini-Games Highlights
        item {
            Text(
                text = "Fun Games & Practice 🎮",
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = KidTextDark,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                GameRowCard(
                    title = "Jigsaw Picture Puzzle",
                    desc = "Slide pieces to assemble cartoon pictures",
                    emoji = "🖼️",
                    tag = "PUZZLE",
                    color = KidBlue,
                    onClick = {
                        soundManager.playPopSound()
                        onNavigateToPractice(1)
                    }
                )

                GameRowCard(
                    title = "Memory Match Cards",
                    desc = "Flip cards & find matching pairs",
                    emoji = "🧠",
                    tag = "MEMORY",
                    color = KidPurple,
                    onClick = {
                        soundManager.playPopSound()
                        onNavigateToPractice(2)
                    }
                )

                GameRowCard(
                    title = "Balloon Pop Game",
                    desc = "Pop flying balloons with letters and numbers",
                    emoji = "🎈",
                    tag = "POP & WIN",
                    color = KidCoral,
                    onClick = {
                        soundManager.playPopSound()
                        onNavigateToPractice(3)
                    }
                )

                GameRowCard(
                    title = "Rainbow Magic Drawing",
                    desc = "Freehand neon glowing drawing & letter tracing",
                    emoji = "🖌️",
                    tag = "CREATIVE",
                    color = KidGreen,
                    onClick = {
                        soundManager.playPopSound()
                        onNavigateToPractice(4)
                    }
                )

                GameRowCard(
                    title = "Super Kids Quiz",
                    desc = "Listen to speech questions & test your smarts",
                    emoji = "🏆",
                    tag = "QUIZ",
                    color = Color(0xFFFF9F43),
                    onClick = {
                        soundManager.playPopSound()
                        onNavigateToPractice(5)
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryTile(
    title: String,
    subtitle: String,
    emoji: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.14f)),
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(color.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 26.sp)
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = KidTextDark
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = KidTextMuted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun GameRowCard(
    title: String,
    desc: String,
    emoji: String,
    tag: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 28.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = color.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = KidTextDark
                )
                Text(
                    text = desc,
                    fontSize = 12.sp,
                    color = KidTextMuted
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
