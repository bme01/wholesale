SCRIPT_PATH_DIR="/home/stuproj/cs4224v/ysql_project_final/wholesale/"
SCRIPT_NAME="wholesale-1.0-SNAPSHOT-jar-with-dependencies.jar"
XACT_FILE_DIR="/temp/cs4224v/project_files/xact_files/"

run() {
  if [[ "$2" == "0" ]]; then
    for cid in 0 5 10 15
      do
        echo "Executing Client $cid.txt"
        nohup java -jar $SCRIPT_PATH_DIR$SCRIPT_NAME "$1" $cid < $XACT_FILE_DIR$cid.txt > output$cid.txt &
      done
  elif [[ "$2" == "1" ]]; then
    for cid in 1 6 11 16
      do
        echo "Executing Client $cid.txt"
        nohup java -jar $SCRIPT_PATH_DIR$SCRIPT_NAME "$1" $cid < $XACT_FILE_DIR$cid.txt > output$cid.txt &
      done
  elif [[ "$2" == "2" ]]; then
    for cid in 2 7 12 17
      do
          echo "Executing Client $cid.txt"
          nohup java -jar $SCRIPT_PATH_DIR$SCRIPT_NAME "$1" $cid < $XACT_FILE_DIR$cid.txt > output$cid.txt &
    done
  elif [[ "$2" == "3" ]]; then
    for cid in 3 8 13 18
      do
          echo "Executing Client $cid.txt"
          nohup java -jar $SCRIPT_PATH_DIR$SCRIPT_NAME "$1" $cid < $XACT_FILE_DIR$cid.txt > output$cid.txt &
    done
  elif [[ "$2" == "4" ]]; then
    for cid in 4 9 14 19
      do
          echo "Executing Client $cid.txt"
          nohup java -jar $SCRIPT_PATH_DIR$SCRIPT_NAME "$1" $cid < $XACT_FILE_DIR$cid.txt > output$cid.txt 2>> clients.csv &
      done
  else
    echo "No such server"
  fi
}

report() {
    java -jar $SCRIPT_PATH_DIR$SCRIPT_NAME "$1" > troughput.csv 2> dbstate.csv
    sed -i '2,$d'  troughput.csv
}

getclient(){
  for cid in {0..19} 
    do
      echo -n "$cid," >> clients.csv
      tail -n 9 output$cid.txt | head -1 >> clients.csv
    done
}

if [[ "$1" == "run" ]]; then
  run "$1" "$2"
elif [[ "$1" == "report" ]]; then
  report "$1"
elif [[ "$1" == "getclient" ]]; then
  getclient 
else
  echo "unknown command"
fi

