# Backend API Resources

## 1. Authentication & User Management
#### Author: Quang Thang
##### Done: 90%
| Endpoint                    | Method | Description                | Status   |
|-----------------------------|--------|----------------------------|----------|
| `/api/v1/auth/register`     | POST   | Register new user          | (Done)   |
| `/api/v1/auth/login`        | POST   | Login                      | (Done)   |
| `/api/v1/auth/google`       | POST   | Login with Google          | (Done)   |
| `/api/v1/guest/set-role`    | POST   | Choose role                | (Done)   |
| `/api/v1/auth/logout`       | POST   | Logout                     | (Done)   |
| `/api/v1/users/{id}`        | GET    | Get user info by ID        | (Done)   |
| `/api/v1/users/{id}`        | PUT    | Update user info           | (Done)   |
| `/api/v1/users/{id}/avatar` | POST   | Upload avatar              | (Done)   |

## 2. Group Management
#### Author : Quang Thang
##### Done: 90%
| Method | URL                                  | Description                          | Status     |
|--------|--------------------------------------|--------------------------------------|------------|
| POST   | `/api/v1/user/groups`                | Create new group                     | (Done)     |
| PATCH  | `/api/v1/user/groups/{id}`           | Update group by id                   | (Done)     |
| GET    | `/api/v1/user/groups/{id}`           | Get group details by id              | (Done)     |
| GET    | `/api/v1/user/groups/public`         | Get list of public groups            | (Done)     |
| POST   | `/api/v1/user/groups/{groupId}/join` | Join group                           | (Done)     |
| GET    | `/api/v1/user/groups/my-group`       | Get current user's groups            | (Done)     |
| DELETE | `/api/v1/user/groups/{id}`           | Delete group by id                   | (Done)     |
| POST   | `/api/v1/user/groups/donate-point`   | Donate points to group               | (Done)     |
| POST   | `/api/v1/user/groups/leave`          | Leave group                          | (Done)     |

## 3. Chat / Messaging
#### Author : Quang Thang
##### Done: 70%
| Method  | URL                                                     | Description                     | Status     |
|---------|---------------------------------------------------------|---------------------------------|------------|
| GET     | `/api/chat/rooms`                                       | Get chat room list              | (Done)     |
| GET     | `/api/chat/rooms/{id}`                                  | Get messages in chat room       | (Done)     |
| POST    | `/api/chat/rooms/{id}/send`                             | Send message in chat room       | (Done)     |
| GET     | `/api/v1/student/flashcards?page=0&size=6&sortBy=title` | Send message in chat room       | (Done)     |

Category : FlashCard

| Method   | URL                                                      | Description                      | Status     |
|----------|----------------------------------------------------------|----------------------------------|------------|
| POST     | `/api/v1/student/flashcard-categories`                   | Create category flashcard        | (Done)     |
| PUT      | `/api/v1/student/flashcard-categories/{id}`              | Update category flashcad by ID   | (Done)     |
| GET      | `/api/v1/student/flashcard-categories`                   | Get all categories               | (Done)     |
| GET      | `/api/v1/student/flashcard-categories/{id}`              | Get category by ID               | (Done)     |
| DELETE   | `/api/v1/student/flashcard-categories/{id} `             | Delete category by ID            | (Done)     |
## 4. FlashCard
#### Author : Hung Quan
##### Done: 90%
| Method  | URL                                 | Description                             | Status  |
|---------|-------------------------------------|-----------------------------------------|---------|
| POST    | `/api/v1/student/flashcards`        | Create new flashcard set                | (Done)  |
| GET     | `/api/v1/student/flashcards`        | Get all user's flashcard sets           | (Done)  |
| GET     | `/api/v1/student/flashcards/{id}`   | Get flashcard set details by ID         | (Done)  |
| GET     | `/api/v1/student/flashcards/public` | Get flashcard set with status public    | (Done)  |
| PATCH   | `/api/v1/student/flashcards/{id}`   | Update flashcard set by ID              | (Done)  |
| DELETE  | `/api/v1/student/flashcards/{id}`   | Delete flashcard set by ID              | (Done)  |



To be updated

## 5. Admin
#### Author :
| Method  | URL                               | Description                             | Status      |
|---------|-----------------------------------|-----------------------------------------|-------------|

To be updated

