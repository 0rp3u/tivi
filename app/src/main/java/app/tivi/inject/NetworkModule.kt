/*
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.tivi.inject

import android.content.Context
import app.tivi.BuildConfig
import coil.util.CoilUtils
import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.LoggingEventListener

@Module
class NetworkModule {
    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor? {
        if (!BuildConfig.DEBUG) {
            return null
        }

        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
    }

    @Singleton
    @Provides
    fun provideHttpEventListener(): LoggingEventListener.Factory? {
        if (!BuildConfig.DEBUG) {
            return null
        }

        return LoggingEventListener.Factory()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor?,
        loggingEventListener: LoggingEventListener.Factory?,
        applicationContext: Context
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                if (httpLoggingInterceptor != null) {
                    addInterceptor(httpLoggingInterceptor)
                }
                if (loggingEventListener != null) {
                    eventListenerFactory(loggingEventListener)
                }
            }
            .cache(CoilUtils.createDefaultCache(applicationContext))
            .connectionPool(ConnectionPool(10, 2, TimeUnit.MINUTES))
            .dispatcher(Dispatcher().apply {
                maxRequestsPerHost = 15
            })
            .build()
    }
}
