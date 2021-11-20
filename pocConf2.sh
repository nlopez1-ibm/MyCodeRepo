#! /bin/sh  
# prepare zAppBuild and initialize IDz userBuild Files  
#######################################################
cd $HOME ; clear ;  zApp=$HOME/poc-zAppBuild 
echo "\n*** Setting up DBB Samples in -> $zApp \n"

samples=$HOME/dbb-samples/Build/zAppBuild/
if  test ! -d "$samples"; then
	clear ;     echo "ERROR: $samples not found. \n Review (Prepare the Groovy Samples on USS) step in POC-Cookbook." ;
	            exit 12  
fi

t=$zApp/.gitattributes 
if  test -f "$t"; then
	clear ;     echo "ERROR: $zApp  exists.  Manually delete folders starting poc-*  and rerun this script." ;
	            echo " ***  Be careful all your customized properties will be lost"  ;
	            exit 12  
fi
mkdir -p $zApp ; mkdir -p  $HOME/poc-sandbox  ;  mkdir -p $HOME/poc-IDz-logs

cp -r   $samples  $zApp
cd $zApp

chtag -tc iso8859-1 .gitattributes; 
chtag -tc iso8859-1 ./utilities/ADMIN.pw
echo "*.md zos-working-tree-encoding=ISO8859-1 git-encoding=utf-8" >> .gitattributes
echo "*.pw zos-working-tree-encoding=ISO8859-1 git-encoding=utf-8" >> .gitattributes    
for file in $(find . -name "*.md" -print); do; chtag -tc iso8859-1  $file; done
for file in $(find . -name "*.groovy" -print); do
	 iconv -f iso8859-1 -t ibm1047 $file > $file.z; mv $file.z $file 
done
for file in $(find . -name "*.properties" -print); do
	 iconv -f iso8859-1 -t ibm1047 $file > $file.z; mv $file.z $file 
done

git init ; git add . 2>$HOME/z.log ; git commit -m "added zAppBuild and converted tags" >>$HOME/z.log

ls -Ta .
echo "\n\nDone.  \nFollow the instructions in the 'PoC cookbook' to configure the zAppBuild properties."
