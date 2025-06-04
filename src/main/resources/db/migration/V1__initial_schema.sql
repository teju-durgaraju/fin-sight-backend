-- Create enum types
CREATE TYPE expense_category AS ENUM (
    'HOUSING', 'TRANSPORTATION', 'FOOD', 'UTILITIES', 'HEALTHCARE',
    'ENTERTAINMENT', 'SHOPPING', 'EDUCATION', 'SAVINGS', 'DEBT',
    'INSURANCE', 'PERSONAL_CARE', 'TRAVEL', 'GIFTS', 'OTHER'
);

CREATE TYPE goal_category AS ENUM (
    'EMERGENCY_FUND', 'RETIREMENT', 'HOUSE_DOWN_PAYMENT', 'DEBT_PAYOFF',
    'VACATION', 'INVESTMENT', 'MAJOR_PURCHASE', 'EDUCATION', 'OTHER'
);

CREATE TYPE goal_status AS ENUM (
    'NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'OVERDUE'
);

CREATE TYPE income_type AS ENUM (
    'SALARY', 'BONUS', 'INVESTMENT', 'RENTAL', 'FREELANCE', 'OTHER'
);

CREATE TYPE reminder_frequency AS ENUM (
    'DAILY', 'WEEKLY', 'MONTHLY', 'QUARTERLY', 'YEARLY'
);

CREATE TYPE difficulty_level AS ENUM (
    'EASY', 'MODERATE', 'CHALLENGING'
);

-- Create tables
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_premium BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE expenses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    category expense_category NOT NULL,
    date DATE NOT NULL,
    is_recurring BOOLEAN DEFAULT false,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE incomes (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    source VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    date DATE NOT NULL,
    type income_type NOT NULL,
    is_recurring BOOLEAN DEFAULT false,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category expense_category NOT NULL,
    month_year DATE NOT NULL,
    monthly_limit DECIMAL(19,2) NOT NULL,
    current_spent DECIMAL(19,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0,
    UNIQUE(user_id, category, month_year)
);

CREATE TABLE goal_templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category goal_category NOT NULL,
    description TEXT,
    suggested_duration_months INTEGER NOT NULL,
    typical_amount DECIMAL(19,2) NOT NULL,
    is_premium BOOLEAN DEFAULT false,
    tips TEXT,
    success_rate DECIMAL(3,2),
    difficulty_level difficulty_level NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE goals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    template_id BIGINT REFERENCES goal_templates(id),
    name VARCHAR(255) NOT NULL,
    category goal_category NOT NULL,
    target_amount DECIMAL(19,2) NOT NULL,
    target_date DATE NOT NULL,
    current_saved DECIMAL(19,2) NOT NULL DEFAULT 0,
    status goal_status NOT NULL DEFAULT 'NOT_STARTED',
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE goal_milestones (
    id BIGSERIAL PRIMARY KEY,
    goal_id BIGINT NOT NULL REFERENCES goals(id),
    name VARCHAR(255) NOT NULL,
    target_amount DECIMAL(19,2) NOT NULL,
    target_date DATE NOT NULL,
    is_completed BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

-- Create dummy charts table for JPA requirements
CREATE TABLE charts (
    id BIGSERIAL PRIMARY KEY
);

-- Create indexes
CREATE INDEX idx_expenses_user_id ON expenses(user_id);
CREATE INDEX idx_expenses_date ON expenses(date);
CREATE INDEX idx_expenses_category ON expenses(category);
CREATE INDEX idx_incomes_user_id ON incomes(user_id);
CREATE INDEX idx_incomes_date ON incomes(date);
CREATE INDEX idx_incomes_type ON incomes(type);
CREATE INDEX idx_budgets_user_id ON budgets(user_id);
CREATE INDEX idx_budgets_month_year ON budgets(month_year);
CREATE INDEX idx_goals_user_id ON goals(user_id);
CREATE INDEX idx_goals_status ON goals(status);
CREATE INDEX idx_goals_target_date ON goals(target_date);
CREATE INDEX idx_goal_milestones_goal_id ON goal_milestones(goal_id);

-- Create function to update goal status
CREATE OR REPLACE FUNCTION update_goal_status()
RETURNS TRIGGER AS $$
BEGIN
    -- Update status based on current saved amount and target date
    IF NEW.current_saved >= NEW.target_amount THEN
        NEW.status := 'COMPLETED';
    ELSIF NEW.target_date < CURRENT_DATE THEN
        NEW.status := 'OVERDUE';
    ELSIF NEW.current_saved > 0 THEN
        NEW.status := 'IN_PROGRESS';
    ELSE
        NEW.status := 'NOT_STARTED';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create trigger for goal status updates
CREATE TRIGGER update_goal_status_trigger
    BEFORE INSERT OR UPDATE ON goals
    FOR EACH ROW
    EXECUTE FUNCTION update_goal_status(); 