package pini.mattia.shopfullytest

import org.junit.Test

import org.junit.Assert.*
import pini.mattia.shopfullytest.data.FlyerDTO
import pini.mattia.shopfullytest.data.FlyerMapper
import pini.mattia.shopfullytest.domain.flyer.Flyer

class FlyerMapperTest {

    private val normalFlyerTest = Flyer(5, 2, "Volantino di test", false, "https://it-it-media.shopfully.cloud/images/volantini/5@3x.jpg")
    private val xlFlyerTest = Flyer(8, 4, "Volantino di test XL", true, "https://it-it-media.shopfully.cloud/images/volantini/xl_custom_8.jpg")

    private val flyerMapper = FlyerMapper()
    @Test
    fun normal_mapping_is_correct() {
        val normalFlyerDtoTest = FlyerDTO(normalFlyerTest.id, normalFlyerTest.retailerId, normalFlyerTest.title, normalFlyerTest.isXL)

        val mappedFlyer = flyerMapper.map(normalFlyerDtoTest)

        assertEquals(normalFlyerTest, mappedFlyer)
    }

    @Test
    fun xl_mapping_is_correct() {
        val xlFlyerDtoTest = FlyerDTO(xlFlyerTest.id, xlFlyerTest.retailerId, xlFlyerTest.title, xlFlyerTest.isXL)

        val mappedFlyer = flyerMapper.map(xlFlyerDtoTest)

        assertEquals(xlFlyerTest, mappedFlyer)
    }
}