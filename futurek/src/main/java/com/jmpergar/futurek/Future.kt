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

import kotlinx.coroutines.experimental.*
/**
 * A potentially deferred computation
 */
sealed class Future<A> {
    companion object {
        /**
         * Lift an already computed value to a Future
         */
        fun <A> pure(a: A): Future<A> = Success(a)

        /**
         * Lift an exception to a failed Future
         */
        fun <A> failed(e: Exception): Future<A> = Failure(e)

        fun <A> invoke(f: () -> A, pool: CoroutineDispatcher = CommonPool): Future<A> =
                Cons(async(pool) { f() }, pool)

    }

    /**
     * Functor map. Maps over the contents of the Future transforming its resulting value A to B
     */
    abstract suspend fun <B> map(fa: (A) -> B): Future<B>

    /**
     * Monadic bind. Chain sequential computations in the (A) -> Future<B> Kleisli
     */
    abstract suspend fun <B> flatMap(f: (A) -> Future<B>): Future<B>

}

/**
 * An already completed future
 */
private class Success<A>(val a: A): Future<A>() {
    override suspend fun <B> map(fa: (A) -> B): Future<B> = Success(fa(a))
    override suspend fun <B> flatMap(f: (A) -> Future<B>): Future<B> = f(a)
}

/**
 * A failed Future
 */
private class Failure<A>(val e: Exception): Future<A>() {
    override suspend fun <B> map(fa: (A) -> B): Future<B> = Failure(e)
    override suspend fun <B> flatMap(f: (A) -> Future<B>): Future<B> = Failure(e)
}

/**
 * An async Future
 */
private class Cons<A>(val deferred: Deferred<A>, val pool: CoroutineDispatcher): Future<A>() {

    override suspend fun <B> map(fa: (A) -> B): Future<B> = Success(fa(deferred.await()))

    override suspend fun <B> flatMap(f: (A) -> Future<B>): Future<B> =
        f(deferred.await())

}

/*
    fun onComplete(f: (A) -> Unit) {
        launch(UI) {
            f(deferred.await())
        }
    }
 */