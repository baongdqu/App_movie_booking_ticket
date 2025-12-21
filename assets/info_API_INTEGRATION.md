# Hướng dẫn Kết nối với OllamaWinApp qua Ngrok

Tài liệu này hướng dẫn cách kết nối các ứng dụng khác (Web, Mobile, Script...) với **OllamaWinApp** thông qua API Server và Ngrok Tunnel.

---

## 🚀 1. Thiết lập trên OllamaWinApp

Để mở cổng kết nối, bạn cần bật tính năng **API Server** và **Ngrok** trên giao diện chính.

1. Mở panel **Connection** bên trái (hoặc cuộn xuống dưới cùng của Sidebar).
2. Tích vào **Enable API Server**.
   - Port mặc định: `5123`
3. Tích vào **Enable Ngrok Tunnel**.
4. (Tùy chọn) Nhập **Ngrok Token** nếu bạn có tài khoản Ngrok (giúp kết nối ổn định hơn).
5. Đợi vài giây, bạn sẽ thấy địa chỉ **Ngrok URL** xuất hiện (ví dụ: `https://abc-123.ngrok-free.app`).
6. Nhấn nút **📋** để copy URL này.

---

## 📡 2. API Endpoints

Server cung cấp các đường dẫn (endpoints) sau để tương tác. Tất cả request đều trả về định dạng **JSON**.

### 🔹 Kiểm tra kết nối
- **URL**: `/health`
- **Method**: `GET`
- **Response**:
```json
{
  "status": "ok",
  "version": "1.0"
}
```

### 🔹 Lấy trạng thái hiện tại
- **URL**: `/status`
- **Method**: `GET`
- **Response**:
```json
{
  "host": "http://localhost:11434",
  "model": "llama3.2",
  "isStateful": true
}
```

### 🔹 Chat với AI
- **URL**: `/chat`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Body**:
```json
{
  "message": "Xin chào, bạn là ai?",
  "system_prompt": "Bạn là một trợ lý ảo vui tính."
}
```
- **Response**:
```json
{
  "response": "Chào bạn! Mình là AI đây, rất vui được gặp bạn! 😄"
}
```

### 🔹 Lấy danh sách Model
- **URL**: `/models`
- **Method**: `GET`
- **Response**:
```json
{
  "models": ["llama3.2", "mistral", "gemma"],
  "current": "llama3.2"
}
```

### 🔹 Đổi Model
- **URL**: `/model`
- **Method**: `POST`
- **Body**:
```json
{
  "model": "mistral"
}
```
- **Response**:
```json
{
  "success": true,
  "model": "mistral"
}
```

---

## 💻 3. Ví dụ Code Kết Nối

Thay `NGROK_URL` bằng địa chỉ bạn copy được từ bước 1.

### 🐍 Python

```python
import requests
import json

NGROK_URL = "https://your-ngrok-url.ngrok-free.app"

def chat_with_ai(message):
    url = f"{NGROK_URL}/chat"
    headers = {"Content-Type": "application/json"}
    payload = {
        "message": message,
        "system_prompt": "Trả lời ngắn gọn và hài hước."
    }

    try:
        response = requests.post(url, json=payload, headers=headers)
        if response.status_code == 200:
            data = response.json()
            print("🤖 AI:", data.get("response"))
        else:
            print(f"Error ({response.status_code}):", response.text)
    except Exception as e:
        print("Connection failed:", e)

# Test
chat_with_ai("Tại sao bầu trời lại màu xanh?")
```

### 📜 JavaScript (Node.js / Browser)

```javascript
const NGROK_URL = "https://your-ngrok-url.ngrok-free.app";

async function chatWithAI(message) {
    try {
        const response = await fetch(`${NGROK_URL}/chat`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                message: message,
                system_prompt: "You are a helpful assistant."
            })
        });

        const data = await response.json();
        if (data.response) {
            console.log("🤖 AI:", data.response);
        } else {
            console.error("Error:", data);
        }
    } catch (error) {
        console.error("Connection failed:", error);
    }
}

// Test
chatWithAI("Viết một đoạn code Hello World bằng Python");
```

### 🐚 cURL (Command Line)

```bash
curl -X POST https://your-ngrok-url.ngrok-free.app/chat \
     -H "Content-Type: application/json" \
     -d "{\"message\": \"Hello from terminal!\"}"
```

---

## ⚠️ Lưu ý quan trọng

1. **Ngrok Free Plan**: Địa chỉ URL sẽ thay đổi mỗi khi bạn tắt/bật lại Ngrok.
2. **Bảo mật**: API Server hiện tại **không có xác thực** (authentication). Bất kỳ ai có URL đều có thể gửi tin nhắn. Hãy cẩn thận khi chia sẻ URL.
3. **Timeout**: Nếu model trả lời quá lâu (>60s), request có thể bị timeout. Hãy cân nhắc dùng model nhỏ hoặc chia nhỏ câu hỏi.

---

## 🛠 Khắc phục lỗi

**Lỗi: `Tunnel xyz not found`**
- Ngrok session đã hết hạn hoặc bị tắt. Hãy tắt và bật lại Ngrok trong app để lấy URL mới.

**Lỗi: `Connection Refused`**
- Đảm bảo bạn đã tích vào **Enable API Server** trong app.

**Lỗi: `ERR_NGROK_6022`**
- Bạn đang gửi quá nhiều request cùng lúc (rate limit của bản Free). Hãy chờ một chút rồi thử lại.
