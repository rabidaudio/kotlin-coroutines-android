package audio.rabid.talks.kotlinasync.api

import audio.rabid.talks.kotlinasync.api.DiagnosticTroubleCode
import retrofit2.http.GET
import retrofit2.http.Path
import rx.Observable

/**
 * Created by cjk on 9/17/17.
 *
 * A retrofit API representing some (imaginary) web service.
 */
interface Api {

    /**
     * Given an error code string, convert it to a [DiagnosticTroubleCode]
     */
    @GET("/codes/{code}")
    fun lookupTroubleCode(@Path("code") code: String): Observable<DiagnosticTroubleCode>
}