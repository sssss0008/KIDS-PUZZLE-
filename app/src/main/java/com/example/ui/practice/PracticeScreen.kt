package com.example.ui.practice

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.KidsSoundManager
import com.example.data.KidsLearningData
import com.example.data.QuizQuestion
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PracticeScreen(
    initialGame: Int = 0,
    soundManager: KidsSoundManager,
    onRewardStars: (amount: Int, category: String) -> Unit
) {
    var selectedGame by remember { mutableIntStateOf(initialGame) }

    val gamesList = listOf(
        "🧩 Shape Match",
        "🔲 Slide Puzzle",
        "🧠 Memory Match",
        "🎈 Balloon Pop",
        "🎨 Rainbow Draw",
        "🏆 Kids Quiz"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KidBackgroundLight)
    ) {
        // Game Selector Tab Bar
        ScrollableTabRow(
            selectedTabIndex = selectedGame,
            edgePadding = 16.dp,
            containerColor = Color.White,
            contentColor = KidPurple,
            divider = {},
            indicator = {}
        ) {
            gamesList.forEachIndexed { index, title ->
                val isSelected = selectedGame == index
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) KidCoral else KidCoralLight.copy(alpha = 0.15f),
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .clickable {
                            soundManager.playPopSound()
                            selectedGame = index
                        }
                        .testTag("practice_game_tab_$index")
                ) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else KidCoralDark,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            when (selectedGame) {
                0 -> ShapeMatchGame(soundManager = soundManager, onWon = { onRewardStars(15, "puzzle") })
                1 -> SlidePuzzleGame(soundManager = soundManager, onWon = { onRewardStars(20, "puzzle") })
                2 -> MemoryCardsGame(soundManager = soundManager, onWon = { onRewardStars(20, "puzzle") })
                3 -> BalloonPopGame(soundManager = soundManager, onPopReward = { onRewardStars(1, "balloon") })
                4 -> RainbowDrawGame(soundManager = soundManager, onSaveReward = { onRewardStars(10, "") })
                5 -> KidsQuizGame(soundManager = soundManager, onWon = { onRewardStars(10, "quiz") })
            }
        }
    }
}

// -------------------------------------------------------------
// 1. SHAPE MATCH GAME
// -------------------------------------------------------------
data class TargetShape(val id: String, val name: String, val emoji: String, val color: Color)

