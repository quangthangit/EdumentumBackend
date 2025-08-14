CREATE OR REPLACE FUNCTION update_group_tier()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.contribution_points >= 2000 THEN
        NEW.tier := 'DIAMOND';
    ELSIF NEW.contribution_points >= 1000 THEN
        NEW.tier := 'PLATINUM';
    ELSIF NEW.contribution_points >= 500 THEN
        NEW.tier := 'GOLD';
    ELSIF NEW.contribution_points >= 100 THEN
        NEW.tier := 'SILVER';
ELSE
        NEW.tier := 'BRONZE';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_update_group_tier ON groups;

CREATE TRIGGER trg_update_group_tier
    BEFORE UPDATE ON groups
    FOR EACH ROW
    EXECUTE FUNCTION update_group_tier();
