@echo off
set DIR=%~dp0
"%DIR%runtime\bin\java.exe" -cp "%DIR%build\classes\java\main;%DIR%build\resources\main" dev.joshuatic.avidcareerproject.Main
pause