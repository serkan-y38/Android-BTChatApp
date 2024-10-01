package com.yilmaz.bt_chat.features.chat.di

import android.content.Context
import com.yilmaz.bt_chat.features.chat.data.chat.BluetoothControllerImpl
import com.yilmaz.bt_chat.features.chat.domain.chat.BluetoothController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatModule {

    @Provides
    @Singleton
    fun provideBTController(
        @ApplicationContext context: Context
    ): BluetoothController = BluetoothControllerImpl(context)

}