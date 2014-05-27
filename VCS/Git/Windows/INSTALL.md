<b>INSTALL</b><br/>
To use this hook copy the two files:
- post-commit-event.cmd
- post-receive

to the desired Git repository in the .git/hooks folder.<br/>
<br/>
<b>CONFIGURE</b><br/>
Then in post-commit-event.cmd change the &lt;server&gt; in line 30 to point to your local Push Server install machine( IP or DNS )<br/>
If your Push Server file interface share ( see Push Server installation instructions ) is different than 'pushfiles', then you must change the 'pushfiles' entry in line 30 of the post-commit-event.cmd as well.<br/>
<br/>
If you want to ci-push on another event than 'git push' you can rename the post-receive file accordingly, see <a href="http://git-scm.com/book/en/Customizing-Git-Git-Hooks">Customizing-Git-Git-Hooks</a><br/>
<br/>
This hook uses the file interface on the Push Server.</br>
