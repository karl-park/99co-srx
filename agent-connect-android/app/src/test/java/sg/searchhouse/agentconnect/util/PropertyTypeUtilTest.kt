package sg.searchhouse.agentconnect.util

import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum
import sg.searchhouse.agentconnect.enumeration.api.ListingEnum.PropertySubType.*

class PropertyTypeUtilTest {

    class IsCondo {
        @Test
        fun whenInputCondominium_thenReturnTrue() {
            val output = PropertyTypeUtil.isCondo(CONDOMINIUM.type)
            assertTrue(output)
        }

        @Test
        fun whenInputApartment_thenReturnTrue() {
            assertTrue(PropertyTypeUtil.isCondo(APARTMENT.type))
        }

        @Test
        fun whenInputMinusOne_thenReturnFalse() {
            assertFalse(PropertyTypeUtil.isCondo(-1))
        }

        @Test
        fun whenInputLongIntNumber_thenReturnFalse() {
            assertFalse(PropertyTypeUtil.isCondo(111111111))
        }

        @Test
        fun whenInputDetachedLanded_thenReturnFalse() {
            assertFalse(PropertyTypeUtil.isCondo(DETACHED.type))
        }
    }

    class IsHDB {
        @Test
        fun whenInputHDB4Room_thenReturnTrue() {
            assertTrue(PropertyTypeUtil.isHDB(HDB_4_ROOMS.type))
        }

        @Test
        fun whenInputSemiDetachedType_thenReturnFalse() {
            assertFalse(PropertyTypeUtil.isHDB(SEMI_DETACHED.type))
        }

        @Test
        fun whenInputMinusNumber_thenReturnFalse() {
            assertFalse(PropertyTypeUtil.isHDB(-2))
        }

        @Test
        fun whenInputNotPropertyTypeNumber_thenReturnFalse() {
            assertFalse(PropertyTypeUtil.isHDB(568))
        }
    }

    class GetPurposeMainType {

        @Test
        fun whenInputResidentialPropertyPurpose_thenReturnResidentialMainType() {
            val output =
                PropertyTypeUtil.getPurposeMainType(ListingEnum.PropertyPurpose.RESIDENTIAL)
            Assert.assertEquals(ListingEnum.PropertyMainType.RESIDENTIAL, output)
        }

        @Test
        fun whenInputCommercialPropertyPurpose_thenReturnCommercialMainType() {
            val output =
                PropertyTypeUtil.getPurposeMainType(ListingEnum.PropertyPurpose.COMMERCIAL)
            Assert.assertEquals(ListingEnum.PropertyMainType.COMMERCIAL, output)
        }

    }

    class GetPropertyMainType {

        @Test
        fun whenInputHDBExecutiveSubType_thenReturnHDBMainType() {
            val output = PropertyTypeUtil.getPropertyMainType(HDB_EXECUTIVE.type)
            Assert.assertEquals(ListingEnum.PropertyMainType.HDB, output)
        }

        @Test
        fun whenInputApartmentSubType_thenReturnCondoMainType() {
            val output = PropertyTypeUtil.getPropertyMainType(APARTMENT.type)
            Assert.assertEquals(ListingEnum.PropertyMainType.CONDO, output)
        }

        @Test
        fun whenInputTerraceSubType_thenReturnLandedMainType() {
            val output = PropertyTypeUtil.getPropertyMainType(TERRACE.type)
            Assert.assertEquals(ListingEnum.PropertyMainType.LANDED, output)
        }

        @Test
        fun whenInputMinusOne_thenReturnNullValue() {
            val output = PropertyTypeUtil.getPropertyMainType(-1)
            Assert.assertNull(output)
        }

        @Test
        fun whenInputNotPropertyTypeNumber_thenReturnNullValue() {
            val output = PropertyTypeUtil.getPropertyMainType(100)
            Assert.assertNull(output)
        }

    }

    class IsAllResidentialSubTypes {

        @Test
        fun whenInputAllResidentialSubTypes_thenReturnTrue() {
            val output = PropertyTypeUtil.isAllResidentialSubTypes(
                listOf(
                    HDB_3_ROOMS,
                    HDB_4_ROOMS,
                    HDB_5_ROOMS,
                    HDB_EXECUTIVE,
                    TERRACE,
                    CONDOMINIUM,
                    APARTMENT,
                    SEMI_DETACHED,
                    DETACHED,
                    HDB_1_ROOM,
                    HDB_2_ROOMS,
                    HDB_HUDC,
                    HDB_JUMBO
                )
            )
            assertTrue(output)
        }

        @Test
        fun whenInputHDBSubTypes_thenReturnFalse() {
            val output = PropertyTypeUtil.isAllResidentialSubTypes(
                listOf(
                    HDB_3_ROOMS,
                    HDB_4_ROOMS,
                    HDB_5_ROOMS,
                    HDB_EXECUTIVE
                )
            )
            assertFalse(output)
        }

        @Test
        fun whenInputAllCommercialSubTypes_thenReturnFalse() {
            val output = PropertyTypeUtil.isAllResidentialSubTypes(
                listOf(
                    OFFICE,
                    WAREHOUSE,
                    SHOP_HOUSE,
                    RETAIL,
                    LAND,
                    FACTORY,
                    HDB_SHOP_HOUSE
                )
            )
            assertFalse(output)
        }

        @Test
        fun whenInputEmptyList_thenReturnFalse() {
            val output = PropertyTypeUtil.isAllResidentialSubTypes(listOf())
            assertFalse(output)
        }
    }
}