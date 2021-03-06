package com.star_zero.dagashi.ui.milestone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.InnerPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Card
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.ui.tooling.preview.Preview
import com.star_zero.dagashi.R
import com.star_zero.dagashi.core.data.model.Milestone
import com.star_zero.dagashi.ui.components.ErrorRetry
import com.star_zero.dagashi.ui.components.SwipeToRefreshIndicator
import com.star_zero.dagashi.ui.components.SwipeToRefreshLayout
import com.star_zero.dagashi.ui.theme.DagashiAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MilestoneFragment : Fragment() {

    private val viewModel: MilestoneViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return FrameLayout(requireContext()).apply {
            id = R.id.milestone_fragment

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setContent(Recomposer.current()) {
                DagashiAppTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        Scaffold(
                            topBar = { AppBar() }
                        ) {
                            MilestoneContent(
                                viewModel = viewModel,
                                onItemSelected = ::navigateIssue
                            )
                        }
                    }
                }
            }
        }
    }

    private fun navigateIssue(milestone: Milestone) {
        val action = MilestoneFragmentDirections.actionMilestoneToIssue(milestone)
        findNavController().navigate(action)
    }
}

@Composable
private fun AppBar() {
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.title))
        }
    )
}

@Composable
private fun MilestoneContent(viewModel: MilestoneViewModel, onItemSelected: (Milestone) -> Unit) {
    launchInComposition {
        viewModel.getMilestones()
    }

    val coroutineScope = rememberCoroutineScope()

    if (viewModel.hasError) {
        ErrorRetry(
            onRetry = {
                coroutineScope.launch {
                    viewModel.refresh()
                }
            }
        )
    } else {
        SwipeToRefreshLayout(
            refreshingState = viewModel.loading,
            onRefresh = {
                coroutineScope.launch {
                    viewModel.refresh()
                }
            },
            refreshIndicator = {
                SwipeToRefreshIndicator()
            }
        ) {
            MilestoneList(viewModel.milestones, onItemSelected)
        }
    }
}

@Composable
private fun MilestoneList(milestones: List<Milestone>, onItemSelected: (Milestone) -> Unit) {
    LazyColumnFor(
        items = milestones,
        contentPadding = InnerPadding(top = 8.dp, bottom = 8.dp)
    ) { milestone ->
        MilestoneCard(milestone, onItemSelected)
    }
}

@Composable
private fun MilestoneCard(milestone: Milestone, onItemSelected: (Milestone) -> Unit) {
    Card(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickable(onClick = {
                onItemSelected(milestone)
            })
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
                Text(
                    text = milestone.title,
                    style = MaterialTheme.typography.subtitle1,
                )
            }
            Spacer(modifier = Modifier.preferredHeight(8.dp))
            ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
                Text(
                    text = milestone.description,
                    style = MaterialTheme.typography.caption,
                )
            }
        }
    }
}

// region Compose Preview
@Preview("Milestone card")
@Composable
private fun PreviewMilestone() {
    val milestone = Milestone(
        "id1",
        "135 2020-08-30",
        "Sample Sample",
        ""
    )
    DagashiAppTheme {
        MilestoneCard(milestone) { }
    }
}

@Preview("Milestone card dark theme")
@Composable
private fun PreviewMilestoneDark() {
    val milestone = Milestone(
        "id1",
        "135 2020-08-30",
        "Sample Sample",
        ""
    )
    DagashiAppTheme(darkTheme = true) {
        MilestoneCard(milestone) { }
    }
}
// endregion
