import json
import requests
import time

API_KEY = "f2840746096f50a2dde45692cf2b67c5"
BASE_URL = "https://api.themoviedb.org/3"
IMAGE_BASE = "https://image.tmdb.org/t/p/w500"

def get_vietnamese_movies(pages=5):
    """Fetch Vietnamese movies (origin_country=VN)"""
    movies = []
    for page in range(1, pages + 1):
        url = f"{BASE_URL}/discover/movie"
        params = {
            "api_key": API_KEY,
            "with_origin_country": "VN",
            "language": "vi-VN",
            "sort_by": "popularity.desc",
            "page": page
        }
        response = requests.get(url, params=params)
        if response.status_code == 200:
            data = response.json()
            movies.extend(data.get("results", []))
            print(f"Page {page}: Got {len(data.get('results', []))} movies")
        time.sleep(0.3)
    return movies[:50]  # Limit to 50 movies

def get_movie_details(movie_id):
    """Get detailed movie info including credits and videos"""
    url = f"{BASE_URL}/movie/{movie_id}"
    params = {
        "api_key": API_KEY,
        "language": "vi-VN",
        "append_to_response": "credits,videos"
    }
    response = requests.get(url, params=params)
    if response.status_code == 200:
        return response.json()
    return None

def format_movie(movie_data, details):
    """Format movie data to match app's Movie model"""
    # Get cast (top 6)
    casts = []
    if details and "credits" in details:
        for cast in details["credits"].get("cast", [])[:6]:
            casts.append({
                "Actor": cast.get("name", ""),
                "PicUrl": f"{IMAGE_BASE}{cast.get('profile_path', '')}" if cast.get('profile_path') else "",
                "Character": cast.get("character", "")
            })
    
    # Get trailer
    trailer = ""
    if details and "videos" in details:
        for video in details["videos"].get("results", []):
            if video.get("type") == "Trailer" and video.get("site") == "YouTube":
                trailer = f"https://www.youtube.com/watch?v={video.get('key')}"
                break
    
    # Get genres
    genres = []
    if details and "genres" in details:
        genres = [g.get("name", "") for g in details.get("genres", [])]
    
    # Get runtime
    runtime = details.get("runtime", 0) if details else 0
    hours = runtime // 60
    mins = runtime % 60
    time_str = f"{hours}h {mins}m" if hours > 0 else f"{mins}m"
    
    # Get vote average as IMDB score
    imdb = movie_data.get("vote_average", 0)
    
    return {
        "Casts": casts,
        "Description": movie_data.get("overview", "Chưa có mô tả"),
        "Genre": genres if genres else ["Phim Việt Nam"],
        "Imdb": round(imdb, 1),
        "Poster": f"{IMAGE_BASE}{movie_data.get('poster_path', '')}" if movie_data.get('poster_path') else "",
        "Price": 75000,  # Default price
        "Time": time_str,
        "Title": movie_data.get("title", "Không rõ"),
        "Trailer": trailer,
        "Year": int(movie_data.get("release_date", "2024")[:4]) if movie_data.get("release_date") else 2024,
        "isUpcoming": False,
        "movieID": f"vn_{movie_data.get('id', 0)}",
        "Pictures": []
    }

def main():
    print("Fetching Vietnamese movies from TMDB...")
    movies = get_vietnamese_movies(pages=5)
    print(f"\nTotal movies found: {len(movies)}")
    
    formatted_movies = []
    for i, movie in enumerate(movies):
        print(f"Processing {i+1}/{len(movies)}: {movie.get('title', 'Unknown')}")
        details = get_movie_details(movie["id"])
        formatted = format_movie(movie, details)
        formatted_movies.append(formatted)
        time.sleep(0.3)  # Rate limiting
    
    # Load existing database
    db_path = r'c:\Users\s3cr3t\AndroidStudioProjects\App_movie_booking_ticket 2025-12-22 07-12-24.png\assets\app-movie-booking-ticket-default-rtdb-export.json'
    with open(db_path, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    # Get existing movies
    existing_movies = data.get("Movies", [])
    print(f"\nExisting movies in database: {len(existing_movies)}")
    
    # Add new Vietnamese movies
    existing_movies.extend(formatted_movies)
    data["Movies"] = existing_movies
    
    # Save updated database
    with open(db_path, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print(f"\nDone! Added {len(formatted_movies)} Vietnamese movies")
    print(f"Total movies in database: {len(existing_movies)}")

if __name__ == "__main__":
    main()
