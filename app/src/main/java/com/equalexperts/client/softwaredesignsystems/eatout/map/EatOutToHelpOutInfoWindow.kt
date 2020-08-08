package com.equalexperts.client.softwaredesignsystems.eatout.map

import android.widget.Button
import com.equalexperts.client.softwaredesignsystems.eatout.R
import com.equalexperts.client.softwaredesignsystems.eatout.services.Restaurant
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class EatOutToHelpOutInfoWindow(
    mainMap: MapView?,
    private val onRestaurantViewClicked: (Restaurant) -> Unit
) : MarkerInfoWindow(R.layout.cv_marker_window, mainMap) {

    private val viewButton by lazy { view.findViewById<Button>(R.id.viewRestaurant) }

    override fun onOpen(item: Any?) {
        super.onOpen(item)
        val marker = item as Marker
        viewButton.setOnClickListener {
            onRestaurantViewClicked(Restaurant(marker.title, marker.subDescription))
            marker.closeInfoWindow()
        }
    }

}
