# TIME A GROOVY startup
# run . loadtest.sh 2>&1 > log to capture out 
# jvm doc https://www.ibm.com/docs/en/sdk-java-technology/8?topic=options-xcheck
# sc can be created uses and mod'd 
# sc ref https://www.ibm.com/docs/en/sdk-java-technology/8?topic=sharing-introduction#best-practices-for-using-xshareclasses
#        https://www.ibm.com/docs/en/sdk-java-technology/8?topic=sharing-introduction#enabling-class-data-sharing    
clear


## play with the cache Default size = 300m, soft limit 64M  - non-persist (z) limit z/OS®: The amount of swap space available to the system.


# delete the current cache 
export JAVA_OPTS="-Xshareclasses:cacheDir=/u/nlopez/javaSC_2 -Xshareclasses:name=DBB   -Xshareclasses:destroyAll"
##groovy --version

# create new cache 
export JAVA_OPTS="-Xshareclasses:cacheDir=/u/nlopez/javaSC_2 -Xshareclasses:name=DBB -Xscmx32m -Xscmaxaot14m   -Xscmaxjitdata24m  -Xshareclasses:allowClasspaths    -Xshareclasses:disableBCI  -Xscdmx1k -Xjit  -Xnoaot -Xquickstart   -XcompilationThreads7"
##groovyz /u/nlopez/dbb-zappbuild/build.groovy -help 

# rpt before first use  
export JAVA_OPTS=" -Xshareclasses:name=DBB -Xshareclasses:printStats"
#groovy --version


# first use sampe  job 
export JAVA_OPTS=" -Xshareclasses:name=DBB -Xjit  -Xnoaot -Xquickstart   -XcompilationThreads7"
time groovy  reader.groovy

## RPT
export JAVA_OPTS=" -Xshareclasses:name=DBB -Xshareclasses:printStats"
groovy --version















export JAVA_OPTS="-Xquickstart -Xshareclasses -Xshareclasses:cacheDir=$HOME/javaSC -Xverify:none -Xgcpolicy:optthruput -Xnoclassgc "  
export JAVA_OPTS="-Dverbose:class"
export JAVA_OPTS="-Xnojit  -Xnoaot -verbose:class"



#avg real - run 1 14/s
#avg real - run 2 
export JAVA_OPTS="-Xquickstart -Xshareclasses -Xshareclasses:cacheDir=$HOME/javaSC -Xverify:none -Xgcpolicy:optthruput -Xnoclassgc "


### new cycle testing 

# time  user 1.2/M  - 57/s  
export JAVA_OPTS="-Xjit  -Xnoaot "

# time user 40/s - 43/s     
export JAVA_OPTS="-Xjit  -Xnoaot -Xquickstart   "

#best     time user ~37 -  47
export JAVA_OPTS="-Xjit  -Xnoaot -Xquickstart   -XcompilationThreads7 "


#++Better    time user init = 39/s,   2nd run=24s    best time=20  holy crap dow to 1.3/s???
#export JAVA_OPTS="-Xshareclasses     -Xshareclasses:verbose -Xscmaxjitdata999m -Xscmaxjitdata24m   -Xshareclasses:adjustsoftmx=24m"










# mod the cache sz 
export JAVA_OPTS="-Xshareclasses -Xshareclasses:cacheDir=/u/nlopez/javaSC2    -Xshareclasses:verbose -Xscmaxaot14m   -Xscmaxjitdata24m   "

# force a nop run
#groovy --version 


#echo $JAVA_OPTS
#time groovy  reader.groovy 
	