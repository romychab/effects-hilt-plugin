package com.elveum.effects.example.data

import com.elveum.container.ListContainerFlow
import com.elveum.container.subject.LazyFlowSubject
import com.elveum.container.subject.newSimpleAsyncLoad
import com.elveum.container.unwrapFirst
import com.elveum.effects.example.domain.Cat
import com.elveum.effects.example.domain.CatsRepository
import com.github.javafaker.Faker
import kotlinx.coroutines.delay
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryCatsRepository @Inject constructor() : CatsRepository {

    private val random = Random(1)
    private val faker = Faker.instance(random)

    private val itemsList = MutableList(40) {
        val id = it + 1L
        Cat(
            id = id,
            name = faker.cat().name(),
            details = faker.lorem().paragraph(4),
            image = "https://source.unsplash.com/random?cat&iddqd=${random.nextInt()}",
            isLiked = false,
        )
    }

    private val subject = LazyFlowSubject.create<List<Cat>> {
        delay(2000)
        emit(ArrayList(itemsList))
    }

    override suspend fun deleteCat(cat: Cat) {
        val index = indexOf(cat)
        if (index != -1) {
            itemsList.removeAt(index)
            notifyChanges()
        }
    }

    override fun getCats(): ListContainerFlow<Cat> {
        return subject.listen()
    }

    override suspend fun toggleLike(cat: Cat) {
        val index = indexOf(cat)
        if (index != -1) {
            val newItem = cat.copy(isLiked = !cat.isLiked)
            itemsList[index] = newItem
            notifyChanges()
        }
    }

    override suspend fun getById(id: Long): Cat {
        return getCats().unwrapFirst().first { it.id == id }
    }

    private fun indexOf(cat: Cat) = itemsList.indexOfFirst { it.id == cat.id }

    private fun notifyChanges() {
        subject.newSimpleAsyncLoad(once = true, silently = true) { ArrayList(itemsList) }
    }
}