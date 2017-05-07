

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