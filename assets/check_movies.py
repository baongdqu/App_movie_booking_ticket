#!/usr/bin/env python3
"""Check and sync Movies with Bookings"""

import json

INPUT_FILE = r"c:\Users\s3cr3t\AndroidStudioProjects\App_movie_booking_ticket 2025-12-22 07-12-24.png\assets\app-movie-booking-ticket-default-rtdb-export.json"

with open(INPUT_FILE, 'r', encoding='utf-8') as f:
    data = json.load(f)

movies = data.get('Movies', [])
bookings = data.get('Bookings', {})

movie_titles = [m.get('Title', 'Unknown') for m in movies]
booking_titles = list(bookings.keys())

print(f"=== MOVIES ({len(movie_titles)} phim) ===")
for i, title in enumerate(movie_titles, 1):
    print(f"  {i}. {title}")

print(f"\n=== BOOKINGS ({len(booking_titles)} phim) ===")
for i, title in enumerate(booking_titles, 1):
    print(f"  {i}. {title}")

# Find missing
missing = [m for m in movie_titles if m not in booking_titles]
extra = [b for b in booking_titles if b not in movie_titles]

print(f"\n=== THIẾU TRONG BOOKINGS ({len(missing)} phim) ===")
for m in missing:
    print(f"  - {m}")

if extra:
    print(f"\n=== CÓ TRONG BOOKINGS NHƯNG KHÔNG CÓ TRONG MOVIES ({len(extra)}) ===")
    for e in extra:
        print(f"  - {e}")
