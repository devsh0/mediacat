@echo off
set PROJECT_DIR=%1
set JAR_NAME=%2
set MVN_TARGET=%PROJECT_DIR%\target
set JLINK_OUT=%PROJECT_DIR%\..\custom-jre

cd %PROJECT_DIR%
call mvn -Dmaven.test.skip=true clean dependency:copy-dependencies package
rmdir %JLINK_OUT% /s /q

jlink -p %MVN_TARGET%\dependency;"%JAVA_HOME%"\jdeps;%MVN_TARGET%\%JAR_NAME% --add-modules org.jsoup,org.mediacat --output %JLINK_OUT% --strip-debug --no-man-pages --no-header-files
xcopy /s "%MVN_TARGET%\configs" "%JLINK_OUT%\configs"
copy %MVN_TARGET%\%JAR_NAME% %JLINK_OUT%\%JAR_NAME%
copy %PROJECT_DIR%\mcat.bat %JLINK_OUT%\mcat.bat
echo Done!