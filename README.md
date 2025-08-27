# EdumentumBackend - API Documentation

## Giới thiệu

EdumentumBackend là backend API cho hệ thống EdumenTUM - nền tảng học tập và kiểm tra kiến thức trực tuyến. API này cung cấp các endpoint để quản lý người dùng, bài kiểm tra (quizzes), tags, và nhiều tính năng khác.

## Các Tính Năng Chính

- Quản lý người dùng và xác thực
- Tạo và quản lý bài kiểm tra (quizzes)
- Hỗ trợ tạo bài kiểm tra bằng AI
- Quản lý và gắn thẻ (tags) cho nội dung
- Tìm kiếm và lọc nội dung học tập

## API Quizzes

### Tạo mới Quiz

```
POST /api/v1/student/quizzes
```

Endpoint này cho phép tạo mới một quiz, bao gồm cả việc tự động tạo và liên kết với các tags. Đặc biệt hữu ích cho các quiz được tạo bởi AI.

#### Request Body

```json
{
  "title": "Tiêu đề Quiz",
  "description": "Mô tả chi tiết về quiz",
  "thumbnailUrl": "https://example.com/images/thumbnail.jpg",
  "difficulty": "EASY|MEDIUM|HARD",
  "estimatedTime": 30,
  "passingScore": 70,
  "maxAttempts": 3,
  "isAiGenerated": true,
  "aiModel": "GPT-4",
  "sourceType": "TEXT|IMAGE|VIDEO|DOCUMENT",
  "metaTitle": "Tiêu đề SEO",
  "metaDescription": "Mô tả SEO",
  "canonicalUrl": "https://example.com/quizzes/slug",
  "keywords": ["keyword1", "keyword2", "keyword3"],
  "visibility": "PUBLIC|PRIVATE|UNLISTED",
  "isPremium": false,
  "quizData": {
    "introduction": "Phần giới thiệu quiz",
    "instructions": "Hướng dẫn làm quiz",
    "questions": [
      {
        "id": 1,
        "text": "Nội dung câu hỏi?",
        "type": "MULTIPLE_CHOICE",
        "points": 1,
        "options": [
          {
            "id": "a",
            "text": "Đáp án A"
          },
          {
            "id": "b",
            "text": "Đáp án B"
          },
          {
            "id": "c",
            "text": "Đáp án C"
          },
          {
            "id": "d",
            "text": "Đáp án D"
          }
        ],
        "correctAnswer": "a",
        "explanation": "Giải thích đáp án đúng"
      }
    ],
    "summary": "Tóm tắt sau khi hoàn thành quiz"
  },
  "tags": [
    {
      "id": 1,  // Chỉ cung cấp nếu tag đã tồn tại trong hệ thống
      "name": "Tên Tag", // Bắt buộc nếu không có id
      "description": "Mô tả về tag",
      "icon": "tên-icon",
      "color": "#HEX-color"
    }
  ]
}
```

#### Response

```json
{
  "id": 123,
  "title": "Tiêu đề Quiz",
  "slug": "tieu-de-quiz",
  "description": "Mô tả chi tiết về quiz",
  "user": {
    "userId": 456,
    "username": "username",
    "email": "user@example.com"
  },
  "tags": [
    {
      "id": 1,
      "name": "Tên Tag",
      "slug": "ten-tag",
      "description": "Mô tả về tag",
      "icon": "tên-icon",
      "color": "#HEX-color"
    }
  ],
  // Các thông tin khác của quiz
}
```

### Xử lý Tags

API mới hỗ trợ xử lý tags một cách linh hoạt:

1. **Sử dụng tag đã tồn tại**: Cung cấp `id` của tag
   ```json
   {"id": 1, "name": "Tag Name"}
   ```

2. **Tạo tag mới**: Chỉ cung cấp `name` và các thông tin khác
   ```json
   {"name": "New Tag", "description": "Description", "icon": "icon-name", "color": "#4285F4"}
   ```

3. **Kết hợp cả hai**: Bạn có thể cung cấp một danh sách tag gồm cả tag đã tồn tại (có ID) và tag mới (chỉ có name)

## Các API Khác

### Lấy Quiz theo ID

```
GET /api/v1/student/quizzes/{quizId}
```

### Cập nhật Quiz

```
PUT /api/v1/student/quizzes/{quizId}
```

### Xóa Quiz

```
DELETE /api/v1/student/quizzes/{quizId}
```

### Lấy danh sách Quiz của người dùng hiện tại

```
GET /api/v1/student/quizzes
```

## Cài đặt và Chạy Dự Án

### Yêu cầu

- Java 17+
- PostgreSQL
- Redis (cho caching)

### Các bước cài đặt

1. Clone repository:
```bash
git clone https://github.com/yourusername/EdumentumBackend.git
```

2. Cấu hình database trong `application.properties`

3. Chạy ứng dụng:
```bash
./gradlew bootRun
```

## Môi trường

- **Dev**: http://localhost:8080
- **Production**: https://api.edumentum.com

## Hướng dẫn đóng góp

1. Fork dự án
2. Tạo branch mới (`git checkout -b feature/amazing-feature`)
3. Commit thay đổi (`git commit -m 'Add some amazing feature'`)
4. Push lên branch (`git push origin feature/amazing-feature`)
5. Tạo Pull Request

## Giấy phép

Distributed under the MIT License. See `LICENSE` for more information.

## Liên hệ

Email - contact@edumentum.com

Project Link: [https://github.com/yourusername/EdumentumBackend](https://github.com/yourusername/EdumentumBackend)
