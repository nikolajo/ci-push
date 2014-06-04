@echo off

:: read commit hook stdin data e.g. "aa45321… 68f7abf… refs/heads/master"
set /p OLDREV_NEWREV_REFNAME=

echo Directory of this script is %~dp0
echo Repository root is %CD%

set OLDREV=%OLDREV_NEWREV_REFNAME:~0,40%
set NEWREV=%OLDREV_NEWREV_REFNAME:~41,40%
set REFNAME=%OLDREV_NEWREV_REFNAME:~82,999%

:: get branch name
for /f "tokens=3* delims=/ " %%a in ("%REFNAME%") do set BRANCH=%%a
echo Branch is %BRANCH%

:: get root repo path
set CURRENT_DIR_NO_GIT_EXT=%CD%
set CURRENT_DIR_NO_GIT_EXT=%CURRENT_DIR_NO_GIT_EXT:\.git=%
for %%* in (%CURRENT_DIR_NO_GIT_EXT%) do set REPOPATH=%%~n*
set REPOPATH=%REPOPATH:\=/%
echo Repo root path is %REPOPATH%

:: get committed files
set TEMPFILE=%NEWREV%temp.txt
git show --pretty="format:" --name-only %NEWREV% > %TEMPFILE%

:: create and write new files and copy them to the push trigger server share
set /a c=0
setlocal ENABLEDELAYEDEXPANSION
FOR /F "eol=; delims=  " %%i in (%TEMPFILE%) do (
	
	set /a c=c+1
	set PUSHFILE=%NEWREV%!c!.txt
	echo branch=%BRANCH% > !PUSHFILE!
	echo path=%REPOPATH%/%%i >> !PUSHFILE!
	copy !PUSHFILE! \\<IP/DNS of Push Server>\pushfiles\!PUSHFILE!
	del !PUSHFILE!	
)
endlocal
del %TEMPFILE%




