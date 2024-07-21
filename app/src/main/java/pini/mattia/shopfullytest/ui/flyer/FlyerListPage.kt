@file:OptIn(ExperimentalGlideComposeApi::class)

package pini.mattia.shopfullytest.ui.flyer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.valentinilk.shimmer.shimmer
import pini.mattia.shopfullytest.R
import pini.mattia.shopfullytest.domain.flyer.Flyer

@Composable
fun FlyerListPage(flyerListViewModel: FlyerListViewModel = viewModel()) {
    val state = flyerListViewModel.viewState.collectAsState().value
    FlyerListPageComposable(state = state)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlyerListPageComposable(state: FlyerListState) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text(text = stringResource(id = R.string.flyer_page_title)) })
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
                            FlyerItem(flyer = flyer)
                        }
                    }
                }
            }

            is FlyerListState.Error -> {

            }
        }

    }
}

@Composable
fun FlyerItem(flyer: Flyer, onFlyerClicked: (flyer: Flyer) -> Unit = {}) {
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

        GlideImage(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(),
            model = flyer.flyerBackground,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )


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