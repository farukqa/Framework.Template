Upon initial cloning of this repository, if you plan to utilize the built-in IntelliJ project, do the following:
(it is assumed git is already installed)

1) Click "Terminal" to open the Terminal window in IntelliJ (bottom of IntelliJ window above status bar)

2) Execute "./after_clone.sh <user name> <user email>" where
	<user name> = the user name to display. Note: this is only used as a display name for the local repo, it is not associated with your user name in other applications.
	<user email> = your MMH email address

3) To verify the user data, execute: "git show-user", it should display:
<your Display User Name>
<your MMH Email Address>