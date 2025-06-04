-- Create enum types
CREATE TYPE expense_category AS ENUM (
    'HOUSING', 'TRANSPORTATION', 'FOOD', 'UTILITIES', 'HEALTHCARE',
    'ENTERTAINMENT', 'SHOPPING', 'EDUCATION', 'SAVINGS', 'DEBT',
    'INSURANCE', 'PERSONAL_CARE', 'TRAVEL', 'GIFTS', 'OTHER'
);

CREATE TYPE goal_category AS ENUM (
    'EMERGENCY_FUND', 'RETIREMENT', 'HOUSE_DOWN_PAYMENT', 'EDUCATION',
    'VACATION', 'DEBT_PAYOFF', 'INVESTMENT', 'MAJOR_PURCHASE', 'OTHER'
);

CREATE TYPE goal_status AS ENUM (
    'IN_PROGRESS', 'COMPLETED', 'EXPIRED'
);

CREATE TYPE income_type AS ENUM (
    'SALARY', 'BONUS', 'INVESTMENT', 'RENTAL', 'FREELANCE', 'OTHER'
);

CREATE TYPE reminder_frequency AS ENUM (
    'DAILY', 'WEEKLY', 'MONTHLY', 'NEVER'
);

CREATE TYPE difficulty_level AS ENUM (
    'EASY', 'MODERATE', 'CHALLENGING'
);

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    is_premium BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Create expenses table
CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    category expense_category NOT NULL,
    date DATE NOT NULL,
    is_recurring BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Create incomes table
CREATE TABLE incomes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    source VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    date DATE NOT NULL,
    type income_type NOT NULL DEFAULT 'SALARY',
    is_recurring BOOLEAN DEFAULT FALSE,
    description TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Create budgets table
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category expense_category NOT NULL,
    month_year DATE NOT NULL,
    monthly_limit DECIMAL(19,2) NOT NULL,
    current_spent DECIMAL(19,2) NOT NULL DEFAULT 0,
    alert_threshold INTEGER DEFAULT 80,
    is_threshold_reached BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT,
    UNIQUE(user_id, category, month_year)
);

-- Create goal_templates table
CREATE TABLE goal_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category goal_category NOT NULL,
    description TEXT,
    suggested_duration_months INTEGER,
    typical_amount DECIMAL(19,2),
    is_premium BOOLEAN NOT NULL DEFAULT FALSE,
    tips TEXT,
    success_rate DOUBLE PRECISION,
    difficulty_level difficulty_level,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Create goals table
CREATE TABLE goals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    target_amount DECIMAL(19,2) NOT NULL,
    target_date DATE NOT NULL,
    current_saved DECIMAL(19,2) NOT NULL DEFAULT 0,
    completion_percentage DOUBLE PRECISION DEFAULT 0,
    status goal_status NOT NULL DEFAULT 'IN_PROGRESS',
    description TEXT,
    category goal_category NOT NULL,
    template_id BIGINT REFERENCES goal_templates(id),
    reminder_frequency reminder_frequency DEFAULT 'WEEKLY',
    auto_adjust_enabled BOOLEAN DEFAULT FALSE,
    priority_level INTEGER DEFAULT 1,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Create goal_milestones table
CREATE TABLE goal_milestones (
    id BIGSERIAL PRIMARY KEY,
    goal_id BIGINT NOT NULL REFERENCES goals(id),
    title VARCHAR(255) NOT NULL,
    target_amount DECIMAL(19,2) NOT NULL,
    target_date DATE NOT NULL,
    completion_percentage DOUBLE PRECISION DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT
);

-- Create charts table (dummy table for JPA requirements)
CREATE TABLE charts (
    id BIGSERIAL PRIMARY KEY
);

-- Create indexes
CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_expenses_date ON expenses(date);
CREATE INDEX idx_expenses_category ON expenses(category);
CREATE INDEX idx_expenses_user_date ON expenses(user_id, date);

CREATE INDEX idx_incomes_user_id ON incomes(user_id);
CREATE INDEX idx_incomes_date ON incomes(date);
CREATE INDEX idx_incomes_type ON incomes(type);
CREATE INDEX idx_incomes_user_date ON incomes(user_id, date);

CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budgets_month_year ON budgets(month_year);
CREATE INDEX idx_budgets_user_month ON budgets(user_id, month_year);

CREATE INDEX idx_goals_user_id ON goals(user_id);
CREATE INDEX idx_goals_status ON goals(status);
CREATE INDEX idx_goals_target_date ON goals(target_date);
CREATE INDEX idx_goals_category ON goals(category);

CREATE INDEX idx_goal_milestones_goal_id ON goal_milestones(goal_id);
CREATE INDEX idx_goal_milestones_target_date ON goal_milestones(target_date);

-- Create functions
CREATE OR REPLACE FUNCTION update_goal_status()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.current_saved >= NEW.target_amount THEN
        NEW.status := 'COMPLETED';
    ELSIF CURRENT_DATE > NEW.target_date THEN
        NEW.status := 'EXPIRED';
    ELSE
        NEW.status := 'IN_PROGRESS';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create triggers
CREATE TRIGGER update_goal_status_trigger
    BEFORE INSERT OR UPDATE ON goals
    FOR EACH ROW
    EXECUTE FUNCTION update_goal_status(); 