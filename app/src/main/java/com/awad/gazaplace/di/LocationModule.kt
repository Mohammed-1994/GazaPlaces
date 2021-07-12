package com.awad.gazaplace.di

import android.content.Context
import com.awad.gazaplace.maps.UpdateLocation
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped


@InstallIn(ActivityComponent::class)
@Module
object LocationModule {


    @Provides
    @ActivityScoped
    fun providesUpdateLocation(@ActivityContext context: Context): UpdateLocation =
        UpdateLocation(context)

}