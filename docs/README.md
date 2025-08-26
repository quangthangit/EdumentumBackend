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
| Method | URL                          | Description                     | Status     |
|--------|------------------------------|---------------------------------|------------|
| GET    | `/api/chat/rooms`            | Get chat room list              | (Done)     |
| GET    | `/api/chat/rooms/{id}`       | Get messages in chat room       | (Done)     |
| POST   | `/api/chat/rooms/{id}/send`  | Send message in chat room       | (Done)     |

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

## 11. Course Management (Teacher)

## 12. Achivement

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

## 16. Pomodoro

## 17. Collections

## 18. Materials