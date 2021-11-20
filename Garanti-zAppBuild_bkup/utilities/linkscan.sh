clear
echo "#########"
echo "#####   Testing DBB v106  Link Module Scanner"
echo "scanning ...MAIN module's DLLs "   NLOPEZ.GAR.ORT.QQ.P092259.LOAD  QQ1C0021
/u/nlopez/IBM/dbb/lib/bgzlkeps  NLOPEZ.GAR.ORT.QQ.P092259.LOAD  QQ1C0021

echo "-------------";echo "**" ;echo "**"
echo "scanning ...   the DLL included in MAIN "   NLOPEZ.GAR.ORT.QQ.P092259.LOAD IQQ1A031
/u/nlopez/IBM/dbb/lib/bgzlkeps  NLOPEZ.GAR.ORT.QQ.P092259.LOAD  IQQ1A031


echo "-------------";echo "**" ;echo "**"
echo "scanning ...   NCAL  included in MAIN "   NLOPEZ.GAR.ORT.QQ.P092259.NCAL.LOAD CQQ1A121
/u/nlopez/IBM/dbb/lib/bgzlkeps  NLOPEZ.GAR.ORT.QQ.P092259.NCAL.LOAD  CQQ1A121

cd ut	
echo "-------------";echo "**" ;echo "**"
echo "scanning ...   CONTROL TEST  "    NLOPEZ.PFG.TEST.LOAD  EPSMLIS 
/u/nlopez/IBM/dbb/lib/bgzlkeps   NLOPEZ.PFG.TEST.LOAD EPSMLIS