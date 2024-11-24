package com.example.appweather.app_ui.favorites_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.appweather.R

@Composable
fun CityControlBlock(
    isAddingCity: Boolean,
    onBack: () -> Unit,
    keyboardController: SoftwareKeyboardController?,
    onAddCityClick: () -> Unit,
    onCloseAddCityClick: () -> Unit,
    newCity: String,
    onCityValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val textFieldValue = remember { mutableStateOf(TextFieldValue(newCity)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        if (!isAddingCity) {
            Text(
                text = stringResource(id = R.string.favoriteh6),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.weight(1f),
                color = Color.White
            )
            IconButton(onClick = {
                onAddCityClick()
                keyboardController?.show()
            }){
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Добавить город",
                    tint = Color.White
                )
            }
        } else {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
            OutlinedTextField(
                value = newCity,
                label = { Text(text = stringResource(id = R.string.enter_city)) },
                onValueChange = { newText ->
                    // Оставляем только буквы
                    val filteredText = newText.filter { it.isLetter() || it == ' ' }
                    onCityValueChange(filteredText)
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Transparent)
                    .focusRequester(focusRequester),
                textStyle = TextStyle(color = Color.White),
                singleLine = true
            )
            IconButton(onClick = {
                onCloseAddCityClick()
                keyboardController?.hide()
            }) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Закрыть",
                    tint = Color.White
                )
            }
        }
    }
}