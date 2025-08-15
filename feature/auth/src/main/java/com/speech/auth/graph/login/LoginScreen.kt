package com.speech.auth.graph.login

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
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
import com.speech.common_ui.compositionlocal.LocalSnackbarHostState
import com.speech.designsystem.R
import com.speech.designsystem.theme.SpeechMateTheme
import com.speech.common_ui.util.clickable
import kotlinx.coroutines.launch


@Composable
internal fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToPractice: () -> Unit,
    navigateToOnBoarding: (String) -> Unit
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.container.sideEffectFlow.collect { sideEffect ->
            when (sideEffect) {
                is LoginSideEffect.NavigateToPractice -> navigateToPractice()
                is LoginSideEffect.NavigateToOnBoarding -> navigateToOnBoarding(sideEffect.idToken)
                is LoginSideEffect.ShowSnackBar -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(sideEffect.message)
                    }
                }
            }
        }
    }


    LoginScreen(
        loginKakao = { idToken -> viewModel.onIntent(LoginIntent.OnLoginClick(idToken)) },
        onLoginFailure = {
            scope.launch {
                snackbarHostState.showSnackbar("로그인에 실패했습니다")
            }
        },
    )
}

@Composable
fun LoginScreen(
    loginKakao: (String) -> Unit,
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

        Image(
            painter = painterResource(R.drawable.app_icon),
            contentDescription = "앱 아이콘",
            modifier = Modifier.size(250.dp)
        )

        Text("SpeechMate", style = SpeechMateTheme.typography.headingXLB)

        Spacer(Modifier.height(30.dp))

        Image(
            painter = painterResource(R.drawable.kakao_login),
            contentDescription = "카카오 로그인",
            modifier = Modifier.clickable {
                loginKakao(context, onSuccess = { idToken ->
                    Log.d("idToken", idToken)
                    loginKakao(idToken)
                }, onFailure = { onLoginFailure() })
            }
        )

        Spacer(Modifier.weight(2f))
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
    )
}