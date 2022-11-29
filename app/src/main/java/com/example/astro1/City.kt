package com.example.astro1

class City {
    public var cityName: String
    public var countryCode: String
    public var weather : String
    public var forecast : String
    public var latitude: Double
    public var longitude: Double
    public var lastRefresh : String

    constructor(cityName : String, countryCode: String, weather : String, forecast : String, latitude: Double, longitude: Double, lastRefresh : String){
        this.cityName = cityName
        this.countryCode = countryCode
        this.weather = weather
        this.forecast = forecast
        this.latitude = latitude
        this.longitude = longitude
        this.lastRefresh = lastRefresh
    }


}