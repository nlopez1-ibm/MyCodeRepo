# run a process x times to simulate load for dbbmon
echo " Running 3  DBB Builds start: " date 
echo " ________________________________" 

#/u/nlopez/Garanti-workspace/build.sh 
#/u/nlopez/Garanti-workspace/build.sh  
#/u/nlopez/Garanti-workspace/build.sh  

zlsof pipe
for number in {1..1500}
do
	groovyz reader.groovy
	zlsof pipe
	sleep 3
done

 
echo " ________________________ END " date  
