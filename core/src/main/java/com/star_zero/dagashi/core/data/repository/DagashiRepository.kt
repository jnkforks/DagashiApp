package com.star_zero.dagashi.core.data.repository

import com.star_zero.dagashi.core.data.model.Issue
import com.star_zero.dagashi.core.data.model.Milestone

interface DagashiRepository {
    suspend fun milestones(): List<Milestone>

    suspend fun issues(path: String): List<Issue>
}
