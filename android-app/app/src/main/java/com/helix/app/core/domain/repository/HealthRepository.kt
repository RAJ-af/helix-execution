package com.helix.app.core.domain.repository

import com.helix.app.core.common.Resource
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    fun getHealthStatus(): Flow<Resource<String>>
}
