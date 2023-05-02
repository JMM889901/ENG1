# Windows only, double click python file to run.
import subprocess
import os
import time
import pyautogui

#subprocess.Popen([r'set CLASSPATH="core\lib\junit-jupiter-api-5.9.2.jar" && .\gradlew.bat', "run"])

def subprocess_cmd():
    subprocess.Popen([r".\gradlew.bat", "test"])
    time.sleep(5)

    # Change over to new window via Windows hotkey.
    pyautogui.hotkey("alt", "shift", "tab")


def os_cmd():
    os.system(r'set CLASSPATH="core\lib\junit-jupiter-api-5.9.2.jar" && .\gradlew.bat test')


if __name__ == '__main__':
    #subprocess_cmd()
    os_cmd()
