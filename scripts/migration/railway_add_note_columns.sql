-- Migration script for Railway database
-- Add type and content columns to notes table
-- Date: 2025-09-16

-- Check if columns exist before adding them
DO $$
BEGIN
    -- Add type column if it doesn't exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'notes' AND column_name = 'type'
    ) THEN
        ALTER TABLE notes ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'block';

        -- Add check constraint for type
        ALTER TABLE notes ADD CONSTRAINT check_note_type
        CHECK (type IN ('markdown', 'block'));

        RAISE NOTICE 'Added type column to notes table';
    ELSE
        RAISE NOTICE 'Type column already exists in notes table';
    END IF;

    -- Add content column if it doesn't exist
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'notes' AND column_name = 'content'
    ) THEN
        ALTER TABLE notes ADD COLUMN content TEXT;
        RAISE NOTICE 'Added content column to notes table';
    ELSE
        RAISE NOTICE 'Content column already exists in notes table';
    END IF;
END$$;
