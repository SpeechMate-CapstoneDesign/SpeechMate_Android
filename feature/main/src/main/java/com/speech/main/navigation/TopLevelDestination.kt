package com.speech.main.navigation

import androidx.annotation.DrawableRes
import com.speech.designsystem.R
import com.speech.navigation.MyPageBaseRoute
import com.speech.navigation.PracticeBaseRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val route : KClass<*>,
    @param:DrawableRes val icon : Int,
    val label : String,
    val contentDescription: String,
) {
    Practice(
        route = PracticeBaseRoute::class,
        icon = R.drawable.ic_bottom_home,
        contentDescription = "연습",
        label = "홈"
    ),
    MyPage(
        route = MyPageBaseRoute::class,
        icon = R.drawable.ic_bottom_my,
        contentDescription = "마이페이지",
        label = "마이페이지"
    ),
}
