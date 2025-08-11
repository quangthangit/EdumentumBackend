# Backend API Resources

## 1. Authentication & User Management

| Endpoint                    | Method | Mô tả                     | Trạng thái |
|-----------------------------|--------|---------------------------|------------|
| `/api/v1/auth/register`     | POST   | Đăng ký user mới          | (Done)     |
| `/api/v1/auth/login`        | POST   | Đăng nhập                 | (Done)     |
| `/api/v1/auth/google`       | POST   | Đăng nhập bằng googole    | (Done)     |
| `/api/v1/guest/set-role`    | POST   | Chọn role                 | (Done)     |
| `/api/v1/auth/logout`       | POST   | Đăng xuất                 | Chưa làm   |
| `/api/v1/users/{id}`        | GET    | Lấy thông tin user theo ID | Chưa làm   |
| `/api/v1/users/{id}`        | PUT    | Cập nhật thông tin user   | Chưa làm   |
| `/api/v1/users/{id}/avatar` | POST   | Upload ảnh đại diện       | Chưa làm   |

Còn Update thêm

## 2. Group Management

| Method | URL                                     | Mô tả                                     | Request Body / Params                             | Trạng thái |
|--------|-----------------------------------------|-------------------------------------------|---------------------------------------------------|------------|
| POST   | `/api/v1/student/groups`                | Tạo nhóm mới                              | JSON: GroupRequestDto                             | (Done)     |
| PATCH  | `/api/v1/student/groups/{id}`           | Cập nhật nhóm theo id                     | Path: id, JSON: GroupRequestDto                   | (Done)     |
| GET    | `/api/v1/student/groups/{id}`           | Lấy chi tiết nhóm theo id                 | Path: id                                          | (Done)     |
| GET    | `/api/v1/student/groups/public`         | Lấy danh sách nhóm công khai (phân trang) | Query params: page (default=0), size (default=12) | (Done) |
| POST   | `/api/v1/student/groups/{groupId}/join` | Tham gia nhóm                             | Path: groupId                                     | (Done)     |
| GET    | `/api/v1/student/groups/my-group`       | Lấy danh sách nhóm của user hiện tại      | -                                                 | (Done)     |

Còn Update thêm

## 3. Chat / Messaging

| Endpoint                    | Method  | Mô tả                          | Trạng thái |
|-----------------------------|---------|--------------------------------|------------|
| `/api/chat/rooms`           | GET     | Lấy danh sách phòng chat       | (Done)     |
| `/api/chat/rooms/{id}`      | GET     | Lấy tin nhắn trong phòng chat  | (Done)     |
| `/api/chat/rooms/{id}/send` | POST    | Gửi tin nhắn trong phòng chat  | (Done)     |

Còn Update thêm

## 4. FlashCard

| Method  | URL                               | Mô tả                                   | Request Body / Params                  | Trạng thái  |
|---------|-----------------------------------|-----------------------------------------|----------------------------------------|-------------|
| POST    | `/api/v1/student/flashcards`      | Tạo bộ flashcard mới                    | JSON: FlashcardSetRequestDto           | (Done)      |
| GET     | `/api/v1/student/flashcards`      | Lấy tất cả bộ flashcard của user        | -                                      | (Done)      |
| GET     | `/api/v1/student/flashcards/{id}` | Lấy chi tiết bộ flashcard theo ID       | Path: id                               | (Done)      |
| PATCH   | `/api/v1/student/flashcards/{id}` | Cập nhật bộ flashcard theo ID           | Path: id, JSON: FlashcardSetRequestDto | (Done)      |
| DELETE  | `/api/v1/student/flashcards/{id}` | Xóa bộ flashcard theo ID                | Path: id                               | (Done)      |

Còn Update thêm

## 5. Admin

| Method  | URL                               | Mô tả                                   | Request Body / Params                  | Trạng thái  |
|---------|-----------------------------------|-----------------------------------------|----------------------------------------|-------------|

Còn Update thêm

## 6 MindMaps

| Method    | URL                                          | Mô tả                                   | Request Body / Params                 | Trạng thái  |
|-----------|----------------------------------------------|-----------------------------------------|---------------------------------------|-------------|
| GET       | `/api/v1/student/mindmaps/files`             | Lấy danh sách file mind map của user    | -                                     | (Done)      |
| POST      | `/api/v1/student/mindmaps/files`             | Tạo mới file mind map                   | JSON: MindMapFileRequestDto           | (Done)      |
| PUT       | `/api/v1/student/mindmaps/files/{id}`        | Cập nhật file mind map theo ID          | Path: id, JSON: MindMapFileRequestDto | (Done)      |
| DELETE    | `/api/v1/student/mindmaps/files/{id}`        | Xóa file mind map theo ID               | Path: id                              | (Done)      |
| GET       | `/api/v1/student/mindmaps/files/{id}`        | Lấy chi tiết file mind map theo ID      | Path: id                              | (Done)      |
| PUT       | `/api/v1/student/mindmaps/files/{id}/name`   | Cập nhật tên file mind map theo ID      | Path: id, JSON: { "name": "string" }  | (Done)      |
| POST      | `/api/v1/student/mindmaps`                   | Tạo mới mind map                        | JSON: MindMapRequestDto               | (Done)      |
| GET       | `/api/v1/student/mindmaps/{id}`              | Lấy mind map theo ID                    | Path: id                              | (Done)      |
| GET       | `/api/v1/student/mindmaps/user`              | Lấy tất cả mind map của user hiện tại   | -                                     | (Done)      |
| PUT       | `/api/v1/student/mindmaps/{id}`              | Cập nhật mind map theo ID               | Path: id, JSON: MindMapRequestDto     | (Done)      |
| DELETE    | `/api/v1/student/mindmaps/{id}`              | Xóa mind map theo ID                    | Path: id                              | (Done)      |

## 7 Task

| Method  | URL                          | Mô tả                              | Request Body / Params           | Trạng thái |
|---------|------------------------------|------------------------------------|---------------------------------|------------|
| POST    | `/api/v1/student/tasks`      | Tạo Task mới                       | JSON: TaskRequestDto            | (Done)     |
| GET     | `/api/v1/student/tasks`      | Lấy tất cả Task của user           | -                               | (Done)     |
| GET     | `/api/v1/student/tasks/{id}` | Lấy Task theo ID                   | Path: id                        | (Done)     |
| PUT     | `/api/v1/student/tasks/{id}` | Cập nhật Task theo ID              | Path: id, JSON: TaskRequestDto  | (Done)     |
| DELETE  | `/api/v1/student/tasks/{id}` | Xóa Task theo ID                   | Path: id                        | (Done)     |

## Hoàn thiện 25%
