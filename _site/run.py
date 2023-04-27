# Windows only, double click python file to run.
import subprocess
import time
import pyautogui

subprocess.Popen([r".\gradlew.bat", "run"])

time.sleep(5)

# Change over to new window via Windows hotkey.
pyautogui.hotkey("alt", "shift", "tab")
