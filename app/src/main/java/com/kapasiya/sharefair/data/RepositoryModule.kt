package com.kapasiya.sharefair.data

import com.google.firebase.firestore.FirebaseFirestore
import com.kapasiya.sharefair.data.repository.*

/**
 * Basic Service Locator to provide repository instances.
 * In a larger project, this should be replaced with Hilt or Koin.
 */
object RepositoryModule {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(firestore)
    }

    val groupRepository: GroupRepository by lazy {
        GroupRepositoryImpl(firestore)
    }

    val billRepository: BillRepository by lazy {
        BillRepositoryImpl(firestore)
    }
}
