tsocmd xmit "(TVT6031.nlopez) dsn(gm.tformat) outdsn(test2)"


this works too

if (props.MFSBuild) {
		
		
		def xcmd='tsocmd xmit ($HOSTNAME.$USER) dsn(${props.mfs_TFORMAT}) outdsn(${props.mfs_TFORMAT.xmit})'
		println "** Post-Processing MFS TFORMAT PDS cmd=$xcmd"
		def proc = xcmd.execute()
		proc.waitFor()

		println "Process exit code: ${proc.exitValue()}"
		println "Std Err: ${proc.err.text}"
		println "Std Out: ${proc.in.text}"
		if (!proc.exitValue()) {
				new CopyToHFS().dataset("USR1.BUILD.SYSPRINT").member("HELLO").file(new File("/u/usr1/build/helloworld.log")).execute()
		}
	}

