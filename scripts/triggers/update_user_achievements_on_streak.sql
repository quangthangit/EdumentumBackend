CREATE OR REPLACE FUNCTION update_user_achievements_on_streak()
RETURNS TRIGGER AS $$
BEGIN
    -- Cập nhật hoặc chèn mới tiến độ theo streak hiện tại
INSERT INTO user_achievements (user_id, achievement_id, current_value, achieved, created_at, updated_at)
SELECT NEW.user_id, a.id, NEW.streak,
       CASE WHEN NEW.streak >= a.target_value THEN TRUE ELSE FALSE END,
       NOW(), NOW()
FROM achievements a
WHERE a.title ILIKE 'Check-in%'

ON CONFLICT (user_id, achievement_id) DO UPDATE
                                             SET current_value = EXCLUDED.current_value,
                                             achieved = EXCLUDED.achieved,
                                             updated_at = NOW();

RETURN NEW;
END;
$$ LANGUAGE plpgsql;
