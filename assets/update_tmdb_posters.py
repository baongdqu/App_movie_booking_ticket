"""
Script to update movie database with trailers from TMDB
API Key: f2840746096f50a2dde45692cf2b67c5
"""

import json
import requests
import time

API_KEY = "f2840746096f50a2dde45692cf2b67c5"
BASE_URL = "https://api.themoviedb.org/3"

def search_movie(title, year=None):
    """Search for a movie by title and optional year"""
    url = f"{BASE_URL}/search/movie"
    params = {
        "api_key": API_KEY,
        "query": title,
        "language": "en-US"
    }
    if year:
        params["year"] = year
    
    response = requests.get(url, params=params)
    if response.status_code == 200:
        results = response.json().get("results", [])
        if results:
            return results[0]
    return None

def get_movie_videos(movie_id):
    """Get movie videos (trailers, teasers, etc.)"""
    url = f"{BASE_URL}/movie/{movie_id}/videos"
    params = {
        "api_key": API_KEY,
        "language": "en-US"
    }
    
    response = requests.get(url, params=params)
    if response.status_code == 200:
        return response.json().get("results", [])
    return []

def get_youtube_trailer(movie_id):
    """Get the best YouTube trailer for a movie"""
    videos = get_movie_videos(movie_id)
    
    # Priority: Official Trailer > Trailer > Teaser > any YouTube video
    trailer = None
    
    for video in videos:
        if video.get("site") != "YouTube":
            continue
            
        video_type = video.get("type", "").lower()
        video_name = video.get("name", "").lower()
        
        # First priority: Official Trailer
        if video_type == "trailer" and "official" in video_name:
            return f"https://www.youtube.com/watch?v={video['key']}"
        
        # Second priority: Any Trailer
        if video_type == "trailer" and trailer is None:
            trailer = f"https://www.youtube.com/watch?v={video['key']}"
        
        # Third priority: Teaser
        if video_type == "teaser" and trailer is None:
            trailer = f"https://www.youtube.com/watch?v={video['key']}"
    
    # Fallback to any YouTube video
    if trailer is None and videos:
        for video in videos:
            if video.get("site") == "YouTube":
                return f"https://www.youtube.com/watch?v={video['key']}"
    
    return trailer

def update_movie_trailer(movie):
    """Update a movie entry with YouTube trailer from TMDB"""
    title = movie.get("Title", "")
    year = movie.get("Year")
    
    print(f"Searching for: {title} ({year})")
    
    # Search for the movie
    search_result = search_movie(title, year)
    if not search_result:
        search_result = search_movie(title)
    
    if search_result:
        movie_id = search_result["id"]
        trailer_url = get_youtube_trailer(movie_id)
        
        if trailer_url:
            movie["Trailer"] = trailer_url
            print(f"  ✓ Updated trailer: {trailer_url}")
            time.sleep(0.2)
            return True
        else:
            print(f"  ✗ No trailer found")
    else:
        print(f"  ✗ Movie not found")
    
    return False

def main():
    # Load the database
    with open("app-movie-booking-ticket-default-rtdb-export.json", "r", encoding="utf-8") as f:
        data = json.load(f)
    
    updated_count = 0
    
    # Update Items
    print("\n" + "="*60)
    print("UPDATING TRAILERS - ITEMS (Now Playing)")
    print("="*60)
    for movie in data.get("Items", []):
        if update_movie_trailer(movie):
            updated_count += 1
    
    # Update Upcomming
    print("\n" + "="*60)
    print("UPDATING TRAILERS - UPCOMMING")
    print("="*60)
    for movie in data.get("Upcomming", []):
        if update_movie_trailer(movie):
            updated_count += 1
    
    # Save updated database
    with open("app-movie-booking-ticket-default-rtdb-export.json", "w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print("\n" + "="*60)
    print(f"DONE! Updated {updated_count} trailers")
    print("="*60)

if __name__ == "__main__":
    main()
