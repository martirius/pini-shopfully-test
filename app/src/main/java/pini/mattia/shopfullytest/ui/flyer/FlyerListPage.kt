@file:OptIn(ExperimentalGlideComposeApi::class)

package pini.mattia.shopfullytest.ui.flyer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.valentinilk.shimmer.shimmer
import pini.mattia.shopfullytest.R
import pini.mattia.shopfullytest.domain.flyer.Flyer
import pini.mattia.shopfullytest.ui.visibilitytracker.VisibilityTracker
import java.util.Date

@Composable
fun FlyerListPage(flyerListViewModel: FlyerListViewModel = viewModel()) {
    val state = flyerListViewModel.viewState.collectAsState().value
    FlyerListPageComposable(state = state, {
        flyerListViewModel.flyerSelected(it)
    }, flyerListViewModel::flyerDetailDismissed,
        { flyerListViewModel.filterSwitched(it) },
        { duration, percentage, flyerId ->
            flyerListViewModel.onImpression(
                duration,
                percentage,
                flyerId
            )
        },
        flyerListViewModel::loadFlyers
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
private fun FlyerListPageComposable(
    state: FlyerListState,
    onSelectedFlyer: (flyer: Flyer) -> Unit,
    onDetailDismissed: () -> Unit,
    onSwitchToggled: (status: Boolean) -> Unit,
    onImpression: (duration: Int, percentage: Float, flyerId: Int) -> Unit,
    reloadFlyers: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val isFilterEnabled = remember {
        mutableStateOf(false)
    }
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.flyer_page_title)) },
                actions = {
                    Row {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = Color.Green
                        )
                        Spacer(modifier = Modifier.size(2.dp))
                        Text(text = "?")
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Switch(checked = isFilterEnabled.value, onCheckedChange = onSwitchToggled)
                })
        }) {
        when (state) {
            is FlyerListState.Loading -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(count = 2),
                    modifier = Modifier.padding(it),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(count = 5) {
                        Box(
                            Modifier
                                .aspectRatio(1f)
                                .shimmer()
                                .background(Color.LightGray)
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }
            }

            is FlyerListState.Content -> {
                isFilterEnabled.value = state.isFilterEnabled

                LazyVerticalGrid(
                    columns = GridCells.Fixed(count = 2),
                    modifier = Modifier.padding(it),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.flyers.forEach { flyer ->
                        item(
                            span = { if (flyer.isXL) GridItemSpan(maxLineSpan) else GridItemSpan(1) }) {
                            FlyerItem(flyer = flyer, onSelectedFlyer, onImpression)
                        }
                    }

                }
                if (state.selectedFlyer != null) {
                    ModalBottomSheet(
                        onDismissRequest = onDetailDismissed,
                        sheetState = sheetState,
                    ) {
                        Text(
                            text = state.selectedFlyer.title,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        GlideImage(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .fillMaxSize(),
                            model = state.selectedFlyer.flyerBackground,
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
            }

            is FlyerListState.Error -> {
                Box(modifier = Modifier
                    .padding(it)
                    .fillMaxSize()) {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = stringResource(id = R.string.flyers_load_error), fontSize = 22.sp, color = Color.Red)
                        Button(onClick = reloadFlyers, colors = ButtonDefaults.buttonColors().copy(containerColor = Color.Red)) {
                            Text(text = stringResource(id = R.string.retry), color = Color.White)
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun FlyerItem(
    flyer: Flyer,
    onFlyerClicked: (flyer: Flyer) -> Unit,
    onImpression: (duration: Int, percentage: Float, flyerId: Int) -> Unit
) {
    Box(
        Modifier
            .border(
                border = BorderStroke(0.5.dp, Color.LightGray),
                shape = RoundedCornerShape(4.dp)
            )
            .aspectRatio(if (flyer.isXL) 2f else 1f)
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                onFlyerClicked(flyer)
            }
    ) {

        var appearedTime = remember {
            0L
        }
        var becameVisible = remember {
            false
        }
        var visibleRatio = remember {
            0.0f
        }
        VisibilityTracker(onVisibilityChanged = { visible ->
            if (visible) {
                becameVisible = true
                appearedTime = Date().time
            }
            if (!visible && becameVisible) {
                val impressionDuration = Date().time - appearedTime
                onImpression(impressionDuration.toInt(), visibleRatio, flyer.id)
                becameVisible = false
            }
        }, threshold = 0.5f, visibleRatioCallback = { ratio ->
            visibleRatio = ratio
        }) {
            GlideImage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                model = flyer.flyerBackground,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }


        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(2.dp)
                .border(
                    border = BorderStroke(0.5.dp, Color.LightGray),
                    shape = RoundedCornerShape(4.dp)
                )
                .align(Alignment.Center)
                .clip(RoundedCornerShape(4.dp))
        ) {

            Text(
                text = flyer.title, modifier = Modifier
                    .background(color = Color.LightGray.copy(alpha = 0.7f))
            )
        }

        if (flyer.isAlreadySeen) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = Color.Green
                )
            }
        }
    }
}