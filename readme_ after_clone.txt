Upon initial cloning of this repository, if you plan to utilize the built-in IntelliJ project, do the following:

(it is assumed git is already installed)
- open a command line at the IntelliJ project root
- execute "./after_clone.sh <user name> <user email>" where
	<user name> = the user name to display in Bitbucket
	<user email> = your MMH email address

- to verify the user data, execute: "git show-user", it should display:
<your Display User Name>
<your MMH Email Address>