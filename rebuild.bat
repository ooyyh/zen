@echo off
chcp 65001
cd /d D:\Project\IdeaProjects\zen
echo 清理旧文件...
if exist target rmdir /s /q target
echo 重新编译（使用UTF-8编码）...
mvn clean compile -DskipTests -Dfile.encoding=UTF-8
echo 完成！
pause
