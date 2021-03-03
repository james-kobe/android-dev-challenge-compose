/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.model.Cat

const val SELECTED_CAT = "selected_cat"
const val SELECTED_POSITION = "selected_position"
const val ADOPTED = "adopted"
/**
 * CatDetailActivity
 *
 * @author james
 * @since 2022/3/3
 */
class CatDetailActivity : AppCompatActivity() {

    private lateinit var selectedCat: Cat
    private var selectedPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cat = intent.getParcelableExtra<Cat>(SELECTED_CAT)
        selectedPosition = intent.getIntExtra(SELECTED_POSITION, 0)
        if (cat == null) {
            Toast.makeText(this, "There must be something wrong.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        selectedCat = cat
        setContent {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = selectedCat.name
                            )
                        },
                        backgroundColor = Color.Transparent, elevation = 0.dp,
                        navigationIcon = {
                            IconButton(onClick = { navigateBack() }) {
                                val backIcon: Painter = painterResource(R.drawable.ic_back)
                                Icon(painter = backIcon, contentDescription = "ic_back")
                            }
                        }
                    )
                }
            ) {
                DisplayCatDetail(cat = selectedCat)
            }
        }
    }

    override fun onBackPressed() {
        navigateBack()
    }

    private fun navigateBack() {
        val intent = Intent()
        intent.putExtra(SELECTED_POSITION, selectedPosition)
        intent.putExtra(ADOPTED, selectedCat.adopted)
        setResult(RESULT_OK, intent)
        finish()
    }
}

var showConfirmDialog by mutableStateOf(false)

@Composable
fun DisplayCatDetail(cat: Cat) {
    val stateCat by remember { mutableStateOf(cat) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CatAvatar(
            avatar = stateCat.avatarFilename,
            name = stateCat.name
        )
        Spacer(
            modifier = Modifier.requiredHeight(26.dp)
        )
        AdoptButton(
            adopted = stateCat.adopted
        )
        Spacer(
            modifier = Modifier.requiredHeight(26.dp)
        )
        CatIntroduction(
            introduction = stateCat.introduction
        )
    }
    if (showConfirmDialog) {
        AdoptConfirmDialog(cat = stateCat)
    }
}

@Composable
fun CatAvatar(avatar: String, name: String) {
    val imageIdentity = GlobalApp.context.resources.getIdentifier(
        avatar, "drawable",
        GlobalApp.context.packageName
    )
    val image: Painter = painterResource(imageIdentity)
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = image,
            contentDescription = name,
            modifier = Modifier
                .requiredSize(150.dp)
                .clip(shape = CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun CatIntroduction(introduction: String) {
    Text(
        text = introduction,
        fontSize = 18.sp,
        style = MaterialTheme.typography.body1
    )
}

@Composable
fun AdoptButton(adopted: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { showConfirmDialog = true },
            enabled = !adopted
        ) {
            Text(text = if (adopted) "Adopted" else "Adopt")
        }
    }
}

@Composable
fun AdoptConfirmDialog(cat: Cat) {
    AlertDialog(
        onDismissRequest = {
            showConfirmDialog = false
        },
        text = {
            Text(
                text = "Do you want to adopt this lovely cat?",
                style = MaterialTheme.typography.body2
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    showConfirmDialog = false
                    cat.adopted = true
                }
            ) {
                Text(
                    text = "Yes"
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showConfirmDialog = false
                }
            ) {
                Text(
                    text = "No"
                )
            }
        }
    )
}
