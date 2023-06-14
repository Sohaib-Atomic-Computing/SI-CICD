ALTER TABLE promotions
    RENAME COLUMN merchant TO merchant_id;

ALTER TABLE promotions
    DROP CONSTRAINT FK_promotions_merchants;

ALTER TABLE promotions
ADD CONSTRAINT FK_promotions_merchants FOREIGN KEY (merchant_id) REFERENCES merchants(id);
