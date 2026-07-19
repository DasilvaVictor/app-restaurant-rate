package com.chiris.app.restaurant_rate.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chiris.app.restaurant_rate.R

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit
){

    val loginSuccess = viewModel.loginSuccess.value

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
            viewModel.resetLoginState()
        }
    }

    Box(Modifier.fillMaxSize().padding(16.dp)) {
        Login(
            Modifier.align(Alignment.Center),
            viewModel = viewModel
        )
    }
}


@Composable
fun Login(modifier: Modifier, viewModel: LoginViewModel) {
    Column(modifier = modifier) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally) )
        Spacer(modifier = Modifier.padding(8.dp))
        EmailField(viewModel)
        Spacer(modifier = Modifier.padding(8.dp))
        PasswordField(viewModel)
        Spacer(modifier = Modifier.padding(8.dp))
        ForgotPassword(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        LoginButton(viewModel)
    }
}

@Composable
fun LoginButton(viewModel: LoginViewModel){
    Button(
        onClick = { viewModel.login() },
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFA23B3B),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF2196F3),
            disabledContentColor = Color.White
        )
    ) {
        Text(text = "Iniciar Sesión")
    }
}

@Composable
fun EmailField(viewModel: LoginViewModel){
    TextField(
        value = viewModel.email,
        onValueChange = { viewModel.email = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = "Email") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray
        )
    )
}

@Composable
fun PasswordField(viewModel: LoginViewModel){
    TextField(
        value = viewModel.password,
        onValueChange = { viewModel.password = it },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = PasswordVisualTransformation(),
        placeholder = { Text(text = "Password") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray
        )
    )
}

@Composable
fun ForgotPassword(modifier: Modifier){
    Text(
        text = "Olvidaste la contraseña?",
        modifier = modifier.clickable {},
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFF5722)
    )
}

@Composable
fun HeaderImage(modifier: Modifier){
    Image(painter = painterResource(id = R.drawable.logo_restaurante),
        contentDescription = "Header",
        modifier = modifier)
}