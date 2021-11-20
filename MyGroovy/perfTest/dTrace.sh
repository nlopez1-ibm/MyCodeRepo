# Simulate a groovyz trace point (njl 1-2020) 
# Env Var DBBMon="Y"

set -x
if test "$DBB_MON" ; then
	if [ ! -d $DBB_MON ]; then mkdir $DBB_MON ; ls $DBB_MON ; fi
	echo $dlog  $(date +"%Y-%m-%d %I:%M %p")   "PS .s.s.s.s.    " >>$DBB_MON"/dbbmon.log"
fi   