def line, noOfLines = 0
File file1 = new File('df.txt')
file1.withReader { reader ->	while ((line = reader.readLine()) != null) {noOfLines++	}}

File file2 = new File('df.txt')
file2.withReader { reader ->	while ((line = reader.readLine()) != null) {noOfLines++	}}

File file3 = new File('df.txt')
file3.withReader { reader ->	while ((line = reader.readLine()) != null) {noOfLines++	}}

println 'Reader Test Code: Read file df.txt 3 times'