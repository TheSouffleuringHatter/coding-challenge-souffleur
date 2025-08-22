Set WshShell = CreateObject("WScript.Shell")
WshShell.Run "cmd.exe /c gradlew.bat run", 0, False
