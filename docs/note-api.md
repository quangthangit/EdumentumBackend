## Notes Module API (Chi tiết)

### Tổng quan
- Block-based note (giống Notion), cộng tác realtime, version history, comment, tagging, search, permission.
- Auth: JWT (header `Authorization: Bearer <JWT>`).
- Realtime: STOMP WebSocket qua `/ws-chat`, app prefix `/app`, broker `/topic`.

### Schema (PostgreSQL)
- `users` (có sẵn): `user_id`, `email`, `username`, ...
- `notes`: `id`, `title`, `owner_id`, `is_deleted`, `created_at`, `updated_at`.
- `note_blocks`: `id`, `note_id`, `type` (enum string `BlockType`), `content` (JSONB), `order_index`, `is_deleted`, timestamps.
- `note_collaborators`: `id`, `note_id`, `user_id`, `permission` (`NotePermission`), unique(note_id,user_id), timestamps.
- `note_comments`: `id`, `note_id`, `block_id?`, `user_id`, `content`, `parent_id?`, `is_deleted`, timestamps.
- `tags`: `id`, `name` (unique), timestamps.
- `note_tags`: `id`, `note_id`, `tag_id`, unique(note_id,tag_id), timestamps.
- `note_versions`: `id`, `note_id`, `user_id`, `action` (`NoteAction`), `diff` (JSONB), timestamps.

### Enum
- `NotePermission`: OWNER | EDITOR | VIEWER
- `NoteAction`: CREATE_NOTE | UPDATE_NOTE | DELETE_NOTE | CREATE_BLOCK | UPDATE_BLOCK | DELETE_BLOCK | REORDER_BLOCKS | ADD_COLLABORATOR | REMOVE_COLLABORATOR | ADD_COMMENT | DELETE_COMMENT
- `BlockType` (enum string):
  - Text & Heading: PARAGRAPH, HEADING_1, HEADING_2, HEADING_3
  - List & Todo: BULLETED_LIST_ITEM, NUMBERED_LIST_ITEM, TO_DO, TOGGLE
  - Formatting: QUOTE, CALLOUT, DIVIDER, CODE
  - Media & Data: TABLE, TABLE_ROW, IMAGE, VIDEO, FILE, BOOKMARK, EMBED
  - Advanced: PAGE, DATABASE_TABLE, DATABASE_BOARD, DATABASE_CALENDAR, DATABASE_GALLERY
  - Inline: EQUATION, MENTION_USER, MENTION_PAGE, MENTION_DATE
- `InlineFormat`: BOLD | ITALIC | UNDERLINE | STRIKETHROUGH | CODE | LINK | MENTION | HIGHLIGHT

### DTO
- NoteRequestDto: `{ title: string, tags?: string[] }`
- NoteResponseDto: `{ id, title, ownerId, isDeleted, blocks: BlockResponseDto[], tags: string[] }`
- BlockRequestDto: `{ type: BlockType, orderIndex: number, content: JsonNode }`
- BlockResponseDto: `{ id, type: BlockType, orderIndex, content: JsonNode }`
- CollaboratorRequestDto: `{ userId: number, permission: NotePermission }`
- CollaboratorResponseDto: `{ userId: number, permission: NotePermission }`
- CommentRequestDto: `{ content: string, blockId?: number, parentId?: number }`
- CommentResponseDto: `{ id, userId, blockId, content }`
- ReorderBlocksRequestDto: `{ noteId: number, orderedBlockIds: number[] }`

### Quyền (Permission)
- Owner: toàn quyền (xóa note, share note, xem collaborators).
- Editor: thêm/sửa/xóa block, reorder, comment, xem note.
- Viewer: xem note, xem comment, thêm comment (nếu hệ thống cho phép; hiện cho phép).
- GET collaborators chỉ dành cho Owner; GET comments dành cho Owner hoặc Collaborator (OWNER/EDITOR/VIEWER).

### REST API

#### Auth
- POST `/api/v1/auth/register` body `{ email, password, username }` → trả `user`, `accessToken`, `refreshToken`
- POST `/api/v1/auth/login` body `{ email, password }` → trả `user`, `accessToken`, `refreshToken`
- POST `/api/v1/user/assign-role?userId=&roleName=` (yêu cầu JWT phù hợp) → 200