## 6 MindMaps
#### Author : Nhat Hao
| Method    | URL                                          | Description                             | Status  |
|-----------|----------------------------------------------|-----------------------------------------|---------|
| GET       | `/api/v1/student/mindmaps/files`             | Get user's mind map file list           | (Done)  |
| POST      | `/api/v1/student/mindmaps/files`             | Create new mind map file                | (Done)  |
| PUT       | `/api/v1/student/mindmaps/files/{id}`        | Update mind map file by ID              | (Done)  |
| DELETE    | `/api/v1/student/mindmaps/files/{id}`        | Delete mind map file by ID              | (Done)  |
| GET       | `/api/v1/student/mindmaps/files/{id}`        | Get mind map file details by ID         | (Done)  |
| PUT       | `/api/v1/student/mindmaps/files/{id}/name`   | Update mind map file name by ID         | (Done)  |
| POST      | `/api/v1/student/mindmaps`                   | Create new mind map                     | (Done)  |
| GET       | `/api/v1/student/mindmaps/{id}`              | Get mind map by ID                      | (Done)  |
| GET       | `/api/v1/student/mindmaps/user`              | Get all mind maps of current user       | (Done)  |
| PUT       | `/api/v1/student/mindmaps/{id}`              | Update mind map by ID                   | (Done)  |
| DELETE    | `/api/v1/student/mindmaps/{id}`              | Delete mind map by ID                   | (Done)  |

## 7 Task
#### Author : Cong Bien
| Method  | URL                          | Description                        | Status  |
|---------|------------------------------|------------------------------------|---------|
| POST    | `/api/v1/student/tasks`      | Create new Task                    | (Done)  |
| GET     | `/api/v1/student/tasks`      | Get all user's Tasks               | (Done)  |
| GET     | `/api/v1/student/tasks/{id}` | Get Task by ID                     | (Done)  |
| PUT     | `/api/v1/student/tasks/{id}` | Update Task by ID                  | (Done)  |
| DELETE  | `/api/v1/student/tasks/{id}` | Delete Task by ID                  | (Done)  |

## 8. Folder in Group Management
#### Author : Quang Thang
##### Done: 80%
| Method | URL                                                 | Description                              | Status |
|--------|-----------------------------------------------------|------------------------------------------|--------|
| POST   | `/api/v1/user/groups/{groupId}/folders`             | Create a new folder inside a group       | (Done) |
| DELETE | `/api/v1/user/groups/{groupId}/folders/{folderId}/` | Delete a folder by its ID within a group | (Done) |
| GET    | `/api/v1/user/groups/folders/{groupId}`             | Get all folders of a group               | (Done) |
| POST   | `/api/v1/user/groups/folders/upload-file`           | Upload multiple files into a folder      | (Done) |
| DELETE | `/api/v1/user/groups/folders/delete-file/{id}`      | Delete a file by its ID                  | (Done) |
| PATCH  | `/api/v1/user/groups/folders/{folderId}`            | Update current folder's groups           | (Done) |

## 9. ContributionHistory
#### Author : Quang Thang
##### Done: 10%
| Method | URL                                           | Description                             | Status |
|--------|-----------------------------------------------|-----------------------------------------|--------|
| GET    | `/api/v1/user/contribution-history/{groupId}` | Get all contribution history by groupId | (Done) |

## 10. Notification

## 11. Course Browsing & Enrollment (Student)
#### Author : Cong Bien
##### Done: 40%

| Method  | URL                                                                  | Description                              | Status |
|---------|----------------------------------------------------------------------|------------------------------------------|--------|
| GET     | `/api/v1/student/courses/{courseId}`                                 | Get course details by ID (student view)  | (Done) |
| GET     | `/api/v1/student/courses/search`                                     | Search courses                           | (Done) |
| GET     | `/api/v1/student/courses/filter`                                     | Filter courses by level/price            | (Done) |
| GET     | `/api/v1/student/courses/tags`                                       | Get courses by tag(s)                    | (Done) |
| GET     | `/api/v1/student/courses/popular`                                    | Get popular courses                      | (pending) |
| GET     | `/api/v1/student/courses/highly-rated`                               | Get highly rated courses                 | (pending) |
| GET     | `/api/v1/student/courses/free`                                       | Get free courses                         | (Done) |
| POST    | `/api/v1/student/courses/{courseId}/enroll`                          | Enroll in a course                       | (Done) |
| DELETE  | `/api/v1/student/courses/{courseId}/enroll`                          | Unenroll from a course                   | (pending) |
| GET     | `/api/v1/student/courses/student/my-enrollments`                     | Get student's enrollments                | (pending) |
| PATCH   | `/api/v1/student/courses/enrollments/{enrollmentId}/progress`        | Update enrollment progress               | (pending) |
| POST    | `/api/v1/student/courses/{courseId}/ratings`                         | Rate a course                            | (pending) |
| PATCH   | `/api/v1/student/courses/{courseId}/ratings`                         | Update rating                            | (pending) |
| DELETE  | `/api/v1/student/courses/{courseId}/ratings`                         | Delete rating                            | (pending) |
| GET     | `/api/v1/student/courses/{courseId}/ratings`                         | Get course ratings                       | (pending) |
| GET     | `/api/v1/student/courses/{courseId}/ratings/my-rating`               | Get student's own rating for the course  | (pending) |

