package audio.rabid.talks.kotlinasync.viewmodel.implementation

import audio.rabid.talks.kotlinasync.api.Api
import audio.rabid.talks.kotlinasync.api.DiagnosticTroubleCode
import rx.Observable

/**
 * Created by cjk on 9/17/17.
 */
class MockApi : Api {

    private val codes = mapOf(
            "P0171" to DiagnosticTroubleCode(
                    code = "P0171",
                    name = "Fuel Mixture Imbalance: Too Lean",
                    description = "Your engine is getting too much air and/or not enough fuel, this " +
                            "is known as a \"Lean\" condition. Excess oxygen is getting into your engine, " +
                            "which makes the combustion process less efficient. This could be caused by " +
                            "many different factors."
            ),
            "P0300" to DiagnosticTroubleCode(
                    code = "P0300",
                    name = "Cylinder 3 Misfire",
                    description = "Cylinders are parts of your engine and contain the combustion " +
                            "process (which essentially provides power for your car). Random misfires " +
                            "means that combustion is not happening in the right cylinder at the right time."
            )
    )

    override fun lookupTroubleCode(code: String): Observable<DiagnosticTroubleCode>
            = codes[code]?.let { Observable.just(it) } ?: Observable.error(Exception("Code not found"))
}