@Composable
private fun ShapeMatchGame(
    soundManager: KidsSoundManager,
    onWon: () -> Unit
) {
    val targetShapes = remember {
        listOf(
            TargetShape("star", "Star", "⭐", KidYellowSoft),
            TargetShape("circle", "Circle", "⚪", KidCoral),
            TargetShape("triangle", "Triangle", "🔺", KidGreen),
            TargetShape("heart", "Heart", "❤️", KidPink)
        )
    }

    var matchedIds by remember { mutableStateOf(setOf<String>()) }
    var selectedPiece by remember { mutableStateOf<TargetShape?>(null) }
    var gameWon by remember { mutableStateOf(false) }

    val remainingPieces = remember(matchedIds) {
        targetShapes.filter { !matchedIds.contains(it.id) }.shuffled()
    }

    LaunchedEffect(matchedIds) {
        if (matchedIds.size == targetShapes.size && !gameWon) {
            gameWon = true
            soundManager.playCelebrationFanfare()
            soundManager.speak("Fantastic! You matched all the shapes! You won 15 stars!")
            onWon()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Game Header
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Match the Shapes! 🧩",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = KidPurple
                    )
                    Text(
                        text = "Tap a shape piece below, then tap its home slot!",
                        fontSize = 12.sp,
                        color = KidTextMuted
                    )
                }

                IconButton(
                    onClick = {
                        soundManager.playPopSound()
                        matchedIds = emptySet()
                        selectedPiece = null
                        gameWon = false
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = KidPurple)
                }
            }
        }

        // Target Slots
        Text(
            text = "Home Slots 🎯",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = KidTextDark
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            targetShapes.forEach { target ->
                val isMatched = matchedIds.contains(target.id)
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isMatched) target.color.copy(alpha = 0.25f)
                            else Color(0xFFE9ECEF)
                        )
                        .border(
                            width = 2.dp,
                            color = if (isMatched) target.color else Color.LightGray,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            if (!isMatched && selectedPiece != null) {
                                if (selectedPiece?.id == target.id) {
                                    soundManager.playCorrectChime()
                                    soundManager.speak("Super! Matched ${target.name}!")
                                    matchedIds = matchedIds + target.id
                                    selectedPiece = null
                                } else {
                                    soundManager.playWrongBuzz()
                                    soundManager.speak("Oops, not quite! Try another one.")
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isMatched) {
                        Text(text = target.emoji, fontSize = 42.sp)
                    } else {
                        Text(
                            text = "?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Selected Piece Indicator
        selectedPiece?.let { piece ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = KidYellowLight,
                border = BorderStroke(1.dp, KidYellow)
            ) {
                Text(
                    text = "Selected: ${piece.emoji} ${piece.name} ➡️ Tap matching slot above!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = KidTextDark,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        } ?: Text(
            text = if (gameWon) "🎉 Superstar! All Matched! ⭐" else "Tap a piece below to select",
            fontSize = 14.sp,
            color = KidTextMuted
        )

        // Bottom Pieces to match
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 70.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            remainingPieces.forEach { piece ->
                val isSelected = selectedPiece?.id == piece.id
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) KidPurpleLight.copy(alpha = 0.35f) else Color.White)
                        .border(
                            width = if (isSelected) 3.dp else 1.5.dp,
                            color = if (isSelected) KidPurple else piece.color,
                            shape = CircleShape
                        )
                        .clickable {
                            soundManager.playPopSound()
                            selectedPiece = piece
                            soundManager.speak(piece.name)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = piece.emoji, fontSize = 38.sp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. SLIDING PUZZLE GAME (3x3)
// -------------------------------------------------------------
@Composable
private fun SlidePuzzleGame(
    soundManager: KidsSoundManager,
    onWon: () -> Unit
) {
    // 3x3 puzzle with numbers 1..8 and 0 as empty slot
    val goal = listOf(1, 2, 3, 4, 5, 6, 7, 8, 0)
    var board by remember {
        // Solvable pre-shuffled configuration
        mutableStateOf(listOf(1, 2, 3, 4, 0, 5, 7, 8, 6))
    }
    var moves by remember { mutableIntStateOf(0) }
    var isSolved by remember { mutableStateOf(false) }

    val emojis = listOf("", "🦁", "🐰", "🐶", "🐱", "🐸", "🐘", "🐼", "⭐")

    fun canMove(index: Int): Boolean {
        val emptyIndex = board.indexOf(0)
        val row = index / 3
        val col = index % 3
        val emptyRow = emptyIndex / 3
        val emptyCol = emptyIndex % 3
        return (kotlin.math.abs(row - emptyRow) + kotlin.math.abs(col - emptyCol)) == 1
    }

    fun makeMove(index: Int) {
        if (!canMove(index) || isSolved) return
        val emptyIndex = board.indexOf(0)
        val newBoard = board.toMutableList()
        newBoard[emptyIndex] = newBoard[index]
        newBoard[index] = 0
        board = newBoard
        moves++
        soundManager.playPopSound()

        if (newBoard == goal) {
            isSolved = true
            soundManager.playCelebrationFanfare()
            soundManager.speak("Hooray! You solved the puzzle in $moves moves! You earned 20 stars!")
            onWon()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Sliding Animal Puzzle 🖼️", fontWeight = FontWeight.Black, fontSize = 18.sp, color = KidPurple)
                Text("Moves: $moves", fontSize = 13.sp, color = KidTextMuted)
            }

            Button(
                onClick = {
                    board = listOf(1, 2, 3, 4, 0, 5, 7, 8, 6).shuffled()
                    moves = 0
                    isSolved = false
                    soundManager.playPopSound()
                },
                colors = ButtonDefaults.buttonColors(containerColor = KidPurple),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Shuffle")
            }
        }

        // 3x3 Grid Board
        Box(
            modifier = Modifier
                .size(310.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFE2E8F0))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
            ) {
                items(board.indices.toList()) { idx ->
                    val value = board[idx]
                    if (value == 0) {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.3f))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(KidYellowLight)
                                .border(2.dp, KidYellow, RoundedCornerShape(16.dp))
                            .clickable { makeMove(idx) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = emojis.getOrElse(value) { "⭐" }, fontSize = 32.sp)
                                Text(text = "$value", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = KidTextDark)
                            }
                        }
                    }
                }
            }
        }

        if (isSolved) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = KidGreenLight,
                modifier = Modifier.padding(bottom = 70.dp)
            ) {
                Text(
                    text = "🏆 Puzzle Solved! +20 Stars Awarded! ⭐",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = KidTextDark,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            Text(
                text = "Tap any tile next to the empty slot to slide it!",
                fontSize = 12.sp,
                color = KidTextMuted,
                modifier = Modifier.padding(bottom = 70.dp)
            )
        }
    }
}

