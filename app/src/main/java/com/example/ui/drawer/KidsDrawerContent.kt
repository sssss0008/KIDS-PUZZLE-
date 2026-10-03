package com.example.ui.drawer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.KidsSoundManager
import com.example.data.KidProfile
import com.example.ui.theme.*

@Composable
fun KidsDrawerContent(
    profile: KidProfile,
    soundManager: KidsSoundManager,
    onEditProfileClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onBadgesClick: () -> Unit,
    onBackupRestoreClick: () -> Unit,
    onSoundSettingsClick: () -> Unit,
    onParentZoneClick: () -> Unit,
    onAboutDeveloperClick: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Drawer Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(KidPurple, KidPurpleDark)
                        )
                    )
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable {
                                    soundManager.playPopSound()
                                    onEditProfileClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = profile.currentAvatar.emoji, fontSize = 40.sp)
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = KidYellowSoft
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("⭐", fontSize = 14.sp)
                                Text(
                                    text = "${profile.stars}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = KidTextDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = profile.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "Level ${profile.level} • ${profile.rankTitle}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Items
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DrawerItemRow(
                    icon = Icons.Default.Edit,
                    iconColor = KidCoral,
                    title = "Edit Kid Profile",
                    subtitle = "Change name, age, or avatar",
                    tag = "drawer_edit_profile",
                    onClick = {
                        soundManager.playPopSound()
                        onCloseDrawer()
                        onEditProfileClick()
                    }
                )

                DrawerItemRow(
                    icon = Icons.Default.EmojiEvents,
                    iconColor = KidYellow,
                    title = "Kids Hall of Fame",
                    subtitle = "View rankings & top contenders",
                    tag = "drawer_leaderboard",
                    onClick = {
                        soundManager.playPopSound()
                        onCloseDrawer()
                        onLeaderboardClick()
                    }
                )

                DrawerItemRow(
                    icon = Icons.Default.MilitaryTech,
                    iconColor = KidPurple,
                    title = "Trophies & Badges",
                    subtitle = "${profile.badgesUnlocked.size} badges unlocked",
                    tag = "drawer_badges",
                    onClick = {
                        soundManager.playPopSound()
                        onCloseDrawer()
                        onBadgesClick()
                    }
                )

                DrawerItemRow(
                    icon = Icons.Default.CloudSync,
                    iconColor = KidGreen,
                    title = "Cloud Passport & Backup",
                    subtitle = "Code: ${profile.passportCode}",
                    tag = "drawer_backup",
                    onClick = {
                        soundManager.playPopSound()
                        onCloseDrawer()
                        onBackupRestoreClick()
                    }
                )

                DrawerItemRow(
                    icon = Icons.Default.VolumeUp,
                    iconColor = KidBlue,
                    title = "Sound & Voice Settings",
                    subtitle = "Narration voice, volume, speed",
                    tag = "drawer_sound_settings",
                    onClick = {
                        soundManager.playPopSound()
                        onCloseDrawer()
                        onSoundSettingsClick()
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                DrawerItemRow(
                    icon = Icons.Default.Lock,
                    iconColor = Color.DarkGray,
                    title = "Grown-Up Zone",
                    subtitle = "Parent gate protected",
                    tag = "drawer_parent_zone",
                    onClick = {
                        soundManager.playPopSound()
                        onCloseDrawer()
                        onParentZoneClick()
                    }
                )

                DrawerItemRow(
                    icon = Icons.Default.Info,
                    iconColor = KidPurple,
                    title = "About Awiskar Acharya",
                    subtitle = "Developer & educational vision",
                    tag = "drawer_about",
                    onClick = {
                        soundManager.playPopSound()
                        onCloseDrawer()
                        onAboutDeveloperClick()
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "KIDS PUZZLE & LEARN",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = KidPurpleLight
                    )
                    Text(
                        text = "Crafted with ❤️ by Awiskar Acharya",
                        fontSize = 11.sp,
                        color = KidTextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerItemRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
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
