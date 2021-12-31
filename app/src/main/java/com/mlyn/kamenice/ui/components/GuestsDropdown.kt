package com.mlyn.kamenice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mlyn.kamenice.R
import com.mlyn.kamenice.data.Guest

@Composable
fun GuestsDropdown(guests: List<Guest>, onSelect: (Guest) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.TopStart)
    ) {
        Text(
            text = "%s: %s %s".format(
                stringResource(id = R.string.guest),
                guests[selectedIndex].name,
                guests[selectedIndex].surname
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { expanded = true }),
            textAlign = TextAlign.Center
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            guests.forEachIndexed { index, guest ->
                DropdownMenuItem(
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        onSelect(guest)
                    }
                ) {
                    Text(
                        text = "%s %s".format(guest.name, guest.surname),
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}