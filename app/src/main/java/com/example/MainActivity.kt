package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.KidsSoundManager
import com.example.data.DataRepository
import com.example.ui.about.AboutScreen
import com.example.ui.drawer.*
import com.example.ui.home.HomeScreen
import com.example.ui.learn.LearnScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.practice.PracticeScreen
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var soundManager: KidsSoundManager
    private lateinit var repository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        soundManager = KidsSoundManager(this)
        repository = DataRepository(this)

        setContent {
            MyApplicationTheme {
                KidsAppRoot(
                    soundManager = soundManager,
                    repository = repository
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

enum class NavigationTab(val label: String) {
    HOME("Home"),
    LEARN("Learn"),
    PRACTICE("Practice"),
    ABOUT_US("About Us")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KidsAppRoot(
    soundManager: KidsSoundManager,
    repository: DataRepository
) {
    val profile by repository.profile.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var selectedLearnCategory by remember { mutableIntStateOf(0) }
    var selectedPracticeGame by remember { mutableIntStateOf(0) }

    // Dialog controllers
    var showProfileDialog by remember { mutableStateOf(false) }
    var showLeaderboardDialog by remember { mutableStateOf(false) }
    var showBadgesDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showSoundDialog by remember { mutableStateOf(false) }
    var showParentGateDialog by remember { mutableStateOf(false) }

    // Sound toggle in top bar
    var isMuted by remember { mutableStateOf(!soundManager.isSoundEffectsEnabled) }

    // Onboarding check
    if (!profile.isOnboarded) {
        OnboardingScreen(
            soundManager = soundManager,
            onComplete = { name, age, avatarId ->
                repository.completeOnboarding(name, age, avatarId)
            },
            onRestoreRequest = {
                showBackupDialog = true
            }
        )

        if (showBackupDialog) {
            BackupRestoreDialog(
                profile = profile,
                repository = repository,
                soundManager = soundManager,
                onDismiss = { showBackupDialog = false }
            )
        }
        return
    }

    // Back handling
    BackHandler(enabled = drawerState.isOpen || currentTab != NavigationTab.HOME) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else if (currentTab != NavigationTab.HOME) {
            currentTab = NavigationTab.HOME
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            KidsDrawerContent(
                profile = profile,
                soundManager = soundManager,
                onEditProfileClick = { showProfileDialog = true },
                onLeaderboardClick = { showLeaderboardDialog = true },
                onBadgesClick = { showBadgesDialog = true },
                onBackupRestoreClick = { showBackupDialog = true },
                onSoundSettingsClick = { showSoundDialog = true },
                onParentZoneClick = { showParentGateDialog = true },
                onAboutDeveloperClick = { currentTab = NavigationTab.ABOUT_US },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "KIDS PUZZLE",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = KidPurple
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = KidYellowSoft
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("⭐", fontSize = 12.sp)
                                    Text(
                                        text = "${profile.stars}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = KidTextDark
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                soundManager.playPopSound()
                                coroutineScope.launch { drawerState.open() }
                            },
                            modifier = Modifier.testTag("drawer_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Drawer Menu",
                                tint = KidPurple
                            )
                        }
                    },
                    actions = {
                        // Quick Mute / Sound Toggle
                        IconButton(
                            onClick = {
                                val next = !isMuted
                                isMuted = next
                                soundManager.isSoundEffectsEnabled = !next
                                soundManager.isSpeechEnabled = !next
                                if (!next) {
                                    soundManager.playCorrectChime()
                                }
                            },
                            modifier = Modifier.testTag("sound_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Toggle sound",
                                tint = if (isMuted) Color.Gray else KidPurple
                            )
                        }

                        // Avatar button opens profile dialog
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(KidYellowLight)
                                .clickable {
                                    soundManager.playPopSound()
                                    showProfileDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = profile.currentAvatar.emoji, fontSize = 22.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("kids_bottom_navigation")
                ) {
                    // 1. HOME
                    NavigationBarItem(
                        selected = currentTab == NavigationTab.HOME,
                        onClick = {
                            soundManager.playPopSound()
                            currentTab = NavigationTab.HOME
                        },
                        icon = {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                        },
                        label = {
                            Text("HOME", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KidPurple,
                            selectedTextColor = KidPurple,
                            indicatorColor = KidPurpleLight.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_item_home")
                    )

                    // 2. LEARN
                    NavigationBarItem(
                        selected = currentTab == NavigationTab.LEARN,
                        onClick = {
                            soundManager.playPopSound()
                            currentTab = NavigationTab.LEARN
                        },
                        icon = {
                            Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Learn")
                        },
                        label = {
                            Text("LEARN", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KidPurple,
                            selectedTextColor = KidPurple,
                            indicatorColor = KidPurpleLight.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_item_learn")
                    )

                    // 3. PRACTICE
                    NavigationBarItem(
                        selected = currentTab == NavigationTab.PRACTICE,
                        onClick = {
                            soundManager.playPopSound()
                            currentTab = NavigationTab.PRACTICE
                        },
                        icon = {
                            Icon(Icons.Default.Extension, contentDescription = "Practice")
                        },
                        label = {
                            Text("PRACTICE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KidPurple,
                            selectedTextColor = KidPurple,
                            indicatorColor = KidPurpleLight.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_item_practice")
                    )

                    // 4. ABOUT US
                    NavigationBarItem(
                        selected = currentTab == NavigationTab.ABOUT_US,
                        onClick = {
                            soundManager.playPopSound()
                            currentTab = NavigationTab.ABOUT_US
                        },
                        icon = {
                            Icon(Icons.Default.Info, contentDescription = "About Us")
                        },
                        label = {
                            Text("ABOUT US", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = KidPurple,
                            selectedTextColor = KidPurple,
                            indicatorColor = KidPurpleLight.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag("nav_item_about_us")
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (currentTab) {
                    NavigationTab.HOME -> HomeScreen(
                        profile = profile,
                        soundManager = soundManager,
                        onNavigateToLearn = { categoryIndex ->
                            selectedLearnCategory = categoryIndex
                            currentTab = NavigationTab.LEARN
                        },
                        onNavigateToPractice = { gameIndex ->
                            selectedPracticeGame = gameIndex
                            currentTab = NavigationTab.PRACTICE
                        },
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        }
                    )

                    NavigationTab.LEARN -> LearnScreen(
                        initialCategory = selectedLearnCategory,
                        soundManager = soundManager,
                        onRewardStars = { amount, category ->
                            repository.addStars(amount, category)
                        }
                    )

                    NavigationTab.PRACTICE -> PracticeScreen(
                        initialGame = selectedPracticeGame,
                        soundManager = soundManager,
                        onRewardStars = { amount, category ->
                            repository.addStars(amount, category)
                        }
                    )

                    NavigationTab.ABOUT_US -> AboutScreen(
                        soundManager = soundManager,
                        onOpenBackupDialog = { showBackupDialog = true }
                    )
                }
            }
        }
    }

    // Dialogs overlay
    if (showProfileDialog) {
        ProfileEditDialog(
            profile = profile,
            soundManager = soundManager,
            onDismiss = { showProfileDialog = false },
            onSave = { name, age, avatarId ->
                repository.updateProfile(name, age, avatarId)
            }
        )
    }

    if (showLeaderboardDialog) {
        LeaderboardDialog(
            leaderboard = repository.getLeaderboard(),
            soundManager = soundManager,
            onDismiss = { showLeaderboardDialog = false }
        )
    }

    if (showBadgesDialog) {
        BadgesDialog(
            profile = profile,
            soundManager = soundManager,
            onDismiss = { showBadgesDialog = false }
        )
    }

    if (showBackupDialog) {
        BackupRestoreDialog(
            profile = profile,
            repository = repository,
            soundManager = soundManager,
            onDismiss = { showBackupDialog = false }
        )
    }

    if (showSoundDialog) {
        SoundSettingsDialog(
            soundManager = soundManager,
            onDismiss = { showSoundDialog = false }
        )
    }

    if (showParentGateDialog) {
        ParentGateDialog(
            soundManager = soundManager,
            onSuccess = {
                showParentGateDialog = false
                showSoundDialog = true
            },
            onDismiss = { showParentGateDialog = false }
        )
    }
}