// -------------------------------------------------------------
// 3. MEMORY CARDS MATCH GAME
// -------------------------------------------------------------
data class MemoryCard(val id: Int, val emoji: String, var isFlipped: Boolean = false, var isMatched: Boolean = false)

@Composable
private fun MemoryCardsGame(
    soundManager: KidsSoundManager,
    onWon: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val initialDeck = remember {
        val emojis = listOf("🦁", "🐰", "🐶", "🍓", "🚀", "⭐")
        val pairs = (emojis + emojis).shuffled()
        pairs.mapIndexed { index, emoji ->
            MemoryCard(id = index, emoji = emoji)
        }
    }

    var cards by remember { mutableStateOf(initialDeck) }
    var flippedIndices by remember { mutableStateOf(listOf<Int>()) }
    var isProcessing by remember { mutableStateOf(false) }
    var gameWon by remember { mutableStateOf(false) }

    fun onCardClicked(index: Int) {
        if (isProcessing || cards[index].isFlipped || cards[index].isMatched) return

        soundManager.playCardFlip()
        val newCards = cards.toMutableList()
        newCards[index] = newCards[index].copy(isFlipped = true)
        cards = newCards

        val newFlipped = flippedIndices + index
        flippedIndices = newFlipped

        if (newFlipped.size == 2) {
            isProcessing = true
            val first = newCards[newFlipped[0]]
            val second = newCards[newFlipped[1]]

            if (first.emoji == second.emoji) {
                soundManager.playCorrectChime()
                newCards[newFlipped[0]] = newCards[newFlipped[0]].copy(isMatched = true)
                newCards[newFlipped[1]] = newCards[newFlipped[1]].copy(isMatched = true)
                cards = newCards
                flippedIndices = emptyList()
                isProcessing = false

                if (newCards.all { it.isMatched }) {
                    gameWon = true
                    soundManager.playCelebrationFanfare()
                    soundManager.speak("Amazing! You matched all pairs! You earned 20 stars!")
                    onWon()
                }
            } else {
                soundManager.playWrongBuzz()
                coroutineScope.launch {
                    delay(800)
                    newCards[newFlipped[0]] = newCards[newFlipped[0]].copy(isFlipped = false)
                    newCards[newFlipped[1]] = newCards[newFlipped[1]].copy(isFlipped = false)
                    cards = newCards
                    flippedIndices = emptyList()
                    isProcessing = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Memory Cards Flip 🧠", fontWeight = FontWeight.Black, fontSize = 18.sp, color = KidPurple)

            IconButton(
                onClick = {
                    val emojis = listOf("🦁", "🐰", "🐶", "🍓", "🚀", "⭐")
                    val pairs = (emojis + emojis).shuffled()
                    cards = pairs.mapIndexed { index, emoji ->
                        MemoryCard(id = index, emoji = emoji)
                    }
                    flippedIndices = emptyList()
                    gameWon = false
                    soundManager.playPopSound()
                }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = KidPurple)
            }
        }

        // 3x4 Grid of Cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().heightIn(max = 380.dp),
            userScrollEnabled = false
        ) {
            items(cards.indices.toList()) { idx ->
                val card = cards[idx]
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            when {
                                card.isMatched -> KidGreenLight
                                card.isFlipped -> Color.White
                                else -> KidPurple
                            }
                        )
                        .border(
                            width = 2.dp,
                            color = if (card.isMatched) KidGreen else KidPurpleLight,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onCardClicked(idx) },
                    contentAlignment = Alignment.Center
                ) {
                    if (card.isFlipped || card.isMatched) {
                        Text(text = card.emoji, fontSize = 38.sp)
                    } else {
                        Text(text = "❓", fontSize = 28.sp)
                    }
                }
            }
        }

        Text(
            text = if (gameWon) "🎉 Master Memory! You Won! ⭐" else "Find all 6 cute pairs!",
            fontSize = 13.sp,
            color = KidTextMuted,
            modifier = Modifier.padding(bottom = 70.dp)
        )
    }
}

