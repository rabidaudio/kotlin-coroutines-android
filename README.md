# Kotlin Coroutines In Android - Samples

This project contains some example code to go with my talk, given at Connect.Tech 2017. [View Slides](https://speakerdeck.com/rabidaudio/kotlin-coroutines-in-android)

There are two modules. 

## [`examples`](sample/KotlinAsync/examples)

Contains a few simple examples from the talk.

- [The `simple` package](sample/KotlinAsync/examples/src/main/java/audio/rabid/debug/examples/simple) contains implementations of a UI flow with several different strategies (`AsyncTask`, callbacks, Observables, coroutines) for comparison.
- [`javainterop`](sample/KotlinAsync/examples/src/main/java/audio/rabid/debug/examples/javainterop) has examples of using coroutines from java.
- [`ExampleUnitTest.kt`](sample/KotlinAsync/examples/src/test/java/audio/rabid/debug/examples/ExampleUnitTest.kt) has examples of using coroutines in tests.
- [`ConfirmationDialog.kt`](sample/KotlinAsync/examples/src/main/java/audio/rabid/debug/examples/ConfirmationDialog.kt)
- [`BlockingCalls.kt`](sample/KotlinAsync/examples/src/main/java/audio/rabid/debug/examples/BlockingCalls.kt)
- [`JobCancellationActivity.kt`](sample/KotlinAsync/examples/src/main/java/audio/rabid/debug/examples/JobCancellationActivity.kt)

## [`app`](sample/KotlinAsync/app)

Has a more complicated flow example (based loosely on [FIXD](https://www.fixdapp.com)), which includes a MVVM pattern, and a couple of helpers that are based on ones used in our actual code. This is intended to be a more real-world example of coroutines in Android. 
