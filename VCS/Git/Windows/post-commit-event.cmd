@echo off

:: read commit hook stdin data e.g. "aa45321… 68f7abf… refs/heads/master"
set /p OLDREV_NEWREV_REFNAME=

echo Directory of this script is %~dp0
echo Repository root is %CD%

set OLDREV=%OLDREV_NEWREV_REFNAME:~0,40%
set NEWREV=%OLDREV_NEWREV_REFNAME:~41,40%
set REFNAME=%OLDREV_NEWREV_REFNAME:~82,999%

for /f "tokens=4* delims=\ " %%a in ("%CD%") do set BRANCH=%%a&set REPOPATH=%%b
set REPOPATH=%REPOPATH:\=/%

echo Branch is %BRANCH%
echo Repo root path is %REPOPATH%

set TEMPFILE=%NEWREV%temp.txt
git show --pretty="format:" --name-only %NEWREV% > %TEMPFILE%

set /a c=0
setlocal ENABLEDELAYEDEXPANSION
FOR /F "eol=; delims=  " %%i in (%TEMPFILE%) do (
	
	set /a c=c+1
	set PUSHFILE=%NEWREV%!c!.txt
	echo branch=%BRANCH% > !PUSHFILE!
	echo path=%REPOPATH%/%%i >> !PUSHFILE!
	copy !PUSHFILE! \\<server>\pushfiles\!PUSHFILE!
	del !PUSHFILE!	
)
endlocal
del %TEMPFILE%




