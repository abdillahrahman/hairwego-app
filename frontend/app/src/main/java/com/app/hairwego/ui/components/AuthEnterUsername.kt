package com.app.hairwego.ui.components

import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import com.ahmetocak.shoppingapp.presentation.designsystem.theme.HairwegoAppTheme
import com.ahmetocak.shoppingapp.utils.ComponentPreview
import com.app.hairwego.R

@Composable
fun AuthEnterUsernameOtf(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    labelText: String
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.one_level_margin)),
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = labelText)
        },
        trailingIcon = {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        keyboardActions = KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        ),
        isError = isError,
        singleLine = true
    )
}

@ComponentPreview
@Composable
private fun AuthEnterUsernameOtfPreview() {
    HairwegoAppTheme {
        Surface {
            AuthEnterUsernameOtf(
                value = "",
                onValueChange = {},
                isError = false,
                labelText = stringResource(id = R.string.enter_username)
            )
        }
    }
}

@ComponentPreview
@Composable
private fun AuthEnterUsernameOtfErrorPreview() {
    HairwegoAppTheme {
        Surface {
            AuthEnterUsernameOtf(
                value = "",
                onValueChange = {},
                isError = true,
                labelText = stringResource(id = R.string.enter_valid_username)
            )
        }
    }
}
