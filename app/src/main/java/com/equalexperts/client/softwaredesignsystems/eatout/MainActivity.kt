package com.equalexperts.client.softwaredesignsystems.eatout

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.bonuspack.kml.KmlDocument
import org.osmdroid.bonuspack.kml.Style
import org.osmdroid.util.BoundingBox
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainMap.setMultiTouchControls(true)

        mainMap.post {
            mainMap.zoomToBoundingBox(BoundingBox(51.58663191759393, -0.03303900824656125, 51.426609140984745, -0.20833197699653283), false)
        }
        val defaultMarker = ContextCompat.getDrawable(this, R.drawable.ic_marker)
        val defaultBitmap = (defaultMarker as BitmapDrawable).bitmap
        val defaultStyle = Style(defaultBitmap, -0x6fefef56, 5f, 0x20AA1010)

        thread {
            val restaurants = KmlDocument().apply {
                parseGeoJSON(GZIPInputStream(resources.openRawResource(R.raw.restaurants)).readBytes().toString(Charset.defaultCharset()))
            }
            mainMap.post {
                val restaurantOverlay = restaurants.mKmlRoot.buildOverlay(mainMap, defaultStyle, null, restaurants)
                mainMap.overlays.add(restaurantOverlay)
                mainMap.invalidate()
            }
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