package com.paywings.oauth.android.sample_app.ui.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.hbb20.CCPCountry
import com.paywings.oauth.android.sample_app.R


@ExperimentalAnimationApi
@Composable
fun PhoneNumberInput(
    country: CCPCountry,
    phoneNumber: String,
    placeholderText: String,
    phoneNumberLength: Int,
    inputEnabled: Boolean,
    onPhoneNumberChange: (newPhoneNumber: String) -> Unit,
    onDropDownShow: () -> Unit,
    phoneNumberInputTextFieldFocusRequester: FocusRequester
) {
    Row(
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    )
    {
        CountrySelectDropDownWithLowerWidth(
            value = "+"+country.phoneCode,
            flagResID = country.flagID,
            inputEnabled = inputEnabled,
            onDropDownShow = onDropDownShow,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        PhoneNumberInputTextField(
            phoneNumber = phoneNumber,
            placeholderText = placeholderText,
            phoneNumberLength = phoneNumberLength,
            inputEnabled = inputEnabled,
            onPhoneNumberChange = onPhoneNumberChange,
            focusRequester = phoneNumberInputTextFieldFocusRequester,
            modifier = Modifier.weight(1.3f)
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun PhoneNumberInputTextField(
    phoneNumber: String,
    placeholderText: String,
    phoneNumberLength: Int,
    inputEnabled: Boolean,
    onPhoneNumberChange: (newPhoneNumber: String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    var cancelIconVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .focusRequester(focusRequester),
        value = phoneNumber,
        onValueChange = {
            if (it.isDigitsOnly() && (phoneNumberLength == 0 || it.length <= phoneNumberLength)) {
                onPhoneNumberChange(it)
            }
            cancelIconVisible = it.isNotEmpty()
        },
        label = { Text(stringResource(id = R.string.sign_in_phone_number_input_screen_phone_number_label)) },
        placeholder = { placeholderText.takeIf { it.isNotBlank() }?.let { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, autoCorrect = false),
        maxLines = 1,
        singleLine = true,
        enabled = inputEnabled,
        trailingIcon = {
            AnimatedVisibility(
                visible = cancelIconVisible,
                enter = fadeIn(
                    // Overwrites the initial value of alpha to 0.4f for fade in, 0 by default
                    initialAlpha = 0.4f
                ),
                exit = fadeOut(
                    // Overwrites the default animation with tween
                    animationSpec = tween(durationMillis = 250)
                )
            ) {
                IconButton(
                    onClick = {
                        onPhoneNumberChange("")
                        cancelIconVisible = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = null
                    )
                }
            }
        }
    )
}


@Composable
fun CountrySelectDropDownWithLowerWidth(
    value: String,
    flagResID: Int?,
    inputEnabled: Boolean,
    onDropDownShow: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier, Alignment.Center){
        OutlinedTextField(
            modifier = Modifier
                .onFocusChanged { focusState -> if (focusState.isFocused) onDropDownShow() }
                .fillMaxWidth(),
            value = " ",
            onValueChange = { },
            singleLine = true,
            readOnly = true,
            enabled = inputEnabled,
            label = {
                Text(
                    maxLines = 1,
                    color = MaterialTheme.colors.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    text = stringResource(id = R.string.sign_in_phone_number_input_screen_country_code_label)
                )
            }
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            flagResID?.let {
                Image(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier
                        .weight(2.5f)
                        .clickable(
                            enabled = inputEnabled
                        ) { onDropDownShow() }
                        .then(Modifier.size(18.dp))
                )
            }
            Text(text = value.trim(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(5.5f)
                    .alpha(if (inputEnabled) 1.0f else ContentAlpha.disabled)
                )
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .clickable(
                        enabled = inputEnabled,
                    ) { onDropDownShow() }
                    .then(Modifier.size(18.dp))
                    .weight(2f)
                    .alpha(if (inputEnabled) 1.0f else ContentAlpha.disabled)
            )
        }
    }
}


