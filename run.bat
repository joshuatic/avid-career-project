@echo off
set DIR=%~dp0

"%DIR%runtime\bin\java.exe" ^
--module-path "%DIR%runtime\lib" ^
--add-modules javafx.controls ^
-cp "%DIR%build\classes\java\main;%DIR%build\resources\main;%DIR%build\libs\*" ^
dev.joshuatic.avidcareerproject.Main

pause