-- Performance optimization indexes for vendor search API
-- Add these indexes to improve query performance

-- Index for rating filter (most common filter)
CREATE INDEX idx_vendors_rating ON vendors(rating);

-- Index for location filter (text search)
CREATE INDEX idx_vendors_location ON vendors(location);

-- Index for category filter (exact match)
CREATE INDEX idx_vendors_category ON vendors(category);

-- Index for compliance filter
CREATE INDEX idx_vendors_compliance ON vendors(compliance);

-- Composite index for common filter combinations
CREATE INDEX idx_vendors_rating_location ON vendors(rating, location);
CREATE INDEX idx_vendors_category_compliance ON vendors(category, compliance);

-- Index for sorting by created_at
CREATE INDEX idx_vendors_created_at ON vendors(created_at);

-- Index for sorting by name
CREATE INDEX idx_vendors_name ON vendors(name);