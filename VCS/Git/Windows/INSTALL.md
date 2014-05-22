<b>INSTALL</b><br/>
To use this hook copy the two files:
- post-commit-event.cmd
- post-receive
to the desired Git repository in the .git/hooks folder
<br/>
<b>CONFIGURE</b><br/>
Then in post-commit-event.cmd change the &lt;server&gt; in line 30 to point to your local server install ( IP or DNS ) 
If your Push Server file interface share ( see Server/INSTALL.md ) is different than 'pushfiles', then you must change the 'pushfiles' entry in line 30 of the post-commit-event.cmd as well.

If you want to ci-push on another event than 'git push' you can rename the post-receive file accordingly, see http://git-scm.com/book/en/Customizing-Git-Git-Hooks 

This hook uses the file interface on the Push Server.
