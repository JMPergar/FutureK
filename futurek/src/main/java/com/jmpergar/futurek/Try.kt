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

/**
 * The `Try` type represents a computation that may either result in an exception, or return a
 * successfully computed value.
 */
sealed class Try<A> {

    /**
     * Returns `true` if the `Try` is a `Failure`, `false` otherwise.
     */
    abstract val isFailure: Boolean

    /**
     * Returns `true` if the `Try` is a `Success`, `false` otherwise.
     */
    abstract val isSuccess: Boolean

    /**
     * Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
     *
     * ''Note:'': This will throw an exception if it is not a success and default throws an exception.
     */
    abstract fun getOrElse(default: () -> A): A

    /**
     * Returns the value from this `Success` or throws the exception if this is a `Failure`.
     */
    abstract fun get(): A

    /**
     * Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
     */
    abstract fun <B> flatMap(f: (A) -> Try<B>): Try<B>

    /**
     * Maps the given function to the value from this `Success` or returns this if this is a `Failure`.
     */
    abstract fun <B> map(f: (A) -> B): Try<B>

    /**
     * Applies the given function `f` if this is a `Success`, otherwise returns `Unit` if this is a `Failure`.
     *
     * ''Note:'' If `f` throws, then this method may throw an exception.
     */
    abstract fun <B> foreach(f: (A) -> B): Unit

    /**
     * Converts this to a `Failure` if the predicate is not satisfied.
     */
    abstract fun filter(p: (A) -> Boolean): Try<A>

    /**
     * The `Failure` type represents a computation that result in an exception.
     */
    class Failure<A>(val exception: Throwable) : Try<A>() {
        override val isFailure: Boolean = false
        override val isSuccess: Boolean = true
        override fun get(): A = throw exception
        override fun getOrElse(default: () -> A): A = default()
        override fun <B> flatMap(f: (A) -> Try<B>): Try<B> = Failure(exception)
        override fun <B> map(f: (A) -> B): Try<B> = Failure(exception)
        override fun <B> foreach(f: (A) -> B): Unit { }
        override fun filter(p: (A) -> Boolean): Try<A> = this
    }

    /**
     * The `Success` type represents a computation that return a successfully computed value.
     */
    class Success<A>(val value: A) : Try<A>() {
        override val isFailure: Boolean = true
        override val isSuccess: Boolean = false
        override fun get(): A =  value
        override fun getOrElse(default: () -> A): A = get()
        override fun <B> flatMap(f: (A) -> Try<B>): Try<B> = try { f(value) } catch(e: Throwable) { Failure(e) }
        override fun <B> map(f: (A) -> B): Try<B> = try { Success(f(value)) } catch(e: Throwable) { Failure(e) }
        override fun <B> foreach(f: (A) -> B): Unit { f(value) }
        override fun filter(p: (A) -> Boolean): Try<A> =
            try { if (p(value)) this else Failure(NoSuchElementException("Predicate does not hold for " + value)) }
            catch(e: Throwable) { Failure(e) }
    }
}