@echo off
setlocal
pushd "%~dp0"
for /r %%f in (*.class) do del /f /q "%%f"
popd
echo Classes supprimees.
