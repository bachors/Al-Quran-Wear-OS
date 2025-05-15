/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.bachors.quranid.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.text.HtmlCompat
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListState
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.bachors.quranid.R
import com.bachors.quranid.presentation.model.Surah
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            MaterialTheme {

                val scalingLazyListState: ScalingLazyListState = rememberScalingLazyListState()
                val swipeDismissableNavController = rememberSwipeDismissableNavController()

                val jsonData = readJsonFromAssets(this@MainActivity)
                val surahList = parseSurahModel(jsonData)

                var surahIndex = 0
                var ayahIndex = 0

                SwipeDismissableNavHost(
                    navController = swipeDismissableNavController,
                    startDestination = "Surah",
                    modifier = Modifier.background(MaterialTheme.colors.background)
                ) {
                    composable("Surah") {
                        ScalingLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 28.dp,
                                start = 10.dp,
                                end = 10.dp,
                                bottom = 40.dp
                            ),
                            verticalArrangement = Arrangement.Center,
                            state = scalingLazyListState
                        ) {
                            items(surahList.size) { index ->
                                Chip(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = 10.dp
                                        ),
                                    colors = ChipDefaults.chipColors(
                                        backgroundColor = colorResource(R.color.colorPrimary)
                                    ),
                                    label = {
                                        Column {
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        bottom = 2.dp
                                                    ),
                                                fontSize = 14.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontStyle = FontStyle.Normal,
                                                fontWeight = FontWeight.Normal,
                                                color = Color.White,
                                                text = surahList[index].nomor + ". " + surahList[index].nama
                                            )
                                            Text(
                                                textAlign = TextAlign.End,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        bottom = 2.dp
                                                    ),
                                                fontFamily = FontFamily.SansSerif,
                                                fontStyle = FontStyle.Normal,
                                                fontWeight = FontWeight.Normal,
                                                color = Color.White,
                                                text = surahList[index].asma
                                            )
                                            Text(
                                                buildAnnotatedString {
                                                    withStyle(style = ParagraphStyle(lineHeight = 12.sp)) {
                                                        withStyle(style = SpanStyle(color = Color.Cyan)) {
                                                            append(surahList[index].arti)
                                                        }
                                                    }
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        bottom = 2.dp
                                                    ),
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontStyle = FontStyle.Normal,
                                                fontWeight = FontWeight.Normal,
                                            )
                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        bottom = 2.dp
                                                    ),
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontStyle = FontStyle.Normal,
                                                fontWeight = FontWeight.Normal,
                                                text = "Jumlah Ayat: " + surahList[index].ayat.size
                                            )
                                            Text(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = Color.White,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontStyle = FontStyle.Normal,
                                                fontWeight = FontWeight.Normal,
                                                text = "⚲ " + surahList[index].type
                                                    .replaceFirstChar {
                                                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                                                    }
                                            )
                                        }
                                    },
                                    onClick = {
                                        surahIndex = index
                                        swipeDismissableNavController.navigate("Ayah")
                                    }
                                )
                            }
                        }
                    }

                    composable("Ayah") {
                        ScalingLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 28.dp,
                                start = 10.dp,
                                end = 10.dp,
                                bottom = 40.dp
                            ),
                            verticalArrangement = Arrangement.Center,
                            state = scalingLazyListState
                        ) {
                            val ayahList = surahList[surahIndex].ayat
                            items(ayahList.size) { index ->
                                Chip(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = 10.dp
                                        ),
                                    colors = ChipDefaults.chipColors(
                                        backgroundColor = colorResource(R.color.colorPrimary)
                                    ),
                                    label = {
                                        Column {
                                            Text(
                                                textAlign = TextAlign.End,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        top = 4.dp,
                                                        bottom = 2.dp
                                                    ),
                                                color = Color.White,
                                                fontSize = 12.sp,
                                                fontFamily = FontFamily.SansSerif,
                                                fontStyle = FontStyle.Normal,
                                                fontWeight = FontWeight.Normal,
                                                text = ayahList[index].ar
                                            )
                                        }
                                    },
                                    onClick = {
                                        ayahIndex = index
                                        swipeDismissableNavController.navigate("Detail")
                                    }
                                )
                            }
                        }
                    }

                    composable("Detail") {
                        ScalingLazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 28.dp,
                                start = 10.dp,
                                end = 10.dp,
                                bottom = 40.dp
                            ),
                            verticalArrangement = Arrangement.Center,
                            state = scalingLazyListState
                        ) {
                            var detailList: Array<String?> = arrayOfNulls(3)
                            detailList[0] = surahList[surahIndex].ayat[ayahIndex].ar
                            detailList[1] = surahList[surahIndex].ayat[ayahIndex].tr
                            detailList[2] = surahList[surahIndex].ayat[ayahIndex].id
                            items(detailList.size) { index ->
                                Chip(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            top = 10.dp
                                        ),
                                    colors = ChipDefaults.chipColors(
                                        backgroundColor = colorResource(R.color.colorPrimary)
                                    ),
                                    label = {
                                        Column {
                                            if(index == 0) {
                                                Text(
                                                    textAlign = TextAlign.End,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            top = 4.dp,
                                                            bottom = 2.dp
                                                        ),
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontStyle = FontStyle.Normal,
                                                    fontWeight = FontWeight.Normal,
                                                    text = detailList[index].toString()
                                                )
                                            } else {
                                                Text(
                                                    buildAnnotatedString {
                                                        withStyle(style = ParagraphStyle(lineHeight = 12.sp)) {
                                                            var title = "Latin:\n"
                                                            if(index == 2) {
                                                                title = "Terjemahan:\n"
                                                            }
                                                            withStyle(style = SpanStyle(color = Color.Cyan)) {
                                                                append(title)
                                                            }
                                                            append(HtmlCompat.fromHtml(detailList[index].toString(), HtmlCompat.FROM_HTML_MODE_COMPACT))
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(
                                                            top = 4.dp,
                                                            bottom = 2.dp
                                                        ),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.SansSerif,
                                                    fontStyle = FontStyle.Normal,
                                                    fontWeight = FontWeight.Normal,
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        //
                                    }
                                )
                            }
                        }
                    }

                }

            }
        }

    }
}

private fun readJsonFromAssets(context: Context): String {
    return context.assets.open("data.json").bufferedReader().use { it.readText() }
}

private fun parseSurahModel(jsonString: String): List<Surah> {
    val gson = Gson()
    return gson.fromJson(jsonString, object : TypeToken<List<Surah>>() {}.type)
}