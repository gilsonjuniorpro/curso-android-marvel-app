package com.example.marvelapp.framework.paging

import androidx.paging.PagingSource
import com.example.core.data.repository.CharactersRemoteDataSource
import com.example.marvelapp.factory.paging.DataWrapperResponseFactory
import com.example.marvelapp.framework.network.response.DataWrapperResponse
import com.example.testing.MainCoroutineRule
import com.example.testing.model.CharacterFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.lang.RuntimeException

@RunWith(MockitoJUnitRunner::class)
class CharactersPagingSourceTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var charactersPagingSource: CharactersPagingSource

    @Mock
    lateinit var remoteDataSource: CharactersRemoteDataSource<DataWrapperResponse>

    private val dataWrapperResponseFactory = DataWrapperResponseFactory()

    private val characterFactory = CharacterFactory()

    @Before
    fun setup() {
        charactersPagingSource = CharactersPagingSource(remoteDataSource, "")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should return success load result when load is called`() = runBlockingTest {
        //arrange
        whenever(remoteDataSource.fetchCharacters(any())).thenReturn(
                dataWrapperResponseFactory.create()
        )

        //act
        val result = charactersPagingSource.load(
                PagingSource.LoadParams.Refresh(
                       null,
                        loadSize = 2,
                        false
                )
        )

        //assert
        val expected = listOf(
                characterFactory.create(CharacterFactory.Hero.ThreeDMan),
                characterFactory.create(CharacterFactory.Hero.ABomb)
        )
        assertEquals(
                PagingSource.LoadResult.Page(
                        data = expected,
                        prevKey = null,
                        nextKey = 20
                ), result
        )
    }


    @ExperimentalCoroutinesApi
    @Test
    fun `should return an error load result when load is called`() = runBlockingTest {
        //arrange
        val exception = RuntimeException()
        whenever(remoteDataSource.fetchCharacters(any())).thenThrow(
                exception
        )

        //act
        val result = charactersPagingSource.load(
                PagingSource.LoadParams.Refresh(
                        null,
                        loadSize = 2,
                        false
                )
        )

        //assert
        assertEquals(
                PagingSource.LoadResult.Error<Int, Character>(exception), result
        )
    }
}
