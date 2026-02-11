# Push to New GitHub Repository

## Step 1: Create New Repository on GitHub
1. Go to https://github.com/new
2. Enter repository name (e.g., "vendor-procurement-system")
3. Choose Public or Private
4. DO NOT initialize with README, .gitignore, or license
5. Click "Create repository"
6. Copy the repository URL (e.g., https://github.com/yourusername/vendor-procurement-system.git)

## Step 2: Run These Commands

Open terminal in project root and run:

```bash
# Initialize git
git init

# Add all files
git add .

# Create initial commit
git commit -m "Initial commit: Vendor Procurement Management System with PR/PO workflow, analytics, and history tracking"

# Add remote (replace with your repository URL)
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## Step 3: Verify
Go to your GitHub repository URL and refresh - you should see all files uploaded.

## Alternative: Using GitHub Desktop
1. Download GitHub Desktop: https://desktop.github.com/
2. Open GitHub Desktop
3. File → Add Local Repository
4. Select your project folder
5. Click "Publish repository"
6. Choose name and visibility
7. Click "Publish"

Done! ✅
