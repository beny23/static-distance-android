package com.equalexperts.client.softwaredesignsystems.eatout

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.equalexperts.client.softwaredesignsystems.eatout.map.EatOutToHelpOutInfoWindow
import com.equalexperts.client.softwaredesignsystems.eatout.map.EatOutToHelpOutMarkerStyler
import com.equalexperts.client.softwaredesignsystems.eatout.services.Location
import com.equalexperts.client.softwaredesignsystems.eatout.services.LocationSearchResult
import com.equalexperts.client.softwaredesignsystems.eatout.services.RestaurantServiceResult
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.bonuspack.kml.KmlDocument
import org.osmdroid.bonuspack.kml.KmlPlacemark
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureMap()

        search.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(search.text.toString())
            }
            false
        }
    }

    private fun performSearch(text: String) {
        services().locationSearchService.search(text) {
            when (it) {
                is LocationSearchResult.Success -> mainMap.controller.animateTo(
                    GeoPoint(
                        it.location.latitude,
                        it.location.longitude
                    )
                )
                is LocationSearchResult.NotFound -> search.error =
                    getString(R.string.location_not_found)
                is LocationSearchResult.NetworkError -> search.error =
                    getString(R.string.location_network_error)
                is LocationSearchResult.ServerError -> search.error =
                    getString(R.string.location_server_error)
            }
        }
    }

    private fun configureMap() {
        mainMap.setMultiTouchControls(true)

        mainMap.post {
            val lastViewedLocation =
                services().lastViewedLocationService.lastViewedLocation
            val geoPoint = GeoPoint(lastViewedLocation.latitude, lastViewedLocation.longitude)
            mainMap.zoomToBoundingBox(BoundingBox.fromGeoPoints(listOf(geoPoint)), false)
            fetchRestaurants(lastViewedLocation)
        }

        mainMap.setScrollableAreaLimitLongitude(-8.6085, 1.6088, 0)
        mainMap.setScrollableAreaLimitLatitude(60.8573, 49.1916, 0)

        mainMap.minZoomLevel = 17.0
        mainMap.maxZoomLevel = 21.0

        var lastLocation = Location(0.0, 0.0)
        mainMap.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent): Boolean {
                val mapCenter = mainMap.mapCenter
                val targetLocation = Location(
                    mapCenter.latitude,
                    mapCenter.longitude
                )

                if (targetLocation.gridX != lastLocation.gridX || targetLocation.gridY != lastLocation.gridY) {
                    lastLocation = targetLocation
                    fetchRestaurants(targetLocation)
                }
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                return false
            }
        })
    }

    private fun fetchRestaurants(targetLocation: Location) {

        services().restaurantService.fetchRestaurants(
            targetLocation
        ) {
            when (it) {
                is RestaurantServiceResult.Success -> {
                    val document = KmlDocument()
                    it.restaurants.forEach { restaurant ->
                        document.mKmlRoot.add(
                            KmlPlacemark(
                                restaurant.toMarker(mainMap)
                            ).apply {
                                setExtendedData("postcode", restaurant.postcode)
                            })
                    }
                    displayRestaurants(document)
                }
            }
        }
    }

    private fun displayRestaurants(
        restaurants: KmlDocument
    ) {
        mainMap.post {
            val infoWindow = EatOutToHelpOutInfoWindow(mainMap) {
                services().restaurantInfoProvider.provideRestaurantInfo(
                    it
                )
            }
            val defaultMarker = ContextCompat.getDrawable(this, R.drawable.ic_marker)!!
            val eatOutToHelpOutMarkerStyler = EatOutToHelpOutMarkerStyler(defaultMarker, infoWindow)
            val restaurantOverlay = restaurants.mKmlRoot.buildOverlay(
                mainMap, null,
                eatOutToHelpOutMarkerStyler, restaurants
            )

            mainMap.overlays.removeAll { it is FolderOverlay }
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
        val mapCenter = mainMap.mapCenter
        services().lastViewedLocationService.lastViewedLocation =
            Location(mapCenter.latitude, mapCenter.longitude)
        mainMap.onPause()
    }

    private fun services() = (application as EatOutToHelpOutApplication).serviceLayer
}