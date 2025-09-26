-- Create features table
CREATE TABLE features (
    id BIGSERIAL PRIMARY KEY,
    feature_key VARCHAR(100) NOT NULL UNIQUE,
    feature_name VARCHAR(255) NOT NULL,
    description TEXT,
    is_premium BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create plan_configurations table
CREATE TABLE plan_configurations (
    id BIGSERIAL PRIMARY KEY,
    plan_type VARCHAR(50) NOT NULL,
    feature_id BIGINT NOT NULL,
    limit_value INTEGER,
    limit_period VARCHAR(20),
    is_allowed BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (feature_id) REFERENCES features(id),
    UNIQUE(plan_type, feature_id)
);

-- Create usage_tracking table
CREATE TABLE usage_tracking (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    feature_id BIGINT NOT NULL,
    period_start TIMESTAMP,
    period_end TIMESTAMP,
    usage_count INTEGER NOT NULL DEFAULT 0,
    last_used TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (feature_id) REFERENCES features(id),
    UNIQUE(user_id, feature_id, period_start)
);

-- Add indexes for performance
CREATE INDEX idx_features_key ON features(feature_key);
CREATE INDEX idx_plan_configurations_plan ON plan_configurations(plan_type);
CREATE INDEX idx_usage_tracking_user_feature ON usage_tracking(user_id, feature_id);
CREATE INDEX idx_usage_tracking_period ON usage_tracking(period_start);

-- Insert default features
INSERT INTO features (feature_key, feature_name, description, is_premium) VALUES
('CREATE_QUIZ', 'Create Quiz', 'Ability to create quizzes', true),
('CREATE_MINDMAP', 'Create Mindmap', 'Ability to create mindmaps', true),
('UNLIMITED_STORAGE', 'Unlimited Storage', 'Unlimited file storage', true),
('PRIORITY_SUPPORT', 'Priority Support', '24/7 priority customer support', true);

-- Insert default plan configurations for FREE plan
INSERT INTO plan_configurations (plan_type, feature_id, limit_value, limit_period, is_allowed) VALUES
('FREE', 1, 3, 'WEEKLY', true),  -- 3 quizzes per week
('FREE', 2, 5, 'WEEKLY', true),  -- 5 mindmaps per week
('FREE', 3, 100, null, true),    -- 100MB storage total
('FREE', 4, null, null, false);  -- No priority support

-- Insert default plan configurations for PRO_MONTHLY plan
INSERT INTO plan_configurations (plan_type, feature_id, limit_value, limit_period, is_allowed) VALUES
('PRO_MONTHLY', 1, null, null, true),  -- Unlimited quizzes
('PRO_MONTHLY', 2, null, null, true),  -- Unlimited mindmaps
('PRO_MONTHLY', 3, 10000, null, true), -- 10GB storage
('PRO_MONTHLY', 4, null, null, true);  -- Priority support

-- Insert default plan configurations for PRO_YEARLY plan
INSERT INTO plan_configurations (plan_type, feature_id, limit_value, limit_period, is_allowed) VALUES
('PRO_YEARLY', 1, null, null, true),   -- Unlimited quizzes
('PRO_YEARLY', 2, null, null, true),   -- Unlimited mindmaps
('PRO_YEARLY', 3, 10000, null, true),  -- 10GB storage
('PRO_YEARLY', 4, null, null, true);   -- Priority support