## 12. Achivement
| Method | URL                             | Description        | Status  |
|--------|---------------------------------|--------------------|---------|
| GET    | `/api/v1/user/achievement       | View Achievement   | (Done)  |
| POST   | `/api/v1/admin/achievement      | Create Achievement | (Done)  |
| PATCH  | `/api/v1/admin/achievement/{id} | Update Achievement | (Done)  |
| DELETE | `/api/v1/admin/achievement/{id} | Delete Achievement | (Done)  |
#### Author : Quang Thang
##### Done: 100%
Còn bổ sung thêm các achievement và các trigger đi theo từng achievement

## 13. Planner

## 14. Payment

## 15. Quizzes
#### Author : Chi Tam
##### Done: 75%
| Method  | URL                                 | Description                             | Status  |
|---------|-------------------------------------|-----------------------------------------|---------|
| POST    | `/api/v1/student/quizzes`           | Create new quizz set                    | (Done)  |
| GET     | `/api/v1/student/quizzes`           | Get all user's quizz                    | (Done)  |
| GET     | `/api/v1/student/quizzes/{id}`      | Get quizz set details by ID             | (Done)  |
| GET     | `/api/v1/student/quizzes/public`    | Get quizz set with status public        | (Done)  |
| PATCH   | `/api/v1/student/quizzes/{id}`      | Update quizz set by ID                  | (Done)  |
| DELETE  | `/api/v1/student/quizzes/{id}`      | Delete quizz set by ID                  | (Done)  |

To be updated

| Endpoint              | Method | Description                                 | Auth    | Status     |
|-----------------------| ------ |---------------------------------------------|---------|------------|
| /                     | POST   | Create new quiz set                         | Bearer  | (Done)     |
| /                     | GET    | Get all quizzes (current user)              | Bearer  | (Done)     |
| /paginated            | GET    | Get all quizzes (paginated + sort)          | Bearer  | (Done)     |
| /search               | GET    | Search quizzes by title (non-paginated)     | Bearer  | (Done)     |
| /search/paginated     | GET    | Search quizzes by title (paginated + sort)  | Bearer  | (Done)     |
| /{id}                 | GET    | Get quiz set details by ID                  | Bearer  | (Done)     |
| /{id}/{slug}          | GET    | Get quiz;                                   | Bearer  | (Done)     |
| /public               | GET    | Get quizzes with status PUBLIC              | Public  | (Done)     |
| /{id}                 | PUT    | Update quiz by ID (replace)                 | Bearer  | (Done)     |
| /{id}                 | PATCH  | Patch update quiz by ID (partial)           | Bearer  | (Done)     |
| /{id}                 | DELETE | Delete quiz by ID                           | Bearer  | (Done)     |
| /{id}/{slug}          | DELETE | Delete quiz by ID with slug validation      | Bearer  | (Done)     |
| /ai/extract           | POST   | Extract question bank from text/file/url    | Bearer  | ()         |
| /ai/generate          | POST   | Generate quiz from content + params         | Bearer  | ()         |

## 16. Pomodoro

## 17. Collections

## 18. Materials

## 19. Profile
#### Author : Quang Thang
##### Done: 55%
| Method  | URL                                      | Description                                | Status  |
|---------|------------------------------------------|--------------------------------------------|---------|
| GET     | `/api/v1/user/profile`                   | Get user profile info                      | (Done)  |
| GET     | `/api/v1/user/profile/attendance`        | Get user attendance profile                | (Done)  |
| POST    | `/api/v1/user/profile`                   | Update user profile                        | (Done)  |

