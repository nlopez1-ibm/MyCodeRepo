# Monitor a groovy process
# Env zDT on Laptop P52,I7 no other workload - steady state  
# pass the pgm in arg 1
# hard wired arg 1 for testing 
# See https://www.ibm.com/support/knowledgecenter/en/SS7K4U_8.5.5/com.ibm.websphere.zseries.doc/ae/tprf_tunejvm_v61.html
#     https://www.ibm.com/support/knowledgecenter/SSYKE2_8.0.0/openj9/xshareclasses/index.html
###############
clear 

# define test senarios (s)  


## s1   avg-ish timing: sys-7  real-13 
jo='-Xshareclasses:none ' 

## s2 avg-ish timing: sys-3.5  real-8.5 
jo='-Xshareclasses -Xquickstart '


## s3 avg-ish timing: real=?   user=?  sys=?   

jo='-Xshareclasses -Xtune:virt   -Xverify:none   -Xms256m  -Xmx256m -Xgcpolicy:optthruput'
jo='-Xshareclasses               -Xverify:none   -Xms512m  -Xmx512m -Xgcpolicy:optthruput -Xnoclassgc -Xshareclasses:cacheDir=$HOME/tmp'
jo='-Xshareclasses:none          -Xverify:none   -Xms512m  -Xmx512m -Xgcpolicy:optthruput -Xnoclassgc '


## s2 avg-ish timing: sys-3.5  real-8.5 
jo='-Xshareclasses -Xquickstart '
 

###---------------------
export JAVA_OPTS=$jo  


## run 3 times 
time groovy reader.groovy
time groovy reader.groovy
time groovy reader.groovy

echo ""  
echo " Senario  JAVAOPTS:" $JAVA_OPTS


# time Notes: 
#    Real= is wall clock time
#    User= is the amount of CPU time spent in user-mode code (outside the kernel) within the process. 
#          This is only actual CPU time used in executing the process. 
#          Other processes and time the process spends blocked do not count towards this figure.
#    Sys=  Is the amount of CPU time spent in the kernel within the process ( (also known as 'supervisor' mode)).

