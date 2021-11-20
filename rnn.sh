for f in *; do
    cp -- "$f" "$(basename -- "$f" ).cbl"
done