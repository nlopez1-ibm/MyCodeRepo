hopelesly complex... no engough time fyal

s='this is a csv  123 line of text.csv for @TESTING REGEX !'


// ==~ is a find return bool
def r  = s =~ /^(?=.*\bcsv\b)(?:\S+ ){1}(\S+)/
println r.value 


// =~ is a match 
//r = s =~ 'REGEX'
//println "`text` is in str =$r "
// ?println r.value

// match end of str
r = s =~ /^.*\.csv$/
//println r.count
//println "cnt of matches on .csv = $r"

// A working sample
//def s="838123 someWord\n8 someWord\n12 someWord"
//def rx = /(\d+)\s*someWord/
//def res = s =~ rx
//(0..<res.count).each { println res[it][1] }