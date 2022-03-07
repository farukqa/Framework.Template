# adds an additional gitconfig file
git config --local include.path ../.gitconfig
# enables ignoring of select project files
git ignore-proj-files
# defines local user info
git set-user $1
git set-email $2