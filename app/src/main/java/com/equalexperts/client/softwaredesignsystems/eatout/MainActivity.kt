package com.equalexperts.client.softwaredesignsystems.eatout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.equalexperts.client.softwaredesignsystems.eatout.map.EatOutToHelpOutInfoWindow
import com.equalexperts.client.softwaredesignsystems.eatout.map.EatOutToHelpOutMarkerStyler
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.bonuspack.kml.KmlDocument
import org.osmdroid.util.BoundingBox
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureMap()

        fetchRestaurants()
    }

    private fun configureMap() {
        mainMap.setMultiTouchControls(true)

        mainMap.post {
            mainMap.zoomToBoundingBox(
                BoundingBox(
                    51.51461167022675,
                    -0.11984109878540039,
                    51.50305280863187,
                    -0.13142824172973633
                ), false
            )
        }

        mainMap.setScrollableAreaLimitLongitude(-8.6085, 1.6088, 0)
        mainMap.setScrollableAreaLimitLatitude(60.8573, 49.1916, 0)

        mainMap.minZoomLevel = 17.0
        mainMap.maxZoomLevel = 21.0
    }

    private fun fetchRestaurants() {
        thread {

            val restaurantsGeoJSON =
                GZIPInputStream(resources.openRawResource(R.raw.restaurants)).readBytes()
                    .toString(Charset.defaultCharset())

            val restaurants = KmlDocument().apply {
                parseGeoJSON(restaurantsGeoJSON)
            }

            displayRestaurants(restaurants)
        }
    }

    private fun displayRestaurants(
        restaurants: KmlDocument
    ) {
        mainMap.post {
            val infoWindow = EatOutToHelpOutInfoWindow(mainMap) {
                (application as EatOutToHelpOutApplication).serviceLayer.restaurantInfoProvider.provideRestaurantInfo(
                    it
                )
            }
            val defaultMarker = ContextCompat.getDrawable(this, R.drawable.ic_marker)!!
            val eatOutToHelpOutMarkerStyler = EatOutToHelpOutMarkerStyler(defaultMarker, infoWindow)
            val restaurantOverlay = restaurants.mKmlRoot.buildOverlay(
                mainMap, null,
                eatOutToHelpOutMarkerStyler, restaurants
            )

            mainMap.overlays.add(restaurantOverlay)
            mainMap.invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        mainMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        mainMap.onPause()
    }
}