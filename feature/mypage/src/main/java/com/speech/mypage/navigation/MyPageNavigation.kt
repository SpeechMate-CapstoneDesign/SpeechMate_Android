package com.speech.mypage.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.speech.domain.model.speech.SpeechConfig
import com.speech.domain.model.speech.SpeechFileType
import com.speech.mypage.graph.mypage.MyPageRoute
import com.speech.mypage.graph.setting.SettingRoute
import com.speech.navigation.MyPageBaseRoute
import com.speech.navigation.MyPageGraph

fun NavController.navigateToMyPage(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.MyPageRoute, navOptions)
}

fun NavController.navigateToSetting(navOptions: NavOptions? = null) {
    navigate(MyPageGraph.SettingRoute, navOptions)
}

fun NavGraphBuilder.myPageNavGraph(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToPolicy: () -> Unit,
    navigateToInquiry: () -> Unit,
    navigateToSetting: () -> Unit,
    navigateToFeedBack: (Int, SpeechFileType, SpeechConfig) -> Unit
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
                navigateToBack = navigateBack,
                navigateToLogin = navigateToLogin,
                navigateToPolicy = navigateToPolicy,
                navigateToInquiry = navigateToInquiry
            )
        }
    }
}
