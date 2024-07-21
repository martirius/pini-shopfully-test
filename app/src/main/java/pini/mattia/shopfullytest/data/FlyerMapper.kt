package pini.mattia.shopfullytest.data

import pini.mattia.shopfullytest.domain.flyer.Flyer
import javax.inject.Inject

class FlyerMapper @Inject constructor() {
    fun map(flyerDTO: FlyerDTO): Flyer = with(flyerDTO) {
        Flyer(
            id = id,
            retailerId = retailerId,
            title = title,
            isXL = isXL,
            flyerBackground = (if (isXL) XL_BG_URL else NORMAL_BG_URL).replace(
                "<id>",
                id.toString()
            )
        )
    }

    companion object {
        private const val NORMAL_BG_URL =
            "https://it-it-media.shopfully.cloud/images/volantini/<id>@3x.jpg"
        private const val XL_BG_URL =
            "https://it-it-media.shopfully.cloud/images/volantini/xl_custom_<id>.jpg"
    }
}