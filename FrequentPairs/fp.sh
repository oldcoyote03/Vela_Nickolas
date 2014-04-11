
javac -classpath $HADOOP_PREFIX/hadoop-core-$HADOOP_VERSION.jar -d whvisit_classes *.java
jar -cvf ./freqpair.jar -C whvisit_classes .
hadoop jar $HOME/whvisit/freqpair.jar org.myorg.FrequentPairs /user/hduser/FrequentPairs/whvisits /user/hduser/FrequentPairs/temp /user/hduser/FrequentPairs/output
fs -cat /user/hduser/FrequentPairs/temp/part-r-00000
fs -cat /user/hduser/FrequentPairs/output/part-r-00000

rm freqpair.jar FrequentPairs.java PairSort.java CountSort.java

rm -r whvisit_classes/org
fs -rmr FrequentPairs/temp FrequentPairs/output
sudo cp -p FrequentPairs.java PairSort.java CountSort.java /home/hduser/whvisit
su hduser
