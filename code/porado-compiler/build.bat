rmdir /s /q "compiler-builds/porado"

jpackage ^
  --input ./target/ ^
  --dest ./compiler-builds/ ^
  --name porado ^
  --main-jar porado-compiler-1.jar ^
  --main-class com.gabrieljamesbenedict.PoradoCompiler ^
  --type app-image ^
  --win-console

pause