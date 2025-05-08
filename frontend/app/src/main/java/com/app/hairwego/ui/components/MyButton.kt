package com.app.hairwego.ui.components

import com.ahmetocak.shoppingapp.presentation.designsystem.theme.HairwegoAppTheme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.ahmetocak.shoppingapp.utils.ComponentPreview
import com.app.hairwego.R

@Composable
fun MyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    buttonText: String
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(top = dimensionResource(id = R.dimen.two_level_margin)),
        shape = RoundedCornerShape(5.dp),
        onClick = onClick,
    ) {
        Text(
            text = buttonText,
            style = MaterialTheme.typography.titleMedium
        )
    }
}


@ComponentPreview
@Composable
private fun ShoppingButtonPreview() {
    HairwegoAppTheme {
        Surface {
           MyButton(onClick = {}, buttonText = "Click")
        }
    }
}