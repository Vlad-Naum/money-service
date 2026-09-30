ALTER TABLE money_costs DROP CONSTRAINT money_costs_category_check;

ALTER TABLE money_costs ALTER COLUMN category TYPE varchar(32)
    USING CASE category
        WHEN 0 THEN 'SUPERMARKETS'
        WHEN 1 THEN 'AUTO'
        WHEN 2 THEN 'TAXI'
        WHEN 3 THEN 'MARKETPLACE'
        WHEN 4 THEN 'CLOTHING'
        WHEN 5 THEN 'RESTAURANTS'
        WHEN 6 THEN 'BEAUTY'
        WHEN 7 THEN 'ENTERTAINMENT'
        WHEN 8 THEN 'OTHER'
        -- ... по одной строке на каждую константу
    END;

ALTER TABLE money_costs ADD CONSTRAINT ck_money_costs_category
    CHECK (category IN ('SUPERMARKETS', 'AUTO', 'TAXI', 'MARKETPLACE', 'CLOTHING', 'RESTAURANTS', 'BEAUTY', 'ENTERTAINMENT', 'OTHER'));