## 20. Study Time
#### Author : Quang Thang
##### Done: 100%
| Method   | URL                                       | Description                                 | Status  |
|----------|-------------------------------------------|---------------------------------------------|---------|
| POST     | `/api/v1/user/study-time`                 | Increase study time                         | (Done)  |
| GET      | `/api/v1/user/study-time`                 | Get study time matrix (7d)                  | (Done)  |

## 21. Attandance
#### Author : Quang Thang
##### Done: 100%
| Method   | URL                                       | Description                                | Status  |
|----------|-------------------------------------------|--------------------------------------------|---------|
| POST     | `/api/v1/user/attendance`                 | User attendance check-in                   | (Done)  |

## 22. Course Management (Teacher)
#### Author : Cong Bien
##### Done: 50%

| Method  | URL                                                           | Description                                | Status  |
|---------|---------------------------------------------------------------|--------------------------------------------|---------|
| POST    | `/api/v1/teacher/courses`                                     | Create new course                          | (Done) |
| PATCH   | `/api/v1/teacher/courses/{courseId}`                          | Update course by ID                        | (Done) |
| GET     | `/api/v1/teacher/courses/{courseId}`                          | Get teacher's course details               | (Done) |
| DELETE  | `/api/v1/teacher/courses/{courseId}`                          | Delete course                              | (Done) |
| PATCH   | `/api/v1/teacher/courses/{courseId}/publish`                  | Publish course                             | (Done) |
| PATCH   | `/api/v1/teacher/courses/{courseId}/archive`                  | Archive course                             | (Done) |
| GET     | `/api/v1/teacher/courses/my-courses`                          | Get teacher's courses (with status filter) | (Done) |
| GET     | `/api/v1/teacher/courses/published`                           | Get all published courses                  | (Done) |
| GET     | `/api/v1/teacher/courses/search`                              | Search courses                             | (Done) |
| GET     | `/api/v1/teacher/courses/filter`                              | Filter courses by level/price              | (Done) |
| GET     | `/api/v1/teacher/courses/tags`                                | Get courses by tag(s)                      | (Done) |
| GET     | `/api/v1/teacher/courses/popular`                             | Get popular courses                        | (pending) |
| GET     | `/api/v1/teacher/courses/highly-rated`                        | Get highly rated courses                   | (pending) |
| GET     | `/api/v1/teacher/courses/free`                                | Get free courses                           | (Done) |
| POST    | `/api/v1/teacher/courses/{courseId}/lessons`                  | Create lesson                              | (Done) |
| PATCH   | `/api/v1/teacher/courses/lessons/{lessonId}`                  | Update lesson                              | (Done) |
| DELETE  | `/api/v1/teacher/courses/lessons/{lessonId}`                  | Delete lesson                              | (Done) |
| GET     | `/api/v1/teacher/courses/{courseId}/lessons`                  | Get lessons of a course                    | (Done) |
| POST    | `/api/v1/teacher/courses/{courseId}/exercises`                | Create exercise                            | (Done) |
| PATCH   | `/api/v1/teacher/courses/exercises/{exerciseId}`              | Update exercise                            | (Done) |
| DELETE  | `/api/v1/teacher/courses/exercises/{exerciseId}`              | Delete exercise                            | (Done) |
| GET     | `/api/v1/teacher/courses/{courseId}/exercises`                | Get exercises of a course                  | (pending) |
| POST    | `/api/v1/teacher/courses/{courseId}/resources`                | Create resource                            | (pending) |
| PATCH   | `/api/v1/teacher/courses/resources/{resourceId}`              | Update resource                            | (pending) |
| DELETE  | `/api/v1/teacher/courses/resources/{resourceId}`              | Delete resource                            | (pending) |
| GET     | `/api/v1/teacher/courses/{courseId}/resources`                | Get resources of a course                  | (pending) |
| GET     | `/api/v1/teacher/courses/{courseId}/enrollments`              | Get enrollments of a course                | (pending) |
| PATCH   | `/api/v1/teacher/courses/enrollments/{enrollmentId}/progress` | Update enrollment progress                 | (pending) |
| GET     | `/api/v1/teacher/courses/{courseId}/ratings`                  | Get ratings of a course    