#### Notes
- GET `/api/v1/notes` → `PaginatedResponse<NoteResponseDto>`
  - Query: `page`, `size`, `query?`, `ownerId?`, `tag?`
- GET `/api/v1/notes/{id}` → `NoteResponseDto`
- POST `/api/v1/notes` (NoteRequestDto) → `NoteResponseDto`
- PUT `/api/v1/notes/{id}` (NoteRequestDto) → `NoteResponseDto`
- DELETE `/api/v1/notes/{id}` → 204 (soft delete)

Ví dụ tạo note:
```json
{
  "title": "Science Notes",
  "tags": ["physics", "experiment"]
}
```

#### Blocks
- POST `/api/v1/notes/{id}/blocks` (BlockRequestDto) → `BlockResponseDto`
- PUT `/api/v1/notes/blocks/{blockId}` (BlockRequestDto) → `BlockResponseDto`
- DELETE `/api/v1/notes/blocks/{blockId}` → 204 (soft delete)
- PATCH `/api/v1/notes/blocks/reorder` (ReorderBlocksRequestDto) → 204

Ví dụ BlockRequestDto:
```json
{
  "type": "PARAGRAPH",
  "orderIndex": 0,
  "content": { "text": "Hello world" }
}
```

Ví dụ content theo BlockType (gợi ý FE):
- PARAGRAPH: `{ "text": string, "marks"?: InlineFormat[] }`
- HEADING_1..3: `{ "text": string }`
- TO_DO: `{ "checked": boolean, "text": string }`
- CODE: `{ "language": "java", "code": "System.out.println(1);" }`
- IMAGE/FILE/VIDEO: `{ "url": "https://...", "name"?: string, "size"?: number }`
- TABLE: `{ "columns": ["A","B"], "rows": [["a1","b1"],["a2","b2"]] }`

#### Collaborators
- GET `/api/v1/notes/{id}/collaborators` → `CollaboratorResponseDto[]` (Owner-only)
- POST `/api/v1/notes/{id}/collaborators` (CollaboratorRequestDto) → `CollaboratorResponseDto`
- DELETE `/api/v1/notes/{id}/collaborators/{userId}` → 204

#### Comments
- GET `/api/v1/notes/{id}/comments` → danh sách comment (chưa xóa) theo thời gian
- POST `/api/v1/notes/{id}/comments` (CommentRequestDto) → `CommentResponseDto`
- DELETE `/api/v1/notes/comments/{commentId}` → 204 (soft delete)

#### Tags
- GET `/api/v1/tags` → `TagEntity[]`
- POST `/api/v1/tags?name=...` → `TagEntity`
- PUT `/api/v1/tags/{id}?name=...` → `TagEntity` (rename)
- DELETE `/api/v1/tags/{id}` → 204

#### Search
- GET `/api/v1/search/notes?query=&tag=&ownerId=&page=&size=` → `PaginatedResponse<NoteResponseDto>`

#### Version History
- GET `/api/v1/notes/{id}/history` → `NoteVersionEntity[]`
- POST `/api/v1/notes/{id}/restore/{versionId}` → 204 (stub; cần CRDT/OT để apply diff)

`note_versions.diff` ví dụ:
```json
{ "blockId": 123 }
```

### Realtime (STOMP)
- Connect: `ws://{host}/ws-chat` (SockJS supported)
- Subscribe: `/topic/note/{noteId}`
- Publish: `/app/note.events` với payload:
```json
{
  "event": "BLOCK_UPDATED",
  "noteId": 1,
  "blockId": 10,
  "payload": "{\"order\":1}"
}
```

Gợi ý: FE sau khi REST update thành công nên phát event để đồng bộ client khác.

### Lỗi phổ biến
- 401: thiếu/ JWT sai.
- 403: không đủ quyền (ví dụ xem collaborators khi không phải Owner).
- 404: note/block/comment không tồn tại.
- 400: payload sai định dạng (ví dụ diff JSON không hợp lệ).

### Gợi ý mở rộng
- Postgres tsvector + GIN cho search nâng cao.
- CRDT/OT cho merge xung đột realtime và restore version chính xác.
- Storage S3/GCS cho IMAGE/FILE/VIDEO, `content` lưu URL/metadata.


