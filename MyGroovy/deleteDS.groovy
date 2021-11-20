import com.ibm.jzos.ZFile
def hlq = "DBRUCE.BETATEST"
def segments = ["BMS","COBOL","COPYBOOK","DBRM","LINK","LOAD","MFS","OBJ","TFORMAT"]
segments.each { segment ->
   def pds = "'${hlq}.${segment}'"
   if (ZFile.dsExists(pds))
      ZFile.remove("//$pds")
}