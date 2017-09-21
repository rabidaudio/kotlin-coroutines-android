Subroutines = asnyc/failable
  - IO (network requests, filesystem operations)
  - heavy computational work
  - UI interactions


Colors: 

orange: #f47810
blue: #497bb7
grey: #393939


The main thread on android is a Looper. Both you and the OS can put work do be done into the queue, and the
looper will execute each one in the order it was assigned. Activity lifecycle callbacks, frame updates, calls
to runOnUI all get put in this list. 



Serializable coroutine states
- hypothetically save work and pick it up later


anko asReference
https://github.com/Kotlin/anko/blob/master/anko/library/generated/coroutines/src/bg.kt



# Me + fixd

# Our experience w kotlin on android

# Useful kotlin patterns

Mixins, sealed classes for state machines, lazy loading for vars at oncreate 

# Problem: async + failable

Code that just uses memory and cpu time, making calculations, conversions

Everything interesting an application can do requires IO. 
- networking, filesystem, accessing hardware peripherals, user interface

For many apps, state transitions are only one or two steps. For example, Twitter
loading state while the first set of tweets are fetched from the network, an error
state if the fetching of tweets fails, and a list state showing the tweets.
Then the add tweet button, which also has three states, loading, failure, success.

The vast majority of web and mobile apps function this way, and we've gotten pretty good
at solving those problems. There are dozens of ways to solve them- AsyncTask, Observables,
Promises, Redux, etc.

# Existing solutions

- Callbacks
- Raw Threads
- AsyncTask
- Promises/Futures
- Observables / Reactive Streams


# What about flows?

Async, branching, failures, ui interactions, cancellation

- Callbacks - callback hell => hard to read, hard to refactor
- Raw Threads - concurrent programing is hard to deal with UI
- AsyncTask - verbose, doesn't handle more than one step, hard to chain together, only works with blocking code
- Promises/Futures - CompletableFuture (minSdk 24), not many other libs, no cancellation, concurrent by default
- Observables / Reactive Streams - steep learning curve, functional programming, lots of operators and rules

all of them require special syntax for async code

# Examples

But what happens when your operations are more complicated, when you have a chain or flow of
operations like this? How can you chain these things together?

Here's an example flow. This is a simplified version of the main function of our app. Our actual flow
is fairly different and has a lot more branches, but I think that is example is illustrative of the problem
and you don't have to have used our app to follow it.


# Experiemental 


# Coroutines


## suspend

suspendCoroutine/suspendCancellableCoroutine -> a raw create method for a suspending function

## build up logic with suspend

you can use all the normal imperative operations on them

if, while, map, apply, try/catch

## coroutine builders to launch suspend functions

launch() -> execute a suspending block, and get a Job back which an be used to cancel
the process or check on it's state

## context

UI, CommonPool

## Concurrency

sequential by default

async -> take a suspending block, and return a Deferred (aka Promise) for the result

await() -> called on a Deferred to suspend until the result is aquired

## Cancelation

Cancelation can be a bit confusing.
When non-suspending calls are 


# Under the hood

no magic, similar solutions from other languages (some of them very old)

suspend -> compiler flag to alter your function to use a callback format

done with a Continuation object

state machine to keep track of step within your code

< show example >

Builders, etc are in library. just a little pure Kotlin code to enable calling them. 
Open source, so you can see how they work

# Kinda like threads

but also not

launch(100_000) {  }

# Installing/setup

```groovy
dependencies {

  // main
  compile 'org.jetbrains.kotlinx:kotlinx-coroutines-core:0.18'
  compile 'org.jetbrains.kotlinx:kotlinx-coroutines-android:0.18'

  // RxJava 1
  compile 'org.jetbrains.kotlinx:kotlinx-coroutines-rx1:0.18'

  // RxJava 2
  compile 'org.jetbrains.kotlinx:kotlinx-coroutines-rx2:0.18'

  // Java 8 CompletableFuture (minSdk 24)
  compile 'org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:0.18'

  // Java NIO (minSdk 26)
  compile 'org.jetbrains.kotlinx:kotlinx-coroutines-nio:0.18'
}
```

# Solving the flowchart

Here, orange boxes represent different UI states, which means code needs to be run on the main thread to update
views to match that state and blue boxes are subroutines (async, failable operations).

Here we've got a sealed class that represents all the possible UI states. For anyone still new to Kotlin
who doesn't know how sealed classes work, look them up when you get home! but in the meantime, just think
of them as enums with associated data. So these are all the possible states of our user interface, and each
state includes all the associated data necessary to draw that state. 

Then we've got our ViewModel, and I've spec'd out all the coroutine calls (the blue calls from the flowchart).
Notice these are marked with suspend. We will come back to this in a second.


permissions, enable bluetooth


# Patterns in android

## Callbacks

bridge google play services tasks:

