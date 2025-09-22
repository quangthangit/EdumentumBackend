-- Migration: Add type and content columns to notes table
-- Date: 2025-09-16

ALTER TABLE notes
ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'block',
ADD COLUMN content TEXT;

-- Add check constraint for type
ALTER TABLE notes
ADD CONSTRAINT check_note_type
CHECK (type IN ('markdown', 'block'));

-- Update existing notes to have default type
UPDATE notes SET type = 'block' WHERE type IS NULL;
