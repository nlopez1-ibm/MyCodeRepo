#! /bin/sh  
cd $HOME; clear; 
echo "***" echo "Migrating MVS PDS to USS" ; echo "***" 
groovy -v ;  java -version ;  git --version ;  echo "***\n" 

w=$HOME/poc-workspace/poc-application/ ; mkdir -p $w ; cd $w
m=$DBB_HOME/migration/bin/migrate.sh

## Provide an MVS PDS HLQ and LLQ with a member name pattern using an asterisk 
## The source will be copied to the targetDir subfolder in $w     
hlq="your-mvs.pds-hlq"
hlq="NLOPEZ.DAT"

llqmem="cobol(???mem*)"
llqmem="cobol(EPS*)"  
$m -r $w -m MappingRule[hlq:$hlq,toLower:true,pdsMapping:false,targetDir:cobol,extension:CBL]     $llqmem   
$m -r $w -m MappingRule[hlq:$hlq,toLower:true,pdsMapping:false,targetDir:copybook,extension:CPY]  $llqmem 
 
## Wrap up 
cp -r $HOME/poc-zAppBuild/application/application-conf  $w
cp $HOME/poc-zAppBuild/.gitattributes  $HOME/poc-workspace/