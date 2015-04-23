while true
do
	date +"%T"
	python server/json_parser.py
	CHANGES=$(git status | grep modified: | awk '{split($0,a,"/"); print a[2]}' | sed 's/\.json//' | tr '\n' ' ')
	git add --all
	sudo git commit -m "Update: $CHANGES"
	git push
	sleep 1800 #30 minutes
done