package com.speech.main.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.speech.common_ui.ui.NoRippleInteractionSource
import com.speech.designsystem.theme.CloudGray
import com.speech.designsystem.theme.PrimaryActive
import com.speech.designsystem.theme.PrimaryDefault
import com.speech.navigation.MyPageBaseRoute
import com.speech.navigation.PracticeBaseRoute
import com.speech.navigation.Route
import com.speech.navigation.isRouteInHierarchy

@Composable
internal fun AppBottomBar(
    currentDestination: NavDestination?,
    navigateToBottomNaviDestination: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .height(51.dp)
            .drawBehind {
                val shadowHeight = 1.dp.toPx()

                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.1f),
                        ),
                        startY = 0f,
                        endY = shadowHeight
                    ),
                    topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                    size = Size(size.width, shadowHeight)
                )
            }
    ) {
        NavigationBar(
            containerColor = Color.White,
            modifier = modifier
                .align(Alignment.BottomCenter)
                .height(50.dp)
        ) {
            TopLevelDestination.entries.forEach { topLevelRoute ->
                NavigationBarItem(
                    icon = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(top = 2.dp),
                        ) {
                            Icon(
                                painter = painterResource(topLevelRoute.icon),
                                contentDescription = topLevelRoute.contentDescription,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    },
                    onClick = {
                        when (topLevelRoute) {
                            TopLevelDestination.Practice -> navigateToBottomNaviDestination(
                                PracticeBaseRoute
                            )

                            TopLevelDestination.MYPage -> navigateToBottomNaviDestination(
                                MyPageBaseRoute
                            )
                        }
                    },
                    selected = currentDestination.isRouteInHierarchy(topLevelRoute.route),
                    interactionSource = remember { NoRippleInteractionSource() },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryActive,
                        unselectedIconColor = CloudGray,
                        selectedTextColor = PrimaryActive,
                        unselectedTextColor = CloudGray,
                        indicatorColor = Color.Transparent
                    ),
                )
            }
        }

    }
}