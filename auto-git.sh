#!/bin/bash

# =============================================================================
# Auto-Git Commit Script
# =============================================================================
# This script automatically commits changes to the Git repository every 60
# seconds. It creates a continuous backup of work without manual intervention.
#
# Usage:
#   ./auto-git.sh              # Run in foreground
#   ./auto-git.sh &            # Run in background
#   nohup ./auto-git.sh &      # Run persistently
#
# The script will:
# 1. Initialize a Git repository if one doesn't exist
# 2. Loop continuously, checking for changes every 60 seconds
# 3. Automatically commit any changes with a timestamp
# 4. Log all activity for debugging
#
# Author: Kilo Companion Team
# Version: 1.0.0
# =============================================================================

# Configuration
COMMIT_INTERVAL=60  # Seconds between checks
LOG_FILE="auto-git.log"
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Colors for terminal output (if supported)
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# =============================================================================
# Helper Functions
# =============================================================================

log_message() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo "[$timestamp] $1" | tee -a "$LOG_FILE"
}

print_status() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# =============================================================================
# Git Initialization
# =============================================================================

initialize_git() {
    cd "$PROJECT_DIR" || exit 1
    
    if [ ! -d ".git" ]; then
        print_status "Git repository not found. Initializing..."
        git init
        
        # Create initial .gitignore if it doesn't exist
        if [ ! -f ".gitignore" ]; then
            print_status "Creating default .gitignore..."
            cat > .gitignore << 'EOF'
# Android/Gradle
*.iml
.gradle
/local.properties
/.idea/caches
/.idea/libraries
/.idea/modules.xml
/.idea/workspace.xml
/.idea/navEditor.xml
/.idea/assetWizardSettings.xml
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties

# Auto-git log
auto-git.log

# IDE
.vscode/
*.swp
*.swo
*~

# OS
.DS_Store
Thumbs.db
EOF
        fi
        
        git add .
        git commit -m "Initial commit: Auto-git initialization"
        print_status "Git repository initialized successfully!"
    else
        print_status "Git repository already initialized."
    fi
}

# =============================================================================
# Main Commit Loop
# =============================================================================

run_commit_loop() {
    cd "$PROJECT_DIR" || exit 1
    
    print_status "Starting auto-git commit loop..."
    print_status "Checking for changes every $COMMIT_INTERVAL seconds"
    print_status "Press Ctrl+C to stop"
    print_status "Log file: $LOG_FILE"
    echo ""
    
    # Track consecutive empty commits to avoid log spam
    local empty_count=0
    local max_empty_logs=5
    
    while true; do
        # Check if there are any changes to commit
        if [ -n "$(git status --porcelain)" ]; then
            # Changes detected
            local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
            local files_changed=$(git status --short | wc -l)
            
            git add .
            
            # Attempt commit with error handling
            if git commit -m "Auto-commit: $timestamp"; then
                print_status "Committed $files_changed file(s) at $timestamp"
                log_message "SUCCESS: Committed $files_changed file(s)"
                empty_count=0
            else
                print_error "Failed to commit changes"
                log_message "ERROR: Git commit failed"
            fi
        else
            # No changes
            ((empty_count++))
            if [ $empty_count -le $max_empty_logs ]; then
                print_status "No changes to commit. Sleeping..."
            elif [ $empty_count -eq $((max_empty_logs + 1)) ]; then
                print_status "No changes detected (suppressing further 'no change' messages)..."
            fi
        fi
        
        # Wait before next check
        sleep $COMMIT_INTERVAL
    done
}

# =============================================================================
# Cleanup Handler
# =============================================================================

cleanup() {
    echo ""
    print_status "Auto-git stopped. Final status:"
    git status --short
    log_message "Auto-git session ended"
    exit 0
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

# =============================================================================
# Script Entry Point
# =============================================================================

main() {
    echo "========================================"
    echo "  Auto-Git Commit Script v1.0.0"
    echo "========================================"
    echo ""
    
    # Verify git is installed
    if ! command -v git &> /dev/null; then
        print_error "Git is not installed. Please install Git first."
        exit 1
    fi
    
    # Initialize git if needed
    initialize_git
    
    # Start the commit loop
    run_commit_loop
}

# Run main function
main "$@"
