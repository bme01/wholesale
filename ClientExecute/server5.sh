SCRIPT_PATH_DIR="/home/stuproj/cs4224v/ysql_project/"
SCRIPT_NAME="wholesale-1.0-SNAPSHOT.jar"
XACT_FILE_DIR="/temp/cs4224v/project_files/xact_files/"
# XACT_FILE_DIR="/temp/cs4224v/project_files/"
for cid in 4 9 14 19
do
    echo "Excuting Client $cid.txt"
    nohup java -jar $SCRIPT_PATH_DIR$SCRIPT_NAME < $XACT_FILE_DIR$cid.txt > result$cid.txt &
done
