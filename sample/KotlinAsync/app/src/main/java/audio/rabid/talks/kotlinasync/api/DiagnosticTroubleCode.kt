package audio.rabid.talks.kotlinasync.api

import java.io.Serializable

/**
 * Created by cjk on 9/17/17.
 *
 * A model from our [Api] which represents an error code from the vehicle and shows info about it
 */
data class DiagnosticTroubleCode(
        val code: String,
        val name: String,
        val description: String
) : Serializable