package com.speech.auth.graph

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.auth.graph.LoginViewModel.LoginEvent
import com.speech.common.event.SpeechMateEvent
import com.speech.common.util.clickable

@Composable
internal fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToPractice: () -> Unit,
) {
    LaunchedEffect(true) {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is LoginEvent.LoginSuccess -> navigateToPractice()
                is LoginEvent.LoginFailure -> {
                    viewModel.eventHelper.sendEvent(SpeechMateEvent.ShowSnackBar("로그인에 실패했습니다."))
                }
            }
        }
    }

    LoginScreen(
        loginKakao = {},
        onLoginFailure = { viewModel.eventHelper.sendEvent(SpeechMateEvent.ShowSnackBar("로그인에 실패했습니다.")) },
        navigateToPractice = navigateToPractice
    )
}

@Composable
private fun LoginScreen(
    navigateToPractice: () -> Unit,
    loginKakao: () -> Unit,
    onLoginFailure: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))

        Text("SpeechMate", style = SpeechMateTheme.typography.headingXLB)

        Spacer(Modifier.height(30.dp))

        Image(
            painter = painterResource(com.speech.designsystem.R.drawable.kakao_login),
            contentDescription = "카카오로 로그인하기",
            modifier = Modifier.clickable {
                loginKakao(context, onSuccess = { idToken ->
                    Log.d("kakao auth", idToken)
                    navigateToPractice()
                }, onFailure = { onLoginFailure() })
            }
        )

        Spacer(Modifier.weight(1f))
    }
}

private fun loginKakao(
    context: Context,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            onFailure()
        } else if (token?.idToken != null) {
            onSuccess(token.idToken!!)
        }
    }

    UserApiClient.instance.apply {
        if (isKakaoTalkLoginAvailable(context)) {
            loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    loginWithKakaoAccount(context, callback = callback)
                } else if (token != null) {
                    onSuccess(token.idToken!!)
                }
            }
        } else {
            loginWithKakaoAccount(context, callback = callback)
        }
    }

}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        loginKakao = {},
        onLoginFailure = {},
        navigateToPractice = {}
    )
}