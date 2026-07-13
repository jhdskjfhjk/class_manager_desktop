@echo off
chcp 65001 >nul
cd /d "%~dp0"

set OUT=out\production\class_manager_desktop
set MAIN=LauncherApp

if not exist "%OUT%\%MAIN%.class" (
    echo [启动器] 未检测到编译产物，开始编译...
    if not exist "%OUT%" mkdir "%OUT%"
    javac -encoding UTF-8 -d "%OUT%" -sourcepath src src\%MAIN%.java
    if errorlevel 1 (
        echo [启动器] 编译失败，请检查 JDK 是否安装且在 PATH 中。
        pause
        exit /b 1
    )
)

echo [启动器] 启动 GUI...
java -cp "%OUT%" %MAIN%
