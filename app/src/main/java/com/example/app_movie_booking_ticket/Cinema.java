package com.example.app_movie_booking_ticket;

/**
 * Model class representing a Cinema/Movie Theater
 * Used with Google Places API Nearby Search results
 */
public class Cinema {
    private String placeId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private double rating;
    private int userRatingsTotal;
    private double distance; // in kilometers
    private String photoReference;
    private boolean isOpenNow;
    private boolean hasOpeningHours;

    public Cinema() {
        // Default constructor
    }

    public Cinema(String placeId, String name, String address, double latitude, double longitude) {
        this.placeId = placeId;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public void setUserRatingsTotal(int userRatingsTotal) {
        this.userRatingsTotal = userRatingsTotal;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public boolean isOpenNow() {
        return isOpenNow;
    }

    public void setOpenNow(boolean openNow) {
        isOpenNow = openNow;
    }

    public boolean hasOpeningHours() {
        return hasOpeningHours;
    }

    public void setHasOpeningHours(boolean hasOpeningHours) {
        this.hasOpeningHours = hasOpeningHours;
    }

    /**
     * Calculate distance between two points using Haversine formula
     * 
     * @param userLat User's latitude
     * @param userLng User's longitude
     */
    public void calculateDistance(double userLat, double userLng) {
        final int R = 6371; // Radius of the Earth in km

        double latDistance = Math.toRadians(this.latitude - userLat);
        double lngDistance = Math.toRadians(this.longitude - userLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(this.latitude))
                        * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        this.distance = R * c;
    }

    /**
     * Get photo URL from Google Places API
     * 
     * @param apiKey   Google API Key
     * @param maxWidth Max width of the photo
     * @return Photo URL or null if no photo reference
     */
    public String getPhotoUrl(String apiKey, int maxWidth) {
        if (photoReference == null || photoReference.isEmpty()) {
            return null;
        }
        return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=" + maxWidth
                + "&photo_reference=" + photoReference
                + "&key=" + apiKey;
    }

    /**
     * Get formatted distance string
     * 
     * @return Distance string like "1.2 km" or "500 m"
     */
    public String getFormattedDistance() {
        if (distance < 1) {
            return String.format("%.0f m", distance * 1000);
        } else {
            return String.format("%.1f km", distance);
        }
    }
}
