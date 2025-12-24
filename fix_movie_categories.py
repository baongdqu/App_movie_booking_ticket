import json

# Load database
db_path = r'c:\Users\s3cr3t\AndroidStudioProjects\App_movie_booking_ticket 2025-12-22 07-12-24.png\assets\app-movie-booking-ticket-default-rtdb-export.json'
with open(db_path, 'r', encoding='utf-8') as f:
    data = json.load(f)

movies = data.get('Movies', [])
print(f"Tổng số phim: {len(movies)}")

# Reset all movies to have no isUpcoming (will be None = not in trending/upcoming, only in All)
for movie in movies:
    movie['isUpcoming'] = None

# === PHIM THỊNH HÀNH: 3 phim Việt + 4 phim nước ngoài ===
trending_titles = []

# 3 phim Việt Nam (movieID bắt đầu bằng vn_)
vn_count = 0
for movie in movies:
    if vn_count >= 3:
        break
    if movie.get('movieID', '').startswith('vn_') and movie.get('Imdb', 0) >= 5:
        movie['isUpcoming'] = False
        trending_titles.append(f"🇻🇳 {movie.get('Title', 'Unknown')}")
        vn_count += 1

# 4 phim nước ngoài (movieID KHÔNG bắt đầu bằng vn_)
foreign_count = 0
for movie in movies:
    if foreign_count >= 4:
        break
    if not movie.get('movieID', '').startswith('vn_') and movie.get('Imdb', 0) >= 6:
        movie['isUpcoming'] = False
        trending_titles.append(f"🌍 {movie.get('Title', 'Unknown')}")
        foreign_count += 1

print(f"\n✅ Phim thịnh hành (7 phim: 3 VN + 4 nước ngoài):")
for i, t in enumerate(trending_titles, 1):
    print(f"  {i}. {t}")

# === PHIM SẮP CHIẾU: 23 phim ===
upcoming_titles = []
count = 0

# Ưu tiên phim năm 2024+
for movie in movies:
    if count >= 23:
        break
    if movie.get('isUpcoming') is None and movie.get('Year', 2020) >= 2024:
        movie['isUpcoming'] = True
        upcoming_titles.append(movie.get('Title', 'Unknown'))
        count += 1

# Nếu chưa đủ, thêm phim khác
for movie in movies:
    if count >= 23:
        break
    if movie.get('isUpcoming') is None:
        movie['isUpcoming'] = True
        upcoming_titles.append(movie.get('Title', 'Unknown'))
        count += 1

print(f"\n✅ Phim sắp chiếu ({len(upcoming_titles)} phim):")
for i, t in enumerate(upcoming_titles, 1):
    print(f"  {i}. {t}")

# Count remaining movies (not in trending or upcoming)
other_count = sum(1 for m in movies if m.get('isUpcoming') is None)
print(f"\n📊 Phim khác (chỉ trong Tất cả phim): {other_count}")

# Save
data['Movies'] = movies
with open(db_path, 'w', encoding='utf-8') as f:
    json.dump(data, f, indent=2, ensure_ascii=False)

print("\n✅ Đã lưu database!")

# Verify counts
trending_count = sum(1 for m in movies if m.get('isUpcoming') == False)
upcoming_count = sum(1 for m in movies if m.get('isUpcoming') == True)
print(f"\n📊 Tổng kết:")
print(f"   - Phim thịnh hành: {trending_count}")
print(f"   - Phim sắp chiếu: {upcoming_count}")
print(f"   - Tất cả phim: {len(movies)}")
