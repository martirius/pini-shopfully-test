package pini.mattia.shopfullytest.data

import kotlinx.serialization.Serializable

@Serializable
data class ApiCallResponse(
    //metadata class is needed? not useful so far so omitted
    val data: List<FlyerWrapper>
)
