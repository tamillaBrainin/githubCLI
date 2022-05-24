# githubCLI
GitHub CLI that will get information from the GitHub repository, create a report and save it locally.
The githubCLI commands are:
- downloads: will fetch the # of downloads of the entire versions + distributions (if
exists)
- The description of the argument should be “Present the entire
downloads for each asset”
- Usage :
githubCLI downloads -r "owner/repo" -o “output path.txt”
- This command reads all the repository’s releases and presents in a table
the Release name, the asset name and the download count.
For example for the whitesource/log4j-detect-distribution , the following
api call,
https://api.github.com/repos/whitesource/log4j-detect-distribution/releases
And in case of the log4j-detect-v1.5.0 release and the
log4j-detect-1.5.0-darwin-amd64.tar.gz distribution we will add the table
row
