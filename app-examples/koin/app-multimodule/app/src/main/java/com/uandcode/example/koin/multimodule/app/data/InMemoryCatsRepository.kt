package com.uandcode.example.koin.multimodule.app.data

import com.elveum.container.Container
import com.elveum.container.containerMap
import com.elveum.container.subject.LazyFlowSubject
import com.github.javafaker.Faker
import com.uandcode.example.koin.multimodule.features.details.domain.CatDetailsRepository
import com.uandcode.example.koin.multimodule.features.list.domain.CatListRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.Random
import com.uandcode.example.koin.multimodule.features.details.domain.Cat as FeatureDetailsCat
import com.uandcode.example.koin.multimodule.features.list.domain.Cat as FeatureListCat

private val PHOTOS = listOf(
    "https://images.unsplash.com/photo-1472491235688-bdc81a63246e?w=640",
    "https://images.unsplash.com/photo-1514888286974-6c03e2ca1dba?w=640",
    "https://images.unsplash.com/photo-1500259571355-332da5cb07aa?w=640",
    "https://images.unsplash.com/photo-1573865526739-10659fec78a5?w=640",
    "https://images.unsplash.com/photo-1513360371669-4adf3dd7dff8?w=640",
    "https://images.unsplash.com/photo-1571566882372-1598d88abd90?w=640",
    "https://images.unsplash.com/photo-1518288774672-b94e808873ff?w=640",
    "https://images.unsplash.com/photo-1568152950566-c1bf43f4ab28?w=640",
)

class InMemoryCatsRepository : CatListRepository, CatDetailsRepository {

    private val random = Random(1)
    private val faker = Faker.instance(random)

    private val itemsList = MutableList(40) {
        val id = it + 1L
        Cat(
            id = id,
            name = faker.cat().name(),
            details = faker.lorem().paragraph(4),
            image = PHOTOS[it % PHOTOS.size],
            isLiked = false,
        )
    }

    private val subject = LazyFlowSubject.create<List<Cat>> {
        delay(2000)
        emit(ArrayList(itemsList))
    }

    override suspend fun deleteCat(cat: FeatureListCat) {
        val index = indexOf(cat.toDataModel())
        if (index != -1) {
            itemsList.removeAt(index)
            notifyChanges()
        }
    }

    override suspend fun toggleLike(cat: FeatureListCat) = toggleLike(cat.toDataModel())

    override suspend fun toggleLike(cat: FeatureDetailsCat) = toggleLike(cat.toDataModel())

    override fun getCats(): Flow<Container<List<Cat>>> {
        return subject.listen()
    }

    override fun getById(id: Long): Flow<Container<Cat>> {
        return getCats()
            .containerMap { list ->
                list.first { it.id == id }
            }
    }

    private fun toggleLike(cat: Cat) {
        val index = indexOf(cat)
        if (index != -1) {
            val newItem = cat.copy(isLiked = !cat.isLiked)
            itemsList[index] = newItem
            notifyChanges()
        }
    }

    private fun indexOf(cat: Cat) = itemsList.indexOfFirst { it.id == cat.id }

    private fun notifyChanges() {
        subject.updateWith(Container.Success(itemsList.toList()))
    }

}
