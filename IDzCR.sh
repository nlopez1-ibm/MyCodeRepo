#!/bin/sh
 #############################################################################
 # #
 # Developer for z/OS 5724-T07 #
 # #
 # Copyright IBM Corp. 2013 All rights reserved. #
 # All rights reserved. US Government Users Restricted Rights - #
 # Use, duplication or disclosure restricted by GSA ADP Schedule Contract #
 # with IBM Corp. #
 # #
 #############################################################################
 
 
 usage()
 {
  echo
  echo "zcodereview -workspace ws_location -rulefile rule_file -pds dsn"
  echo " -exportdirectory export_dir"
  echo " [-members member_list] [-extensionmap extmap_path]"
  echo " [-customrules custrules_path] [-propertygrp propgrp_path]"
  echo
  echo " ws_location The workspace location"
  echo " rule_file The file containing exported rules to be executed"
  echo " dsn Source PDS"
  echo " member_list Comma-separated member names"
  echo " extmap_path Path to exported extension mapping .zip file"
  echo " custrules_path Path to exported custom rules .ccr file"
  echo " propgrp_path Path to exported property group .xml file"
  echo " export_dir The target directory for generated reports"
  return
 }
 
 
 # set the path to the CR install directory and the JRE install directory
 JAVA_HOME="/usr/lpp/java/J8.0_64"
  base_dir="/usr/lpp/IBM/idzuti/cr" 
 
 # supply defaults if desired
 workspace=
 rulefile=
 pds=
 members=
 extensionmap=
 customrules=
 propertygrp=
 exportdirectory=
 
 
 # process parameters
 while [ $# -gt 0 ]
 do
  case "$1" in
  -workspace ) workspace=$2; shift 2;;
  -rulefile ) rulefile=$2; shift 2;;
  -pds ) pds=$2; shift 2;;
  -members ) members=$2; shift 2;;
  -extensionmap ) extensionmap=$2; shift 2;;
  -customrules ) customrules=$2; shift 2;;
  -propertygrp ) propertygrp=$2; shift 2;;
  -exportdirectory ) exportdirectory=$2; shift 2;;
  # unknown parameter
  * ) echo "Ignoring parameter: $1"; shift 1;;
  esac
 done
 
 
 # change delimiters to support paths with spaces
 OIFS="$IFS"
 IFS='|
 '
 
 
 # workspace, pds, rulefile, exportdirectory are mandatory
 if [ "$workspace" = "" ]
 then
  echo "-workspace parameter is mandatory"; usage
 elif [ "$pds" = "" ]
 then
  echo "-pds parameter is mandatory"; usage
 elif [ "$rulefile" = "" ]
 then
  echo "-rulefile parameter is mandatory"; usage
 elif [ "$exportdirectory" = "" ]
 then
  echo "-exportdirectory parameter is mandatory"; usage
 else
  # construct parameters and command
  ap_parm="|-application|com.ibm.rsar.analysis.codereview.rdz.zos.AnalyzeApplication"
  ws_parm="|-data|$workspace"
  pds_parm="|-pds|$pds"
  rule_parm="|-rulefile|$rulefile"
  exp_parm="|-exportdirectory|$exportdirectory"
  if [ "$members" != "" ]
  then
  mem_parm="|-members|$members"
  fi
  if [ "$extensionmap" != "" ]
  then
  extmap_parm="|-extensionmap|$extensionmap"
  fi
  if [ "$customrules" != "" ]
  then
  customrules_parm="|-customrules|$customrules"
  fi
  if [ "$propertygrp" != "" ]
  then
  propertygrp_parm="|-propertygrp|$propertygrp"
  fi
  command_start="$JAVA_HOME/bin/java|-Dosgi.configuration.area='Config'|-Dosgi.locking=none|-Dosgi.parentClassloader=ext|-cp|$base_dir/plugins/
launcher.jar|org.eclipse.core.launcher.Main|-os|zos|-ws|motif|-arch|s390"
  command="$command_start$ap_parm$ws_parm$rule_parm$exp_parm$pds_parm$mem_parm$extmap_parm$customrules_parm$propertygrp_parm|-
verbose|-nosplash"
 
 
 
 
 
 
  # run command
  echo "Running software analysis..."
  echo $command
  $command
 fi
 
 
 IFS="$OIFS"