```kotlin
suspend fun <T> Task<T>.await(): T = suspendCoroutine { cont ->
    addOnCompleteListener { cont.resume(it.result) }
    addOnFailureListener { cont.resumeWithException(it) }
}
```

Now you can await tasks like `getLastLocation()`

```kotlin
launch(UI) {
  val locationClient = LocationServices.getFusedLocationProviderClient(this)
  locationClient.lastLocation.await()
}
```

## Observables to coroutines

If you are using a library that exposes Observables or Singles, maybe you
are using Retrofit with the rxjava adapter for example, you can suspend until
an item is emitted.
Everything just works. If Observable emits an error, that error will be thrown
at the call site. If the coroutine is cancelled, it will call unsubscribe on
the observable's subscription.

- Observable.awaitFirst()
- Observable.awaitFirstOrDefault()
- Observable.awaitSingle()
- Observable.awaitLast()
- Single.await()
- Observable.consumeEach {}

## Cancellation on stop/destroy

launch job, keep attached to activity/fragment

on re-launch, cancel if existing job, then replace
on stop/destroy, call job.cancel()

```kotlin
interface JobManagerMixin {

    data class ManagedJob(val job: Job, val end: LifecycleEnd) {
        enum class LifecycleEnd { onPause, onStop, onDestroy }
    }

    val _jobs: MutableMap<String, ManagedJob>

    fun launch(context: CoroutineContext = UI,
               end: ManagedJob.LifecycleEnd,
               block: suspend CoroutineScope.() -> Unit)
            = launchSingleTask(UUID.randomUUID().toString(), context, end, block)

    fun launchSingleTask(name: String,
                         context: CoroutineContext = UI,
                         end: ManagedJob.LifecycleEnd,
                         block: suspend CoroutineScope.() -> Unit) {
        _jobs[name]?.job?.cancel(CancellationException("singleTask replaced"))
        _jobs[name] = ManagedJob(launch(context, CoroutineStart.DEFAULT, block), end)
    }

    fun onPause() {
        cancelJobs(ManagedJob.LifecycleEnd.onPause)
    }

    fun onStop() {
        cancelJobs(ManagedJob.LifecycleEnd.onStop)
    }

    fun onDestroy() {
        cancelJobs(ManagedJob.LifecycleEnd.onDestroy)
    }

    private fun cancelJobs(lifecycleEnd: ManagedJob.LifecycleEnd) = _jobs.forEach { (name, managedJob) ->
        if (managedJob.end == lifecycleEnd) {
            managedJob.job.cancel(CancellationException("Job hit end of life cycle: $lifecycleEnd"))
            _jobs.remove(name)
        }
    }
}
```

## Dialogs

```kotlin
suspend fun showConfirmationDialog() = suspendCancellableCoroutine<Boolean> { cont ->
    val dialog = AlertDialog.Builder(this)
            .setMessage("Are you sure?")
            .setPositiveButton("Yes", { _, _ -> cont.resume(true) })
            .setNegativeButton("No", { _, _ -> cont.resume(false) })
            .setCancelable(true)
            .setOnCancelListener { cont.cancel() }
            .create()

    dialog.show()
    cont.invokeOnCompletion { if(cont.isCancelled) dialog.dismiss() }
}
```

## Activity for result (also request permissions)

```kotlin

```

## Blocking code

```kotlin
data class User(val id: Int, val email: String, val name: String)

suspend fun getUserById(db: SQLiteDatabase, id: Int): User? = async(CommonPool) {

    db.query("users", arrayOf("id", "email", "name"), "id = ?", arrayOf(id.toString()), null, null, null).use { cursor ->
        if (cursor.count == 0)
            return@async null
        cursor.moveToFirst()
        User(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                name = cursor.getString(cursor.getColumnIndex("name"))
        )
    }
}.await()

suspend fun saveUser(db: SQLiteDatabase, user: User) = async(CommonPool) {
    db.insert("users", null, ContentValues().apply {
        put("id", user.id)
        put("name", user.name)
        put("email", user.email)
    })
}.await()
```

## concurrency

## Many other coroutine features

- Channels
- Actors
- Mutexes (suspending locks)

## Gotchas

- if it doesn't acutally suspend
- cancellation is cooperative

## Questions



======


Kotlin Coroutines - simplify async

We've all done this before

AsyncTask

Load data -> drop in UI

show loading

OK

catch error, and show error message

OK

make 2 network requests, and show error for either

make network request, get user input from a dialog, make second request


======

Observable.create => suspendCancellableCoroutine


FIXD flowchart


https://github.com/Kotlin/kotlinx.coroutines
https://github.com/Kotlin/kotlinx.coroutines/tree/master/ui/kotlinx-coroutines-android
https://github.com/Kotlin/kotlinx.coroutines
http://kotlinlang.org/docs/reference/coroutines.html
https://github.com/square/retrofit



Shift the burden in to libraries. Now we can shift the burden into the language. Benefits: compiler checking + safety.