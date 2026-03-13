package com.emirbardakci.uconnectfake.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emirbardakci.uconnectfake.ui.theme.UconnectButtonGray
import com.emirbardakci.uconnectfake.ui.theme.UconnectTextWhite

@Composable
fun UconnectAppButton(
    appName: String,
    iconResId: Int,
    backgroundColor: Color,
    onAppClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(16.dp)
            .clickable { onAppClick() }
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = appName,
            tint = UconnectTextWhite,
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = appName,
            color = UconnectTextWhite,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun UconnectBottomNavItem(
    iconResId: Int,
    label: String,
    onClick: () -> Unit,
    tint: Color = UconnectTextWhite
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .clickable { onClick() }
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.size(36.dp),
            tint = tint
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = tint,
            fontSize = 14.sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun UconnectTopBar(
    title: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 16.dp)
    ) {
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_revert),
            contentDescription = "Back",
            tint = UconnectTextWhite,
            modifier = Modifier
                .size(24.dp)
                .clickable { onBackClick() }
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "uconnect",
                color = UconnectTextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = " | $title",
                color = UconnectTextWhite,
                fontSize = 16.sp
            )
        }
        
        Icon(
            painter = painterResource(id = android.R.drawable.ic_menu_preferences),
            contentDescription = "Settings",
            tint = UconnectTextWhite,
            modifier = Modifier
                .size(24.dp)
                .clickable { onSettingsClick() }
        )
    }
}

@Composable
fun NavigationButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isSelected: Boolean = false
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) UconnectButtonGray else Color.Transparent)
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = UconnectTextWhite
        )
    }
} 