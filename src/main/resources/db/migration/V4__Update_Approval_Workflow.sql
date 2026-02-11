-- Migration for approval workflow
-- Update existing status values to match enum

-- Update existing status values
UPDATE purchase_requisitions SET status = 'DRAFT' WHERE status = 'draft' OR status = 'Draft';
UPDATE purchase_requisitions SET status = 'SUBMITTED' WHERE status = 'submitted' OR status = 'Submitted';
UPDATE purchase_requisitions SET status = 'APPROVED' WHERE status = 'approved' OR status = 'Approved';
UPDATE purchase_requisitions SET status = 'REJECTED' WHERE status = 'rejected' OR status = 'Rejected';

-- Set default status for any null values
UPDATE purchase_requisitions SET status = 'DRAFT' WHERE status IS NULL;

-- Add constraint to ensure only valid status values
ALTER TABLE purchase_requisitions ADD CONSTRAINT chk_status 
CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED'));

-- Add index for status queries
CREATE INDEX idx_purchase_requisitions_status ON purchase_requisitions(status);

-- Add index for approval history queries
CREATE INDEX idx_approval_history_requisition_id ON approval_history(requisition_id);
CREATE INDEX idx_approval_history_action_at ON approval_history(action_at);