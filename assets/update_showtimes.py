#!/usr/bin/env python3
"""
Script to update movie showtimes in Firebase export JSON.
Updates all Bookings dates to random dates between 01/12/2025 and 01/02/2026.
Also generates multiple showtimes per movie for a more realistic experience.
"""

import json
import random
from datetime import datetime, timedelta

# Configuration
INPUT_FILE = r"c:\Users\s3cr3t\AndroidStudioProjects\App_movie_booking_ticket 2025-12-22 07-12-24.png\assets\app-movie-booking-ticket-default-rtdb-export.json"
OUTPUT_FILE = INPUT_FILE  # Overwrite original

# Date range: 01/12/2025 to 01/02/2026
START_DATE = datetime(2025, 12, 1)
END_DATE = datetime(2026, 2, 1)

# Bảng giá vé hợp lý cho từng phim (VND)
MOVIE_PRICES = {
    "Damaged": 75000,
    "Dune: Part Two": 120000,
    "Godzilla-Kong": 110000,
    "Immaculate": 85000,
    "Kung Fu Panda 4": 80000,
    "No Way Up": 75000,
    "Ordinary Angels": 70000,
    "Rebel Moon": 95000,
    "The Fall Guy": 90000,
    "The Gorge": 85000,
    "The Three Musketeers": 90000,
}

# Giá mặc định nếu phim không có trong bảng
DEFAULT_PRICE = 80000

# Available showtimes (hours)
SHOWTIME_HOURS = ["09:00", "10:30", "12:00", "13:30", "15:00", "16:30", "18:00", "19:30", "21:00", "22:30"]

def generate_random_date():
    """Generate a random date between START_DATE and END_DATE."""
    delta = END_DATE - START_DATE
    random_days = random.randint(0, delta.days)
    return START_DATE + timedelta(days=random_days)

def generate_showtimes_for_movie(num_showtimes=5):
    """Generate multiple random showtimes for a movie."""
    showtimes = []
    used_dates = set()
    
    for _ in range(num_showtimes):
        # Generate unique date-time combination
        attempts = 0
        while attempts < 100:
            date = generate_random_date()
            time = random.choice(SHOWTIME_HOURS)
            date_str = date.strftime("%Y-%m-%d")
            key = f"{date_str}_{time}"
            if key not in used_dates:
                used_dates.add(key)
                showtimes.append(key)
                break
            attempts += 1
    
    return showtimes

def create_seats_template():
    """Create a seats template with random booking rate."""
    rows = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K']
    seats = {}
    
    # Tỷ lệ ghế đã đặt ngẫu nhiên từ 10% đến 90%
    booking_rate = random.uniform(0.1, 0.9)
    
    for row in rows:
        for col in range(1, 9):  # 8 seats per row
            seat_id = f"{row}{col}"
            seats[seat_id] = "booked" if random.random() < booking_rate else "available"
    return seats

def sanitize_firebase_key(key):
    """Remove invalid Firebase key characters: $ # [ ] . /"""
    invalid_chars = '$#[]./'
    for char in invalid_chars:
        key = key.replace(char, '')
    return key.strip()

def main():
    print("Loading JSON file...")
    with open(INPUT_FILE, 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    if 'Movies' not in data:
        print("Error: 'Movies' node not found in JSON!")
        return
    
    if 'Cinemas' not in data:
        print("Error: 'Cinemas' node not found in JSON!")
        return
    
    movies = data['Movies']
    cinemas = data['Cinemas']
    print(f"Found {len(movies)} movies in Movies")
    print(f"Found {len(cinemas)} cinemas")
    
    # Create new Bookings for ALL movies
    new_bookings = {}
    
    for movie in movies:
        movie_name = movie.get('Title', 'Unknown')
        
        # Sanitize movie name to remove Firebase invalid characters
        safe_movie_name = sanitize_firebase_key(movie_name)
        
        print(f"Processing: {safe_movie_name}")
        
        # Generate 3-7 random showtimes for each movie
        num_showtimes = random.randint(3, 7)
        new_showtime_keys = generate_showtimes_for_movie(num_showtimes)
        
        new_bookings[safe_movie_name] = {}
        for showtime_key in new_showtime_keys:
            # Random 1-5 cinemas for each showtime
            num_cinemas = random.randint(1, min(5, len(cinemas)))
            selected_cinemas = random.sample(cinemas, num_cinemas)
            
            # Create cinemas info for this showtime
            cinemas_info = {}
            for cinema in selected_cinemas:
                cinema_id = sanitize_firebase_key(cinema.get('id', 'unknown'))
                # Giá vé ngẫu nhiên cho mỗi rạp: 70k, 100k, 150k
                price_per_seat = random.choice([70000, 100000, 150000])
                cinemas_info[cinema_id] = {
                    "name": cinema.get('name', 'Unknown Cinema'),
                    "address": cinema.get('address', ''),
                    "pricePerSeat": price_per_seat,
                    "seats": create_seats_template()
                }
            
            new_bookings[safe_movie_name][showtime_key] = {
                "cinemas": cinemas_info
            }
        
        print(f"  -> Generated {len(new_showtime_keys)} showtimes")
    
    # Update the data
    data['Bookings'] = new_bookings
    
    print(f"\nTotal movies with bookings: {len(new_bookings)}")
    print("\nSaving updated JSON file...")
    with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print("Done! Showtimes updated successfully.")
    print(f"Date range: {START_DATE.strftime('%d/%m/%Y')} - {END_DATE.strftime('%d/%m/%Y')}")

if __name__ == "__main__":
    main()
