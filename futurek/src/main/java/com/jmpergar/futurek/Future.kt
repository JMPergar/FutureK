/*
 * Copyright (C) 2017 José Manuel Pereira García
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jmpergar.futurek

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch

class Future<T> {

    private val deferred: Deferred<T>

    private constructor(deferred: Deferred<T>) {
        this.deferred = deferred
    }

    constructor(f: () -> T) : this(async(CommonPool) { f() })

    fun <X> map(f: (T) -> X): Future<X> {
        return Future(async(CommonPool) { f(deferred.await()) })
    }

    fun <X> flatMap(f: (T) -> Future<X>): Future<X> {
        return Future(async(CommonPool) { f(deferred.await()).deferred.await() })
    }

    fun onComplete(f: (T) -> Unit) {
        launch(UI) {
            f(deferred.await())
        }
    }
}