package com.paywings.oauth.android.sample_app.ui.screens.dialogs.country

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hbb20.CCPCountry
import com.paywings.oauth.android.sample_app.R
import com.paywings.oauth.android.sample_app.ui.screens.components.ScreenTitleWithCloseButtonIcon
import com.paywings.oauth.android.sample_app.ui.theme.dialog
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import java.util.*

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun CountrySelectDialog(
    dialogState: MaterialDialogState,
    countries: List<CCPCountry>,
    searchCountryNameValue: String,
    onSearchValueChange: (newSearchValue: String) -> Unit,
    onSelectedCountry: (selectedCountry: CCPCountry) -> Unit,
    onClose: () -> Unit
) {
    MaterialDialog(
        dialogState = dialogState,
        autoDismiss = false,
        shape = MaterialTheme.shapes.dialog,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ) {
        CountrySelectDialogContent(
            countries = countries,
            searchCountryNameValue = searchCountryNameValue,
            onSearchValueChange = onSearchValueChange,
            onSelectedCountry = onSelectedCountry,
            onClose = onClose
        )
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun CountrySelectDialogContent(
    countries: List<CCPCountry>,
    searchCountryNameValue: String,
    onSearchValueChange: (newSearchValue: String) -> Unit,
    onSelectedCountry: (selectedCountry: CCPCountry) -> Unit,
    onClose: () -> Unit
) {
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(dimensionResource(id = R.dimen.corner_default_radius)),
            color = MaterialTheme.colors.background  //Color.LightGray
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 16.dp)
            )
            {
                Column {
                    ScreenTitleWithCloseButtonIcon(
                        titleResId = R.string.country_select_dialog_title,
                        onClose = onClose
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SearchTextField(
                        searchLabelResId = R.string.country_select_dialog_search_label,
                        searchPlaceholderResId = R.string.country_select_dialog_search_placeholder,
                        searchCountryNameValue = searchCountryNameValue
                    ) { newSearchValue ->
                        onSearchValueChange(
                            newSearchValue
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    CountrySelectSearchList(countries = countries, onSelected = onSelectedCountry)
                }

            }
        }
    }
}


@ExperimentalAnimationApi
@Composable
fun SearchTextField(
    @StringRes searchLabelResId: Int,
    @StringRes searchPlaceholderResId: Int? = null,
    searchCountryNameValue: String,
    onSearchValueChange: (newSearchValue: String) -> Unit
) {
    var cancelIconVisible by remember { mutableStateOf(false) }
    var searchIconVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier
            .onFocusChanged { searchIconVisible = !it.isFocused }
            .fillMaxWidth(),
        value = searchCountryNameValue,
        onValueChange = {
            onSearchValueChange(it)
            cancelIconVisible = it.isNotEmpty()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            autoCorrect = false,
            capitalization = KeyboardCapitalization.Sentences
        ),
        singleLine = true,
        label = { Text(stringResource(id = searchLabelResId)) },
        placeholder = {
            searchPlaceholderResId?.let {
                Text(stringResource(id = searchPlaceholderResId))
            }
        },
        leadingIcon = {
            if (searchIconVisible) {
                Icon(imageVector = Icons.Filled.Search, contentDescription = null)
            }
        },
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
                        onSearchValueChange("")
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
fun CountrySelectItem(country: CCPCountry, onSelected: (selectedCountry: CCPCountry) -> Unit) {
    Row(
        modifier = Modifier
            .wrapContentHeight()
            .height(IntrinsicSize.Max)
            .clickable { onSelected(country) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .height(40.dp)
                .widthIn(max = 40.dp)
                .padding(
                    top = 2.dp,
                    bottom = 2.dp
                ),
            contentScale = ContentScale.Fit,
            painter = painterResource(country.flagID),
            contentDescription = null
        ) // empty holder
        Text(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 8.dp),
            style = MaterialTheme.typography.body2,
            text = "${country.name} (${country.nameCode.uppercase(Locale.ROOT)})"
        )
    }
}


@Composable
fun CountrySelectSearchList(
    countries: List<CCPCountry>,
    onSelected: (selectedCountry: CCPCountry) -> Unit
) {
    when (countries.isNotEmpty()) {
        true -> {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(
                    items = countries,
                    itemContent = { country ->
                        CountrySelectItem(country, onSelected)
                    }
                )
            }
        }
        false -> {
            Text(text = stringResource(id = R.string.country_select_dialog_search_country_not_found))
        }
    }
}