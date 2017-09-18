# Me + fixd

# Our experience w kotlon on android

# Useful kotlin patterns

Mixins, sealed classes for state machines, lazy loading for vars at oncreste 

# Experiemental 

# Problem: async + failable

# Existing solutions

Asynctask, callbacks, rx

# What about flows?

Async, branching, failures, ui interactions, cancellation

# Examples

# Coroutines

The main thread on android is a Looper. Both you and the OS can put work do be done into the queue, and the
looper will execute each one in the order it was assigned. Activity lifecycle callbacks, frame updates, calls
to runOnUI all get put in this list. 

So if you try 


async -> take a suspending block, and return a Deferred (aka Promise) for the result

await() -> called on a Deferred to suspend until the result is aquired

launch() -> execute a suspending block, and get a Job back which an be used to cancel
the process or check on it's state

suspendCoroutine/suspendCancellableCoroutine -> a raw create method for a suspending function

Suspend, concepts under the hood, context, cancellation

## Cancellation

Cancelation can be a bit confusing.
When non-suspending calls are 

# Installing/setup

# Patterns in android

## Cancellation on stop/destroy

## Rx awaiting

## Dialogs

## Activity for result

## Blocking code

## Many other coroutine features

- Channels
- Actors
- Mutexes (suspending locks)

## Gotchas

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