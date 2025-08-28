package com.speech.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.speech.mypage.graph.mypage.MyPageRoute
import com.speech.mypage.graph.setting.SettingRoute
import com.speech.navigation.MyPageBaseRoute
import com.speech.navigation.MyPageGraph
import com.speech.navigation.PracticeGraph

fun NavController.navigateToMyPgae(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.MyPageRoute, navOptions)
}

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.SettingRoute, navOptions)
}

fun NavGraphBuilder.myPageNavGraph(
    navigateBack: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToFeedBack: (Int) -> Unit
) {
    navigation<MyPageBaseRoute>(startDestination = MyPageGraph.MyPageRoute) {
        composable<MyPageGraph.MyPageRoute> {
            MyPageRoute(
                navigateToSetting = navigateToSetting,
                navigateToFeedBack = navigateToFeedBack
            )
        }

        composable<MyPageGraph.SettingRoute> {
            SettingRoute(
                navigateToBack = navigateBack
            )
        }
    }
}