# Coding_Challenge_1

# Features
1. Application has 4 views:
● View 1: Splash screen.
● View 2: A greeting view to the user, asking name and email if this information is not
available already in application. Email format should be validated.
● View 3: Another view with the same greeting but using the given user information.
Additionally, this view shows the user's current location on Google maps.
● View 4: About view (version number, author).
● Nav: Navigation menu for navigation between the views. Navigation elements:
○ Home page - View 2 or View 3 (depending on the case)
○ About - View 4
○ Logout - Visible if the user
Error situations to handle:
● Invalid email address
● No internet connection
Both error should display a top banner. Upon click or if waiting 3 seconds, the banner will be
dismissed.
Picture 1 and Picture 2 show the flows between these views in two different cases:
● CASE 1 - When user data is available in the application
● CASE 2 - When user data is not available in the application
For each view, consider the related PSD for pixel perfect implementation.

2.Intent handling for application URL, for opening the greeting view via intent URI. Purpose is to
open the application from an email link.

3. Application should be integrated with Google Analytics and report basic metrics to GA. Pick up
those you think more relevant. At least these should be added:
● statistics from showing greetings
● statistics from application launches via link


