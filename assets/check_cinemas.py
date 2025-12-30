#!/usr/bin/env python3
"""Check Cinemas structure"""

import json

INPUT_FILE = r"c:\Users\s3cr3t\AndroidStudioProjects\App_movie_booking_ticket 2025-12-22 07-12-24.png\assets\app-movie-booking-ticket-default-rtdb-export.json"

with open(INPUT_FILE, 'r', encoding='utf-8') as f:
    data = json.load(f)

cinemas = data.get('Cinemas', [])
print(f"Found {len(cinemas)} cinemas\n")

for i, cinema in enumerate(cinemas):
    print(f"{i+1}. {cinema.get('name', 'Unknown')} (ID: {cinema.get('id', 'N/A')})")
