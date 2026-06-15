package com.example.barbershopcompose.di

import com.example.barbershopcompose.data.repository.ProfissionalRepositoryImpl
import com.example.barbershopcompose.domain.repository.ProfissionalRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideProfissionalRepository(db: FirebaseFirestore): ProfissionalRepository {
        return ProfissionalRepositoryImpl(db)
    }
}