package com.example.appweather.app_ui.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appweather.R
import com.example.appweather.app_ui.components.BackgroundImage
import com.example.appweather.view_models.FavoritesViewModel

@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel, onBack: () -> Unit) {
    val favoriteCities by viewModel.favoriteCities.collectAsState(initial = emptyList())
    var newCity by remember { mutableStateOf("") }
    var isAddingCity by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current


    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    BackgroundImage()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack,
                modifier = Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    role = null,
                    enabled = true,
                    onClick = onBack
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            if (!isAddingCity) {
                Text(
                    text = "Управление городами",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f),
                    color = Color.White
                )
                IconButton(onClick = {
                    isAddingCity = true
                    keyboardController?.show()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Добавить город",
                        tint = Color.White
                    )
                }
            } else {
                OutlinedTextField(
                    value = newCity,
                    label = { Text(text = stringResource(id = R.string.enter_city)) },
                    onValueChange = { newCity = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newCity.isNotBlank()) {
                                viewModel.addCity(newCity.trim())
                                newCity = ""
                                isAddingCity = false
                            }
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    textStyle = TextStyle(color = Color.White),
                    singleLine = true
                )


                LaunchedEffect(isAddingCity) {
                    if (isAddingCity) {
                        focusRequester.requestFocus()
                    }
                }

                IconButton(onClick = {
                    isAddingCity = false
                    newCity = ""
                }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Закрыть",
                        tint = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (favoriteCities.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет избранных городов",
                    style = MaterialTheme.typography.h6.copy(color = Color.White),
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(favoriteCities) { city ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = 8.dp,
                        backgroundColor = Color.Transparent
                    ) {
                        // Градиентный фон карточки
                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color(0xFF154B78), Color(0xFF1E88E5))
                                    )
                                )
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Левый блок с текстом и значком
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = city,
                                        style = MaterialTheme.typography.h6,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp
                                    )

//                                  Spacer(modifier = Modifier.height(4.dp))
//                                    Text(
//                                        text = "Последнее обновление: ${getCurrentDate()}",
//                                        style = MaterialTheme.typography.caption,
//                                        color = Color.LightGray,
//                                        fontSize = 12.sp
//                                    )
                                }


                                IconButton(
                                    onClick = { viewModel.removeCity(city) },
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x55FFFFFF))
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Удалить",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
