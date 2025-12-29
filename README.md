# resqfood-backend

Members:
  - Samip Shrestha
  - Yannick Wuyten
  - Tomas Vaneynde
  - Thomas Bosmans

## Important!!!
The app uses Firebase for notifications. A dummy config is provided so the app starts correctly. If the console logs a Firebase warning, the app is running in 'Offline-Notification' mode, and all other features (Household, Profile) will work perfectly.

To make everything work flawlesly:
rename backend/ucll/src/main/resources/serviceAccountKey.json.example -> serviceAccountKey.json
