package com.equalexperts.client.softwaredesignsystems.eatout.map

import android.graphics.drawable.Drawable
import org.osmdroid.bonuspack.kml.*
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline

class EatOutToHelpOutMarkerStyler(private val defaultMarker: Drawable) :
    KmlFeature.Styler {
    override fun onPolygon(
        polygon: Polygon?,
        kmlPlacemark: KmlPlacemark?,
        kmlPolygon: KmlPolygon?
    ) {

    }

    override fun onLineString(
        polyline: Polyline?,
        kmlPlacemark: KmlPlacemark?,
        kmlLineString: KmlLineString?
    ) {
    }

    override fun onFeature(overlay: Overlay?, kmlFeature: KmlFeature?) {
    }

    override fun onPoint(
        marker: Marker?,
        kmlPlacemark: KmlPlacemark?,
        kmlPoint: KmlPoint?
    ) {
        marker?.icon = defaultMarker
        marker?.subDescription = kmlPlacemark?.getExtendedData("postcode")
    }

    override fun onTrack(
        polyline: Polyline?,
        kmlPlacemark: KmlPlacemark?,
        kmlTrack: KmlTrack?
    ) {}
}