// -------------------------------------------------------------
// 4. BALLOON POP PHONICS GAME
// -------------------------------------------------------------
data class Balloon(
    val id: Int,
    val label: String,
    val color: Color,
    val xPercent: Float,
    val yPercent: Float
)

@Composable
private fun BalloonPopGame(
    soundManager: KidsSoundManager,
    onPopReward: () -> Unit
) {
    var score by remember { mutableIntStateOf(0) }
    var balloons by remember {
        mutableStateOf(
            listOf(
                Balloon(1, "A", KidCoral, 0.2f, 0.25f),
                Balloon(2, "B", KidBlue, 0.75f, 0.3f),
                Balloon(3, "C", KidGreen, 0.45f, 0.5f),
                Balloon(4, "1", KidYellow, 0.25f, 0.7f),
                Balloon(5, "2", KidPink, 0.75f, 0.65f),
                Balloon(6, "⭐", KidPurple, 0.5f, 0.85f)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Balloon Pop Game! 🎈", fontWeight = FontWeight.Black, fontSize = 18.sp, color = KidCoralDark)
                Text("Tap balloons to pop & hear letters!", fontSize = 12.sp, color = KidTextMuted)
            }

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = KidYellowSoft
            ) {
                Text(
                    text = "Popped: $score 🎈",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = KidTextDark,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Floating Sky Box
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD), Color(0xFFF0FDF4))
                    )
                )
        ) {
            val width = maxWidth
            val height = maxHeight

            balloons.forEach { balloon ->
                val xPos = width * balloon.xPercent - 36.dp
                val yPos = height * balloon.yPercent - 36.dp

                Box(
                    modifier = Modifier
                        .offset(x = xPos, y = yPos)
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(balloon.color)
                        .clickable {
                            soundManager.playPopSound()
                            soundManager.speak(balloon.label)
                            score++
                            onPopReward()

                            // Spawn new balloon randomly
                            val letters = listOf("A", "B", "C", "D", "E", "1", "2", "3", "5", "⭐", "🍎", "🐶")
                            val colors = listOf(KidCoral, KidBlue, KidGreen, KidYellow, KidPink, KidPurple)
                            balloons = balloons.map {
                                if (it.id == balloon.id) {
                                    Balloon(
                                        id = it.id,
                                        label = letters.random(),
                                        color = colors.random(),
                                        xPercent = (15..80).random() / 100f,
                                        yPercent = (15..85).random() / 100f
                                    )
                                } else it
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = balloon.label,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}

// -------------------------------------------------------------
// 5. RAINBOW MAGIC DRAWING & TRACING CANVAS
// -------------------------------------------------------------
data class DrawPath(val path: Path, val color: Color, val strokeWidth: Float)

@Composable
private fun RainbowDrawGame(
    soundManager: KidsSoundManager,
    onSaveReward: () -> Unit
) {
    var currentColor by remember { mutableStateOf(KidCoral) }
    var strokeWidth by remember { mutableFloatStateOf(16f) }
    val paths = remember { mutableStateListOf<DrawPath>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var currentTemplate by remember { mutableStateOf("A") }

    val colors = listOf(
        KidCoral,
        KidYellow,
        KidGreen,
        KidBlue,
        KidPurple,
        KidPink,
        Color.White // Eraser
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Rainbow Magic Art 🎨", fontWeight = FontWeight.Black, fontSize = 18.sp, color = KidPurple)
                Text("Trace letter $currentTemplate or draw anything!", fontSize = 12.sp, color = KidTextMuted)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("A", "B", "1", "⭐").forEach { t ->
                    Surface(
                        shape = CircleShape,
                        color = if (currentTemplate == t) KidPurple else Color.White,
                        border = BorderStroke(1.dp, KidPurple),
                        modifier = Modifier.clickable {
                            soundManager.playPopSound()
                            currentTemplate = t
                        }
                    ) {
                        Text(
                            text = t,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (currentTemplate == t) Color.White else KidPurple,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Drawing Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF2D3436)) // Night glowing canvas
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val path = Path().apply { moveTo(offset.x, offset.y) }
                            currentPath = path
                        },
                        onDrag = { change, _ ->
                            currentPath?.lineTo(change.position.x, change.position.y)
                        },
                        onDragEnd = {
                            currentPath?.let {
                                paths.add(DrawPath(it, currentColor, strokeWidth))
                                currentPath = null
                            }
                        }
                    )
                }
        ) {
            // Watermark letter for tracing
            Text(
                text = currentTemplate,
                fontSize = 180.sp,
                fontWeight = FontWeight.Black,
                color = Color.White.copy(alpha = 0.12f),
                modifier = Modifier.align(Alignment.Center)
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                paths.forEach { drawPath ->
                    drawPath(
                        path = drawPath.path,
                        color = drawPath.color,
                        style = Stroke(
                            width = drawPath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
                currentPath?.let {
                    drawPath(
                        path = it,
                        color = currentColor,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Color Palette & Tools
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                colors.forEach { c ->
                    val isSelected = currentColor == c
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 36.dp else 28.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(
                                2.dp,
                                if (isSelected) Color.Black else Color.LightGray,
                                CircleShape
                            )
                            .clickable {
                                soundManager.playPopSound()
                                currentColor = c
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (c == Color.White) {
                            Text("🧹", fontSize = 14.sp)
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {
                        soundManager.playPopSound()
                        paths.clear()
                    }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Clear", tint = KidCoral)
                }

                Button(
                    onClick = {
                        soundManager.playCelebrationFanfare()
                        soundManager.speak("Super artwork! You earned 10 stars!")
                        onSaveReward()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KidGreen),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Save 🌟")
                }
            }
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}

// -------------------------------------------------------------
// 6. KIDS AUDIO QUIZ GAME
// -------------------------------------------------------------
@Composable
private fun KidsQuizGame(
    soundManager: KidsSoundManager,
    onWon: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var score by remember { mutableIntStateOf(0) }

    val question = KidsLearningData.QUIZ_LIST[currentIndex]

    LaunchedEffect(currentIndex) {
        soundManager.speak(question.speechPrompt)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question ${currentIndex + 1} of ${KidsLearningData.QUIZ_LIST.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = KidPurple
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = KidYellowSoft
                ) {
                    Text(
                        text = "Score: $score ⭐",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = KidTextDark,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Question Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(2.dp, KidPurpleLight.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = question.visualHint, fontSize = 54.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.question,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = KidTextDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                IconButton(
                    onClick = {
                        soundManager.playCorrectChime()
                        soundManager.speak(question.speechPrompt)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Read aloud",
                        tint = KidPurple,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Options
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            question.options.forEachIndexed { optIndex, text ->
                val isSelected = selectedOption == optIndex
                val optColor = when {
                    isSelected && isCorrect == true -> KidGreen
                    isSelected && isCorrect == false -> KidCoral
                    else -> Color.White
                }

                Button(
                    onClick = {
                        if (selectedOption == null) {
                            selectedOption = optIndex
                            if (optIndex == question.correctIndex) {
                                isCorrect = true
                                score += 10
                                soundManager.playCorrectChime()
                                soundManager.speak(question.explanation)
                                onWon()
                            } else {
                                isCorrect = false
                                soundManager.playWrongBuzz()
                                soundManager.speak("Nice try! Try next question!")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = optColor,
                        contentColor = if (isSelected) Color.White else KidTextDark
                    ),
                    border = BorderStroke(1.5.dp, KidPurpleLight)
                ) {
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Next Question
        if (selectedOption != null) {
            Button(
                onClick = {
                    soundManager.playPopSound()
                    selectedOption = null
                    isCorrect = null
                    currentIndex = (currentIndex + 1) % KidsLearningData.QUIZ_LIST.size
                },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(50.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KidPurple)
            ) {
                Text("Next Question ➡️", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Spacer(modifier = Modifier.height(50.dp))
        }

        Spacer(modifier = Modifier.height(70.dp))
    }
}
