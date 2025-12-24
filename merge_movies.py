import json

# Read the database file
with open(r'c:\Users\s3cr3t\AndroidStudioProjects\App_movie_booking_ticket 2025-12-22 07-12-24.png\assets\app-movie-booking-ticket-default-rtdb-export.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Get Trends and Upcomming arrays
trends = data.get('Trends', [])
upcomming = data.get('Upcomming', [])

print(f"Trends count: {len(trends)}")
print(f"Upcomming count: {len(upcomming)}")

# Combine into Movies array
movies = []

# Add all Trends (isUpcoming should be false or not set)
for movie in trends:
    movie['isUpcoming'] = False
    movies.append(movie)

# Add all Upcomming (isUpcoming should be true)
for movie in upcomming:
    movie['isUpcoming'] = True
    movies.append(movie)

print(f"Total Movies: {len(movies)}")

# Update the data
data['Movies'] = movies

# Remove old nodes
if 'Trends' in data:
    del data['Trends']
if 'Upcomming' in data:
    del data['Upcomming']

# Write back to file
with open(r'c:\Users\s3cr3t\AndroidStudioProjects\App_movie_booking_ticket 2025-12-22 07-12-24.png\assets\app-movie-booking-ticket-default-rtdb-export.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=2, ensure_ascii=False)

print("Done! Movies merged successfully.")
