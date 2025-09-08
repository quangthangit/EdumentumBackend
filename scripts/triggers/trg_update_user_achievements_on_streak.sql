DROP TRIGGER IF EXISTS trg_update_user_achievements_on_streak ON user_profiles;

CREATE TRIGGER trg_update_user_achievements_on_streak
AFTER UPDATE OF streak ON user_profiles
FOR EACH ROW
WHEN (OLD.streak IS DISTINCT FROM NEW.streak)
EXECUTE FUNCTION update_user_achievements_on_streak();
