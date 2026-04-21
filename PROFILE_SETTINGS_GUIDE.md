# Profile & Settings Pages - Quick Start Guide

## 🎯 Accessing the Pages

### Profile Page
- **Route:** `/profile`
- **Access:** Requires authentication (AuthGuard)
- **Navigation:** Click on "Mon Profil" in the main navigation or use `/profile` URL

### Settings Page
- **Route:** `/settings`
- **Access:** Requires authentication (AuthGuard)
- **Navigation:** Click on "Paramètres" in the main navigation or use `/settings` URL

## 📖 Features Guide

### Profile Page (`/profile`)

#### View Mode
1. **User Information Display**
   - Avatar with user's first letter
   - Full name
   - Email
   - Phone number
   - User role (Demandeur, Technicien, Admin)

2. **Edit Button**
   - Click "Modifier" to enter edit mode
   - Make changes to name and/or phone number
   - Form validates in real-time

#### Edit Mode
1. **Edit Form**
   - **Name field:** Required, minimum 2 characters
   - **Phone field:** Optional, must match phone format
   - Shows validation errors below each field

2. **Action Buttons**
   - **Enregistrer:** Save changes (disabled if form is invalid)
   - **Annuler:** Discard changes and return to view mode

3. **Settings Link**
   - Quick access to Settings page from profile

### Settings Page (`/settings`)

#### Section 1: User Profile Card
- Displays user avatar and basic information
- "Modifier le profil" button redirects to Profile page
- Quick access to profile editing

#### Section 2: Security
1. **Change Password**
   - Click "Changer" button to reveal password form
   - Form fields:
     - Old Password (required, min 6 chars)
     - New Password (required, min 8 chars)
     - Confirm Password (must match)
   - Click "Enregistrer" to save or "Annuler" to close form
   - Password validation errors show below each field

2. **Two-Factor Authentication** (Coming Soon)
   - Button disabled, to be implemented

3. **Active Sessions** (Coming Soon)
   - Button disabled, to be implemented

#### Section 3: Preferences
1. **Email Notifications**
   - Toggle to receive/disable email notifications

2. **Dark Theme**
   - Toggle to switch between light and dark themes

3. **Language**
   - Language selection dropdown

#### Section 4: Danger Zone (Red Section)
1. **Logout**
   - Click to disconnect immediately
   - Shows success notification
   - Redirects to home page after 500ms

2. **Delete Account**
   - Click to delete your account permanently
   - Double confirmation required
   - Action is irreversible
   - All user data will be deleted

## 🔒 Security Notes

- All sensitive operations require authentication
- Password changes require old password verification
- Account deletion requires double confirmation
- Passwords are encrypted before transmission
- Sessions are JWT-based for security

## 📱 Responsive Design

Both pages are fully responsive:
- **Desktop:** Full width cards with optimal spacing
- **Tablet:** Adjusted layout for medium screens
- **Mobile:** Single column layout, full-width inputs

## ⚠️ Error Handling

The pages handle various error scenarios:
- **Network errors:** User-friendly error messages
- **Validation errors:** Real-time field validation with feedback
- **Authentication errors:** Auto-redirect to login if session expires
- **Server errors:** Error notifications with descriptions

## 🔄 User Experience Tips

1. **Profile Updates:**
   - Changes are saved immediately upon clicking "Enregistrer"
   - Confirmation notification appears

2. **Password Change:**
   - Minimum 8 characters required
   - Must contain confirmation to ensure accuracy
   - Logged out after successful password change

3. **Account Deletion:**
   - Double confirmation prevents accidental deletion
   - User is logged out after deletion
   - Redirected to home page

## 🛠️ API Integration

### Profile Page API Calls
- `GET /api/users/me` - Load profile
- `PUT /api/users/me` - Update profile

### Settings Page API Calls
- `GET /api/users/me` - Load user data
- `POST /api/users/change-password` - Change password
- `DELETE /api/users/delete-account` - Delete account

## 💡 Troubleshooting

### Profile not loading
- Check if you're logged in
- Verify AuthGuard is working
- Check browser console for errors

### Password change fails
- Ensure old password is correct
- New password must be at least 8 characters
- Confirm password must match new password

### Cannot delete account
- Account deletion requires double confirmation
- Ensure you're logged in
- Check if account has dependencies (e.g., open tickets)

## 🎨 Styling

### Color Scheme
- **Primary:** #007bff (Blue) - Buttons and links
- **Success:** #28a745 (Green) - Save buttons
- **Danger:** #dc3545 (Red) - Delete buttons
- **Gray:** #6c757d (Neutral) - Cancel buttons

### Components
- Modern cards with shadows
- Smooth transitions (0.3s ease)
- Responsive grid layout
- Touch-friendly button sizes (48px minimum)

## 📞 Support

For issues or feature requests:
1. Check the browser console for errors
2. Verify API endpoints are accessible
3. Ensure JWT token is valid
4. Check network requests in DevTools

---

**Last Updated:** 2026-04-20
**Version:** 1.0.0

