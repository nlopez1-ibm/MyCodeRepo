#! /bin/sh                                                                      
## kickoff a Groovyz process  from  a SSH script 

export _BPX_SHAREAS=NO ; export _BPXK_AUTOCVT=ON 
export _CEE_RUNOPTS="FILETAG(AUTOCVT,AUTOTAG) POSIX(ON)" ; export _TAG_REDIR_ERR=txt ; export _TAG_REDIR_IN=txt ; export _TAG_REDIR_OUT=txt 
export JAVA_HOME=/usr/lpp/java/J8.0_64 ; export DBB_HOME=/var/dbb100FIX/usr/lpp/IBM/dbb ; export GROOVY_HOME=$DBB_HOME/groovy-2.4.12   
export GIT_SHELL=/var/rocket/bin/bash ; export GIT_EXEC_PATH=/var/rocket/libexec/git-core ; export GIT_Path=/var/rocket/bin ; export PERL5LIB=/var/rocket/share/perl/5.24.1:$PERL5LIB ; export GIT_TEMPLATE_DIR=/var/rocket/share/git-core/templates 
export PATH=$JAVA_HOME/bin:$GROOVY_HOME/bin:$DBB_HOME/bin:$GIT_Path:$PATH 
## 
## ZOA
export ZOAU_ROOT=/u/nlopez/IBM/zoa
export CLASSPATH=$ZOAU_ROOT/lib/*:$CLASSPATH
export LIBPATH=$ZOAU_ROOT/lib:$LIBPATH 
export PATH=$ZOAU_ROOT/bin:$PATH
##echo "ZOUA Activated."

## SDSF Java API - note that the isfjcall is alread in the system profile  
export CLASSPATH=/usr/include/java_classes/isfjcall.jar:$CLASSPATH
export LIBPATH=/usr/lib/java_runtime:$LIBPATH
###

cd /u/nlopez/MyCodeRepo/MyGroovy/perfTest
groovyz USSMon.groovy "$@"
