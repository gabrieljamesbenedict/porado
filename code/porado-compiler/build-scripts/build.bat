cd ..
call mvn clean install

cd compiler-builds
rmdir /s /q "porado"
del "porado.rar"

jpackage ^
  --input ../target/ ^
  --dest ./ ^
  --name porado ^
  --main-jar porado-compiler-1.jar ^
  --main-class com.gabrieljamesbenedict.Porado^
  --type app-image ^
  --win-console

rar a -r "porado.rar" "porado"

pause