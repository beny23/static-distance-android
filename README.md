# static-distance-android
The Android application for the [Eat Out To Help Out website](https://github.com/beny23/static-distance).

Don't forget the [Eat Out To Help Out iOS app](https://github.com/beny23/static-distance-app).

## Data generation
The application currently bundles restaurant information as a static resource.

To build the static resource you will need:
 * Python 3
 * [JQ](https://stedolan.github.io/jq/)
 * NPM
 
To build the static resource:

```bash
cd dataGeneration
npm install csv2geojson # You only need to do this once
./build.sh
cp target/restaurants.geojson.gz ../app/src/main/res/raw/restaurants.gz
```

## OpenSource Attributions
This project uses the following OpenSource libraries:
 * [omsdroid OpenStreetMaps library](https://github.com/osmdroid/osmdroid) 
 * [osmbonuspack library](https://github.com/MKergall/osmbonuspack)
 * [Retrofit](https://square.github.io/retrofit/)
 * [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
