# User Guide

## First Launch
1. Grant storage permissions
2. The app creates `Documents/KiloWorkspace`

## Features

### Home Tab
Shows workspace status and file existence.

### Config Tab
Edit `opencode.json` configuration files.

### WebView Tab
Access local CLI web interfaces at `http://127.0.0.1:3000`

## OAuth Flow
1. CLI initiates OAuth
2. Browser opens
3. Kilo Companion intercepts redirect
4. Token saved to `auth.json`

## File Locations
- Config: `Documents/KiloWorkspace/.config/kilo/opencode.json`
- Auth: `Documents/KiloWorkspace/auth.json`
