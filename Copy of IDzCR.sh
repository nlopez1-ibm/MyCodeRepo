no laucnher !!! 


testing remote diff v2




#!/bin/sh
 #############################################################################
 # 
 # IBM Developer for z/OS 5724-T07 
 #  
 # Copyright IBM Corp. 2021 All rights reserved. 
 # All rights reserved. US Government Users Restricted Rights - 
 # Use, duplication or disclosure restricted by GSA ADP Schedule Contract 
 # with IBM Corp.
 #############################################################################

base_dir="/usr/lpp/IBM/idzuti/cr" 
ap_parm=" -application com.ibm.rsar.analysis.codereview.rdz.zos.AnalyzeApplication"
ws_parm=" -data /u/nlopez/tmp"
pds_parm=" "
rule_parm=" -rulefile /u/nlopez/dbb-zappbuild/build-conf/codeReview.properties"
exp_parm=" -exportdirectory /u/nlopez/tmp"
mem_parm=" -members test1,test2"
 
 
command_start="java -Dosgi.configuration.area='Config' -Dosgi.locking=none -Dosgi.parentClassloader=ext -cp $base_dir/plugins/ launcher.jar org.eclipse.core.launcher.Main -os zos -ws motif -arch s390"

#command_start="$JAVA_HOME/bin/java|-Dosgi.configuration.area='Config'|-Dosgi.locking=none|-Dosgi.parentClassloader=ext|-cp|$base_dir/plugins/
#launcher.jar|org.eclipse.core.launcher.Main|-os|zos|-ws|motif|-arch|s390"


command="$command_start$ap_parm$ws_parm$rule_parm$exp_parm$pds_parm$mem_parm$extmap_parm$customrules_parm$propertygrp_parm|-
verbose|-nosplash"
 
 
 
  



 
command="$command_start$ap_parm$ws_parm$rule_parm$exp_parm$pds_parm$mem_parm$extmap_parm$customrules_parm$propertygrp_parm|-
verbose|-nosplash"
 
 
 
 
 
 
# run command
echo "Running software analysis..."
echo $command
$command
