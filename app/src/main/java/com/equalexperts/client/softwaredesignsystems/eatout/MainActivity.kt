package com.equalexperts.client.softwaredesignsystems.eatout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.osmdroid.util.BoundingBox

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainMap.post {
            mainMap.zoomToBoundingBox(BoundingBox(51.58663191759393, -0.03303900824656125, 51.426609140984745, -0.20833197699653283), false)
        }
    }
}