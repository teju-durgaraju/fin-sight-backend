-- Drop triggers
DROP TRIGGER IF EXISTS update_goal_status_trigger ON goals;

-- Drop functions
DROP FUNCTION IF EXISTS update_goal_status();

-- Drop indexes
DROP INDEX IF EXISTS idx_expenses_user_id;
DROP INDEX IF EXISTS idx_expenses_date;
DROP INDEX IF EXISTS idx_expenses_category;
DROP INDEX IF EXISTS idx_incomes_user_id;
DROP INDEX IF EXISTS idx_incomes_date;
DROP INDEX IF EXISTS idx_incomes_type;
DROP INDEX IF EXISTS idx_budgets_user_id;
DROP INDEX IF EXISTS idx_budgets_month_year;
DROP INDEX IF EXISTS idx_goals_user_id;
DROP INDEX IF EXISTS idx_goals_status;
DROP INDEX IF EXISTS idx_goals_target_date;
DROP INDEX IF EXISTS idx_goal_milestones_goal_id;

-- Drop tables
DROP TABLE IF EXISTS charts;
DROP TABLE IF EXISTS goal_milestones;
DROP TABLE IF EXISTS goals;
DROP TABLE IF EXISTS goal_templates;
DROP TABLE IF EXISTS budgets;
DROP TABLE IF EXISTS incomes;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS users;

-- Drop enum types
DROP TYPE IF EXISTS expense_category;
DROP TYPE IF EXISTS goal_category;
DROP TYPE IF EXISTS goal_status;
DROP TYPE IF EXISTS income_type;
DROP TYPE IF EXISTS reminder_frequency;
DROP TYPE IF EXISTS difficulty_level; 