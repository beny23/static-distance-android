package com.equalexperts.client.softwaredesignsystems.eatout

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.equalexperts.client.softwaredesignsystems.eatout.services.Location
import com.equalexperts.client.softwaredesignsystems.eatout.services.LocationSearchResult
import com.equalexperts.client.softwaredesignsystems.eatout.services.Restaurant
import com.equalexperts.client.softwaredesignsystems.eatout.services.RestaurantServiceResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.activity_main.*

class RestaurantClusterItem(val restaurant: Restaurant) : ClusterItem {
    private val gmapsPosition = LatLng(restaurant.location.latitude, restaurant.location.longitude)

    override fun getSnippet() = restaurant.postcode

    override fun getTitle() = restaurant.name

    override fun getPosition() = gmapsPosition
}

class MainActivity : AppCompatActivity() {

    private lateinit var map: GoogleMap
    private lateinit var markerClusterManager: ClusterManager<RestaurantClusterItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logo.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

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
                is LocationSearchResult.Success -> map.moveCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(it.location.latitude, it.location.longitude)
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
        var lastLocation = Location(0.0, 0.0)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it

            map.setMinZoomPreference(10.0f)
            map.setMaxZoomPreference(50.0f)
            markerClusterManager = ClusterManager(this, map)
            map.setOnCameraIdleListener(markerClusterManager)
            map.setOnMarkerClickListener(markerClusterManager)

            markerClusterManager.setOnClusterItemInfoWindowClickListener {
                services().restaurantInfoProvider.provideRestaurantInfo(it.restaurant)
            }

            val lastViewedLocation = services().lastViewedLocationService.lastViewedLocation

            map.moveCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(lastViewedLocation.latitude, lastViewedLocation.longitude)
                )
            )
            fetchRestaurants(lastViewedLocation)


            map.setOnCameraMoveListener {
                val targetLocation = Location(
                    map.cameraPosition.target.latitude,
                    map.cameraPosition.target.longitude
                )
                if (targetLocation.gridX != lastLocation.gridX || targetLocation.gridY != lastLocation.gridY) {
                    lastLocation = targetLocation
                    fetchRestaurants(targetLocation)
                }
            }
        }
    }

    private fun fetchRestaurants(targetLocation: Location) {

        services().restaurantService.fetchRestaurants(targetLocation) {
            when (it) {
                is RestaurantServiceResult.Success -> {
                    markerClusterManager.clearItems()
                    it.restaurants.forEach { restaurant ->
                        markerClusterManager.addItem(RestaurantClusterItem(restaurant))
                    }
                    markerClusterManager.cluster()
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        services().lastViewedLocationService.lastViewedLocation =
            Location(map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
    }

    private fun services() = (application as EatOutToHelpOutApplication).serviceLayer
}