package com.example.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.KidsSoundManager
import com.example.data.AVAILABLE_AVATARS
import com.example.data.KidAvatar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    soundManager: KidsSoundManager,
    onComplete: (name: String, age: Int, avatarId: String) -> Unit,
    onRestoreRequest: () -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var kidName by remember { mutableStateOf("") }
    var selectedAge by remember { mutableIntStateOf(4) }
    var selectedAvatar by remember { mutableStateOf(AVAILABLE_AVATARS[0]) }

    val totalSteps = 4

    LaunchedEffect(step) {
        when (step) {
            0 -> soundManager.speak("Welcome to Kids Puzzle! Let's begin a super fun learning adventure!")
            1 -> soundManager.speak("What is your name, little superstar?")
            2 -> soundManager.speak("How old are you?")
            3 -> soundManager.speak("Pick your favorite adventure companion!")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF9E6),
                        Color(0xFFE8F4FD),
                        Color(0xFFF3E5F5)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar with back & progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (step > 0) {
                    IconButton(
                        onClick = {
                            soundManager.playPopSound()
                            step--
                        },
                        modifier = Modifier.testTag("onboarding_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = KidPurple
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Step Dots
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 0 until totalSteps) {
                        val active = i == step
                        Box(
                            modifier = Modifier
                                .size(if (active) 14.dp else 10.dp)
                                .clip(CircleShape)
                                .background(if (active) KidPurple else KidPurpleLight.copy(alpha = 0.4f))
                        )
                    }
                }

                // Restore Button on first step
                if (step == 0) {
                    TextButton(
                        onClick = {
                            soundManager.playPopSound()
                            onRestoreRequest()
                        },
                        modifier = Modifier.testTag("onboarding_restore_btn")
                    ) {
                        Icon(Icons.Default.Restore, contentDescription = null, tint = KidCoral, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Restore", color = KidCoral, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Step Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                when (step) {
                    0 -> StepWelcome(
                        soundManager = soundManager,
                        onStart = {
                            soundManager.playCelebrationFanfare()
                            step = 1
                        }
                    )
                    1 -> StepName(
                        name = kidName,
                        onNameChange = { kidName = it },
                        soundManager = soundManager
                    )
                    2 -> StepAge(
                        selectedAge = selectedAge,
                        onAgeSelected = {
                            selectedAge = it
                            soundManager.playPopSound()
                            soundManager.speak("Awesome! $it years old!")
                        }
                    )
                    3 -> StepAvatar(
                        selectedAvatar = selectedAvatar,
                        onAvatarSelected = {
                            selectedAvatar = it
                            soundManager.playPopSound()
                            soundManager.speak("I am ${it.name}! Let's play together!")
                        }
                    )
                }
            }

            // Bottom Continue Button
            if (step > 0) {
                val canProceed = when (step) {
                    1 -> kidName.trim().isNotEmpty()
                    else -> true
                }

                Button(
                    onClick = {
                        soundManager.playPopSound()
                        if (step < totalSteps - 1) {
                            step++
                        } else {
                            soundManager.playCelebrationFanfare()
                            onComplete(kidName.trim(), selectedAge, selectedAvatar.id)
                        }
                    },
                    enabled = canProceed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .testTag("onboarding_next_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = KidCoral,
                        disabledContainerColor = Color.LightGray
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(
                        text = if (step == totalSteps - 1) "Let's Play! 🚀" else "Continue",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = if (step == totalSteps - 1) Icons.Default.Celebration else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun StepWelcome(
    soundManager: KidsSoundManager,
    onStart: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        // Joyful Hero Puzzle Icon
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(KidYellowLight, KidYellowSoft, KidOrange)
                    )
                )
                .clickable {
                    soundManager.playPopSound()
                    soundManager.speak("Kids Puzzle! Learn, play, and win stars!")
                },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "🧩", fontSize = 72.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "KIDS PUZZLE",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = KidPurple,
            letterSpacing = 1.sp
        )

        Text(
            text = "Play, Learn & Win Stars! ⭐",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = KidCoral,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Exciting games, Phonics A-Z, cute animals, shape puzzles, drawing, and kid ranks!",
            fontSize = 15.sp,
            color = KidTextMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(60.dp)
                .testTag("start_adventure_btn"),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = KidPurple),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = KidYellowSoft)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Start Adventure!",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StepName(
    name: String,
    onNameChange: (String) -> Unit,
    soundManager: KidsSoundManager
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "👋", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "What is your name?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = KidPurple
        )
        Text(
            text = "Type your name or pick a fun nickname",
            fontSize = 14.sp,
            color = KidTextMuted
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { if (it.length <= 16) onNameChange(it) },
            placeholder = { Text("e.g. Leo, Emma, Alex", fontSize = 18.sp) },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("kid_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KidPurple,
                unfocusedBorderColor = KidPurpleLight,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Quick suggestions
        Text("Or pick one:", fontSize = 13.sp, color = KidTextMuted)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            listOf("Leo 🦁", "Luna 🐰", "Sam 🚀", "Mia 🌸").forEach { suggestion ->
                val clean = suggestion.substringBefore(" ")
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = KidBlueBg,
                    border = BorderStroke(1.dp, KidBlueLight),
                    modifier = Modifier.clickable {
                        soundManager.playPopSound()
                        onNameChange(clean)
                        soundManager.speak("Nice to meet you, $clean!")
                    }
                ) {
                    Text(
                        text = suggestion,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KidBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepAge(
    selectedAge: Int,
    onAgeSelected: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🎂", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "How old are you?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = KidPurple
        )
        Text(
            text = "We adapt games for your age!",
            fontSize = 14.sp,
            color = KidTextMuted
        )

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(2, 3, 4, 5, 6, 7).forEach { age ->
                val isSelected = selectedAge == age
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.15f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )

                Box(
                    modifier = Modifier
                        .scale(scale)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) KidCoral else Color.White)
                        .border(
                            2.dp,
                            if (isSelected) KidCoralDark else KidPurpleLight.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .clickable { onAgeSelected(age) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$age",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isSelected) Color.White else KidPurple
                    )
                }
            }
        }
    }
}

@Composable
private fun StepAvatar(
    selectedAvatar: KidAvatar,
    onAvatarSelected: (KidAvatar) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Pick Your Adventure Buddy!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = KidPurple
        )
        Text(
            text = "Tap to choose your game partner",
            fontSize = 13.sp,
            color = KidTextMuted
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(AVAILABLE_AVATARS) { avatar ->
                val isSelected = selectedAvatar.id == avatar.id
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                )

                Card(
                    modifier = Modifier
                        .scale(scale)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onAvatarSelected(avatar) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) Color(avatar.colorHex).copy(alpha = 0.25f) else Color.White
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected) Color(avatar.colorHex) else Color.LightGray.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(if (isSelected) 6.dp else 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = avatar.emoji, fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = avatar.name.substringBefore(" "),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidTextDark,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
