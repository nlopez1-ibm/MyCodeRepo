import com.ibm.jzos.FileFactory
import com.ibm.jzos.ZFile

// Dumpt the system default file encoding for MVS files 
println "System Default code page=" + FileFactory.getDefaultZFileEncoding()
println "locate " + Zile.locateDSN('nlopez.dat.jcl') 
