FutureK [![](https://jitpack.io/v/JMPergar/FutureK.svg)](https://jitpack.io/#JMPergar/FutureK)
=======

A simple monadic future implementation based on coroutines for Kotlin

## Usage

It's a simple type that help us to manage the asynchrony and the concurrency. With this library you can write async code in sync way.

For launch an async process it's than simple like this:

```kotlin
Future { doHeavyTask() }
```

If it's necessary to obtain a result and process it this is the way:

```kotlin
val myResultFuture = Future { getResultFromMyServer() }
myResultFuture.map { processMyResult(it) }
```

If there is concurrency and we need to process more than one result together we should use `flatMap` and `map`:

```kotlin
val myResultFuture = Future { getResultFromMyServer() }
val myOtherResultFuture = Future { getOtherResultFromMyServer() }
  
val myDataProcessedFuture = myResultFuture.flatMap {
    myResult -> myOtherResult.map {
        myOtherResult -> processTogether(myResult, myOtherResult)
    }
}
```

And if you need apply effects in the UI the method for this is `onComplete`:

```kotlin
myDataProcessedFuture.onComplete { renderResult(it) }
```

If use map and flatMap together is weird or ugly for you, you can implement methods like this for avoid boilerplate. Maybe in next versions this library could include a solution for this. 

```kotlin
fun <T, K, R> forComprehension(f1: Future<T>, f2: Future<K>, function: (T, K) -> R): Future<R> {
        return f1.flatMap { it1 -> f2.map { it2 -> function(it1, it2) } }
}
 
// And this code
val myDataProcessedFuture = myResultFuture.flatMap {
    myResult -> myOtherResult.map {
        myOtherResult -> processTogether(myResult, myOtherResult)
    }
}
 
// Would be like this
val result = forComprehension(myResultFuture, myOtherResult) {
    myResult, myOtherResult -> processTogether(myResult, myOtherResult)
}
```

Here a complete example:

```kotlin
fun initLoadData() {
 
    val myResultFuture = Future { getResultFromMyServer() }
    val myOtherResultFuture = Future { getOtherResultFromMyServer() }
 
    val result = forComprehension(myResultFuture, myOtherResult) {
        myResult, myOtherResult -> processTogether(myResult, myOtherResult)
    }
 
    result.onComplete {
        renderResult(it)
    }
}
```

## Distribution

Add as a dependency to your `build.gradle`
```groovy
repositories {
    ...
    maven { url 'https://jitpack.io' }
    maven { url "https://kotlin.bintray.com/kotlinx/" }
}
 
dependencies {
    compile 'com.github.JMPergar:FutureK:v0.12'
}
```

License
=======

    Copyright 2017 José Manuel Pereira García

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
