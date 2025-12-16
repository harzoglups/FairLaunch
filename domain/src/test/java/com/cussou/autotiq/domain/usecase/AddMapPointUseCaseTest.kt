package com.cussou.autotiq.domain.usecase

import com.cussou.autotiq.domain.repository.MapPointRepository
import com.cussou.autotiq.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddMapPointUseCaseTest {

    private lateinit var repository: MapPointRepository
    private lateinit var useCase: AddMapPointUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = AddMapPointUseCase(repository)
    }

    @Test
    fun `invoke creates point with correct coordinates`() = runTest {
        // Given
        val latitude = 47.3769
        val longitude = 8.5417
        val expectedId = 1L
        
        coEvery { repository.insertPoint(any()) } returns Result.Success(expectedId)

        // When
        val result = useCase(latitude, longitude)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(expectedId, (result as Result.Success).data)
        
        coVerify {
            // Verify that a point with the correct coordinates was inserted
            repository.insertPoint(match { point ->
                point.latitude == latitude && point.longitude == longitude
            })
        }
    }

    @Test
    fun `invoke handles repository error`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { repository.insertPoint(any()) } returns Result.Error(exception)

        // When
        val result = useCase(47.3769, 8.5417)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }
}

