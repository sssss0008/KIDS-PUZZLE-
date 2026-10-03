package com.example.ui.learn

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.KidsSoundManager
import com.example.data.*
import com.example.ui.theme.*

@Composable
fun LearnScreen(
    initialCategory: Int = 0,
    soundManager: KidsSoundManager,
    onRewardStars: (Int, String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(initialCategory) }
    var activePhonicsLetter by remember { mutableStateOf<PhonicsLetter?>(null) }
    var activeNumberItem by remember { mutableStateOf<NumberItem?>(null) }
    var tappedCount by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        "🔤 Phonics",
        "🔢 Numbers",
        "🦁 Animals",
        "🎨 Shapes & Colors"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidBackgroundLight)
    ) {
        // Top Section: Category Selector Scrollable Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = Color.White,
            contentColor = KidPurple,
            divider = {},
            indicator = {}
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) KidPurple else KidPurpleLight.copy(alpha = 0.15f),
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .clickable {
                            soundManager.playPopSound()
                            selectedTab = index
                        }
                        .testTag("learn_tab_$index")
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else KidPurple,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            when (selectedTab) {
                0 -> PhonicsTab(
                    onLetterClick = { letter ->
                        soundManager.playPopSound()
                        activePhonicsLetter = letter
                        soundManager.speak("${letter.letter}! ${letter.phonicsSound}! ${letter.letter} is for ${letter.word}!")
                        onRewardStars(1, "phonics")
                    }
                )
                1 -> NumbersTab(
                    onNumberClick = { item ->
                        soundManager.playPopSound()
                        activeNumberItem = item
                        tappedCount = 0
                        soundManager.speak("${item.number}! Let's count ${item.number} ${item.itemEmoji}!")
                        onRewardStars(1, "")
                    }
                )
                2 -> AnimalsTab(
                    soundManager = soundManager,
                    onReward = { onRewardStars(1, "") }
                )
                3 -> ShapesAndColorsTab(
                    soundManager = soundManager,
                    onReward = { onRewardStars(1, "") }
                )
            }
        }
    }

    // Letter Detail Dialog
    activePhonicsLetter?.let { letter ->
        Dialog(onDismissRequest = { activePhonicsLetter = null }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("phonics_detail_dialog")
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(letter.colorHex).copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = letter.phonicsSound,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(letter.colorHex),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        IconButton(onClick = { activePhonicsLetter = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${letter.letter} ${letter.letter.lowercaseChar()}",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(letter.colorHex)
                    )

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(letter.colorHex).copy(alpha = 0.15f))
                            .clickable {
                                soundManager.playPopSound()
                                soundManager.speak("${letter.word}! ${letter.funSentence}")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = letter.emoji, fontSize = 56.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = letter.word,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = KidTextDark
                    )

                    Text(
                        text = letter.funSentence,
                        fontSize = 15.sp,
                        color = KidTextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Voice repeat button
                    Button(
                        onClick = {
                            soundManager.playCorrectChime()
                            soundManager.speak("${letter.letter} says ${letter.phonicsSound}! ${letter.word}!")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(letter.colorHex)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth(0.85f).height(50.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Listen Again 🔊", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Next Letter / Previous Letter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val currentIndex = KidsLearningData.ALPHABET_LIST.indexOf(letter)
                        TextButton(
                            onClick = {
                                if (currentIndex > 0) {
                                    activePhonicsLetter = KidsLearningData.ALPHABET_LIST[currentIndex - 1]
                                    soundManager.speak("${activePhonicsLetter?.letter}! ${activePhonicsLetter?.word}!")
                                }
                            },
                            enabled = currentIndex > 0
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text("Prev")
                        }

                        TextButton(
                            onClick = {
                                if (currentIndex < KidsLearningData.ALPHABET_LIST.size - 1) {
                                    activePhonicsLetter = KidsLearningData.ALPHABET_LIST[currentIndex + 1]
                                    soundManager.speak("${activePhonicsLetter?.letter}! ${activePhonicsLetter?.word}!")
                                }
                            },
                            enabled = currentIndex < KidsLearningData.ALPHABET_LIST.size - 1
                        ) {
                            Text("Next")
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                        }
                    }
                }
            }
        }
    }

    // Number Interactive Counting Dialog
    activeNumberItem?.let { item ->
        Dialog(onDismissRequest = { activeNumberItem = null }) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp)
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
                            text = "Count to ${item.number}!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = KidPurple
                        )
                        IconButton(onClick = { activeNumberItem = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    Text(
                        text = "${item.number}",
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(item.colorHex)
                    )

                    Text(
                        text = item.word,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidTextDark
                    )

                    Text(
                        text = "Tap the items to count: $tappedCount / ${item.number}",
                        fontSize = 14.sp,
                        color = KidTextMuted,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Interactive counting grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(minOf(item.number, 5)),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .padding(vertical = 8.dp)
                    ) {
                        items(item.number) { idx ->
                            val isPopped = idx < tappedCount
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isPopped) KidYellowSoft else Color(0xFFF1F2F6))
                                    .clickable {
                                        if (!isPopped) {
                                            tappedCount = idx + 1
                                            soundManager.playPopSound()
                                            soundManager.speak("${idx + 1}")
                                            if (tappedCount == item.number) {
                                                soundManager.playCelebrationFanfare()
                                                soundManager.speak("Super! You counted all ${item.number}!")
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.itemEmoji,
                                    fontSize = if (isPopped) 28.sp else 22.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            tappedCount = 0
                            soundManager.playPopSound()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KidCoral),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("Reset Counter 🔄", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhonicsTab(
    onLetterClick: (PhonicsLetter) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(KidsLearningData.ALPHABET_LIST) { letter ->
            Card(
                modifier = Modifier
                    .height(115.dp)
                    .clickable { onLetterClick(letter) }
                    .testTag("phonics_letter_${letter.letter}"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Color(letter.colorHex).copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = letter.letter.toString(),
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(letter.colorHex)
                    )
                    Text(
                        text = letter.emoji,
                        fontSize = 24.sp
                    )
                    Text(
                        text = letter.word,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = KidTextDark,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun NumbersTab(
    onNumberClick: (NumberItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(KidsLearningData.NUMBERS_LIST) { item ->
            Card(
                modifier = Modifier
                    .height(120.dp)
                    .clickable { onNumberClick(item) },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Color(item.colorHex).copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "${item.number}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(item.colorHex)
                        )
                        Text(
                            text = item.word,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = KidTextDark
                        )
                    }

                    Text(text = item.itemEmoji, fontSize = 34.sp)
                }
            }
        }
    }
}

@Composable
private fun AnimalsTab(
    soundManager: KidsSoundManager,
    onReward: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        items(KidsLearningData.ANIMALS_LIST) { animal ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        soundManager.playCorrectChime()
                        soundManager.speak(animal.voiceSpeech)
                        onReward()
                    },
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Color(animal.colorHex).copy(alpha = 0.3f)),
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
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(animal.colorHex).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = animal.emoji, fontSize = 36.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = animal.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = KidTextDark
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(animal.colorHex).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = animal.soundText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(animal.colorHex),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = animal.funFact,
                            fontSize = 12.sp,
                            color = KidTextMuted
                        )
                    }

                    IconButton(
                        onClick = {
                            soundManager.playCorrectChime()
                            soundManager.speak(animal.voiceSpeech)
                            onReward()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Hear animal sound",
                            tint = Color(animal.colorHex)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShapesAndColorsTab(
    soundManager: KidsSoundManager,
    onReward: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Text(
                text = "Geometric Shapes 📐",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = KidTextDark
            )
        }

        items(KidsLearningData.SHAPES_LIST) { shape ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        soundManager.playPopSound()
                        soundManager.speak("This is a ${shape.name}! ${shape.description}")
                        onReward()
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Color(shape.colorHex).copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(text = shape.emoji, fontSize = 40.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = shape.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = Color(shape.colorHex)
                        )
                        Text(
                            text = shape.description,
                            fontSize = 12.sp,
                            color = KidTextMuted
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Rainbow Colors 🌈",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = KidTextDark,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(KidsLearningData.COLORS_LIST) { colorItem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        soundManager.playPopSound()
                        soundManager.speak("${colorItem.name}! Like a ${colorItem.exampleName}!")
                        onReward()
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Color(colorItem.colorHex).copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(colorItem.colorHex))
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = colorItem.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = KidTextDark
                        )
                        Text(
                            text = "Example: ${colorItem.exampleEmoji} ${colorItem.exampleName}",
                            fontSize = 13.sp,
                            color = KidTextMuted
                        )
                    }
                }
            }
        }
    }
}
