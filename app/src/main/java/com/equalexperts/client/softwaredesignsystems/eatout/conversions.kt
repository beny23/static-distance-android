package com.equalexperts.client.softwaredesignsystems.eatout

import com.equalexperts.client.softwaredesignsystems.eatout.services.Location
import com.equalexperts.client.softwaredesignsystems.eatout.services.Restaurant
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

fun Restaurant.toMarker(mapView: MapView): Marker {
    return Marker(mapView).apply {
        title = name
        subDescription = postcode
        position = GeoPoint(location.latitude, location.longitude)
    }
}

fun Marker.toRestaurant(): Restaurant {
    return Restaurant(title, subDescription, position.toLocation())
}

private fun GeoPoint.toLocation(): Location {
    return Location(latitude, longitude)
}