
import json

try:
    with open('assets/app-movie-booking-ticket-default-rtdb-export.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    print("Keys in Bookings:")
    if 'Bookings' in data:
        for key in data['Bookings']:
            print(f"- '{key}'")
    else:
        print("Bookings node not found.")

    print("\nMatching Movies:")
    if 'Movies' in data:
        movies = data['Movies']
        if isinstance(movies, dict):
             for key, val in movies.items():
                title = val.get('Title', 'No Title')
                if '578' in title:
                    print(f"- Key: '{key}', Title: '{title}'")
        elif isinstance(movies, list):
             for i, m in enumerate(movies):
                if m:
                    title = m.get('Title', 'No Title')
                    if '578' in title:
                        print(f"- Index: {i}, Title: '{title}'")

except Exception as e:
    print(f"Error: {e}")
