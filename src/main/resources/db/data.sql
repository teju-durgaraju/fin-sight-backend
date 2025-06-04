-- Insert sample goal templates
INSERT INTO goal_templates (name, category, description, suggested_duration_months, typical_amount, is_premium, tips, success_rate, difficulty_level, created_at, updated_at)
VALUES 
    ('Emergency Fund Builder', 'EMERGENCY_FUND', 'Build a 6-month emergency fund', 12, 15000.00, false, 
     'Start with 1 month of expenses, then gradually increase to 6 months', 0.85, 'EASY', 
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    ('Retirement Starter', 'RETIREMENT', 'Begin your retirement savings journey', 24, 50000.00, false,
     'Start with 15% of your income, increase by 1% each year', 0.75, 'MODERATE',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    ('House Down Payment', 'HOUSE_DOWN_PAYMENT', 'Save for a 20% down payment on a house', 36, 60000.00, false,
     'Consider high-yield savings accounts for better returns', 0.65, 'CHALLENGING',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    ('Student Loan Payoff', 'DEBT_PAYOFF', 'Accelerate your student loan repayment', 24, 30000.00, false,
     'Use the debt snowball or avalanche method', 0.80, 'MODERATE',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    ('Vacation Fund', 'VACATION', 'Save for your dream vacation', 12, 5000.00, false,
     'Set up automatic transfers to a dedicated savings account', 0.90, 'EASY',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    ('Investment Portfolio', 'INVESTMENT', 'Build a diversified investment portfolio', 24, 25000.00, true,
     'Start with index funds and gradually diversify', 0.70, 'MODERATE',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    ('Car Purchase', 'MAJOR_PURCHASE', 'Save for a new car purchase', 24, 20000.00, false,
     'Consider both new and used car options', 0.85, 'EASY',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    ('Education Fund', 'EDUCATION', 'Save for higher education', 48, 50000.00, true,
     'Consider 529 plans for tax advantages', 0.75, 'CHALLENGING',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    ('Wedding Fund', 'MAJOR_PURCHASE', 'Save for your dream wedding', 18, 30000.00, false,
     'Create a detailed budget and prioritize expenses', 0.80, 'MODERATE',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    
    ('Business Startup', 'INVESTMENT', 'Save for starting your own business', 36, 50000.00, true,
     'Research your market and create a business plan', 0.60, 'CHALLENGING',
     CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample expense categories with descriptions
INSERT INTO expense_categories (name, description, created_at, updated_at)
VALUES 
    ('HOUSING', 'Rent, mortgage, property taxes, home insurance, maintenance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TRANSPORTATION', 'Car payments, fuel, public transit, maintenance, insurance', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('FOOD', 'Groceries, dining out, takeout, snacks', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('UTILITIES', 'Electricity, water, gas, internet, phone, cable', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('HEALTHCARE', 'Medical insurance, prescriptions, doctor visits, dental', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ENTERTAINMENT', 'Movies, streaming services, hobbies, sports', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SHOPPING', 'Clothing, personal items, household goods', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('EDUCATION', 'Tuition, books, courses, student loans', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('SAVINGS', 'Emergency fund, retirement, investments', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DEBT', 'Credit cards, loans, other debt payments', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('INSURANCE', 'Life insurance, disability insurance, other coverage', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('PERSONAL_CARE', 'Haircuts, cosmetics, gym memberships', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('TRAVEL', 'Vacations, business trips, weekend getaways', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('GIFTS', 'Birthdays, holidays, special occasions', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('OTHER', 'Miscellaneous expenses not covered above', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample income types with descriptions
INSERT INTO income_types (name, description, created_at, updated_at)
VALUES 
    ('SALARY', 'Regular employment income', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('BONUS', 'Performance bonuses, annual bonuses', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('INVESTMENT', 'Dividends, interest, capital gains', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('RENTAL', 'Income from property rentals', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('FREELANCE', 'Contract work, consulting, gig economy', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('OTHER', 'Other sources of income', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP); 