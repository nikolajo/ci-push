To use this hook copy the two files:
- post-commit-event.cmd
- post-receive
to the desired Git repository in the .git/hooks folder

Then in post-commit-event.cmd change the <server> in line 30 to point to your local server install ( IP or DNS ) 

If you want to ci-push on another event than git push you can rename the post-receive file accordingly, see http://git-scm.com/book/en/Customizing-Git-Git-Hooks 
