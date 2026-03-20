package com.kapasiya.sharefair.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase
import com.kapasiya.sharefair.data.repository.*

/**
 * Basic Service Locator to provide repository instances.
 */
object RepositoryModule {
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val realtimeDb: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
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
    
    val draftRepository: DraftRepository by lazy {
        DraftRepositoryImpl(firestore)
    }
    
    val recurringBillRepository: RecurringBillRepository by lazy {
        RecurringBillRepositoryImpl(firestore)
    }

    val chatRepository: ChatRepository by lazy {
        ChatRepositoryImpl(realtimeDb)
    }

    val collectionRepository: CollectionRepository by lazy {
        CollectionRepositoryImpl(firestore)
    }

    val notificationRepository: NotificationRepository by lazy {
        NotificationRepositoryImpl(firestore)
    }
}
